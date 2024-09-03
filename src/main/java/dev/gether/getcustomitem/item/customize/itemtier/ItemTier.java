package dev.gether.getcustomitem.item.customize.itemtier;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;
import dev.gether.getconfig.domain.Item;
import dev.gether.getconfig.domain.config.sound.SoundConfig;
import dev.gether.getconfig.utils.ColorFixer;
import dev.gether.getconfig.utils.IntegerToRoman;
import dev.gether.getconfig.utils.ItemUtil;
import dev.gether.getcustomitem.GetCustomItem;
import dev.gether.getcustomitem.item.CustomItem;
import dev.gether.getcustomitem.item.ItemType;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

@Getter
@Setter
@JsonTypeName("item_tier")
public class ItemTier extends CustomItem {

    private static final String ITEM_TIER_KEY = "itemtier";
    private static final String ITEM_TIER_PROGRESS_KEY = "itemtier-progress";

    private NamespacedKey ITEM_KEY;
    private NamespacedKey ITEM_TIER;
    private NamespacedKey ITEM_TIER_PROGRESS;
    private Map<Integer, TierData> tierData;
    private Set<EquipmentSlot> equipmentSlots;

    @JsonIgnore
    private ItemStack itemTier;
    @JsonIgnore
    private Random random;
    private String charProgress;
    private int lengthProgress;
    private String successColor;
    private String failureColor;

    public ItemTier() {
        super();
    }

    public ItemTier(String key, String categoryName, int usage, Item item, ItemType itemType,
                    int cooldown, String permissionBypass, SoundConfig soundConfig,
                    Set<EquipmentSlot> equipmentSlots, Map<Integer, TierData> tierData,
                    String charProgress, int lengthProgress, String successColor, String failureColor) {
        super(key, categoryName, usage, item, itemType, cooldown, permissionBypass, soundConfig, null, null, null, null);
        this.itemTier = item.getItemStack();
        this.tierData = tierData;
        this.equipmentSlots = equipmentSlots;
        this.charProgress = charProgress;
        this.lengthProgress = lengthProgress;
        this.successColor = successColor;
        this.failureColor = failureColor;
    }

    @Override
    protected Map<String, String> replacementValues() {
        return Collections.emptyMap();
    }

    @Override
    public void init() {
        super.init();
        this.random = new Random();
        initNamespacedKeys();
        initItemTier();
        initTierData();
    }

    private void initNamespacedKeys() {
        this.ITEM_KEY = new NamespacedKey(GetCustomItem.getInstance(), getKey());
        this.ITEM_TIER = new NamespacedKey(GetCustomItem.getInstance(), ITEM_TIER_KEY);
        this.ITEM_TIER_PROGRESS = new NamespacedKey(GetCustomItem.getInstance(), ITEM_TIER_PROGRESS_KEY);
    }

    private void initItemTier() {
        itemTier = super.getItemStack();
        ItemMeta itemMeta = itemTier.getItemMeta();
        if (itemMeta != null) {
            itemMeta.getPersistentDataContainer().set(ITEM_KEY, PersistentDataType.BYTE, (byte)1);
            itemMeta.getPersistentDataContainer().set(ITEM_TIER, PersistentDataType.INTEGER, 0);
            itemMeta.getPersistentDataContainer().set(ITEM_TIER_PROGRESS, PersistentDataType.DOUBLE, 0.0);
            itemTier.setItemMeta(itemMeta);
        }
    }

    private void initTierData() {
        tierData.values().forEach(tier -> tier.init(ITEM_TIER, ITEM_TIER_PROGRESS));
    }

    @JsonIgnore
    public Optional<Integer> getLevel(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        return itemMeta != null ? Optional.ofNullable(itemMeta.getPersistentDataContainer().get(ITEM_TIER, PersistentDataType.INTEGER)) : Optional.empty();
    }

    @JsonIgnore
    public boolean isItemTier(ItemStack item) {
        if(item == null) return false;
        if(!item.hasItemMeta()) return false;

        return item.getItemMeta().getPersistentDataContainer().has(ITEM_KEY, PersistentDataType.BYTE);

    }

    @JsonIgnore
    public ItemStack getItemStack() {
        ItemStack clone = itemTier.clone();
        ItemMeta itemMeta = clone.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setCustomModelData(random.nextInt());
            clone.setItemMeta(itemMeta);
        }
        updateItem(clone);
        return clone;
    }

    private String getProgress(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return null;

        Optional<Integer> levelOptional = getLevel(itemStack);
        if(levelOptional.isEmpty())
            return "";

        int level = levelOptional.get() + 1;
        TierData tierData = this.tierData.get(level);
        if(tierData==null) return "";

        Double progress = itemMeta.getPersistentDataContainer().get(ITEM_TIER_PROGRESS, PersistentDataType.DOUBLE);
        double result = progress / tierData.getRequirementValue();

        int successCount = (int) (result * lengthProgress);
        int failureCount = lengthProgress - successCount;

        StringBuilder progressBar = new StringBuilder();

        for (int i = 0; i < successCount; i++) {
            progressBar.append(successColor).append(charProgress);
        }

        for (int i = 0; i < failureCount; i++) {
            progressBar.append(failureColor).append(charProgress);
        }

        return progressBar.toString();
    }

    @Override
    public void updateItem(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return;

        Integer usage = itemMeta.getPersistentDataContainer().get(namespacedKey, PersistentDataType.INTEGER);
        if (usage == null) return;

        Optional<Integer> levelOptional = getLevel(itemStack);
        if (levelOptional.isEmpty()) return;

        int level = levelOptional.get();
        TierData nextTierData = this.tierData.get(level + 1);
        List<String> lore = getLoreForLevel(level);

        if (lore.isEmpty()) return;

        Double hasAmount = itemMeta.getPersistentDataContainer().get(ITEM_TIER_PROGRESS, PersistentDataType.DOUBLE);
        lore = updateLore(lore, usage, level, hasAmount, (nextTierData != null ?  nextTierData.getRequirementValue() : 0d), getProgress(itemStack));

        itemMeta.setLore(ColorFixer.addColors(lore));
        itemStack.setItemMeta(itemMeta);
    }

    private List<String> getLoreForLevel(int level) {
        if (level == 0) {
            return new ArrayList<>(super.getItem().getLore());
        } else {
            TierData tierData = this.tierData.get(level);
            return new ArrayList<>(tierData.getItem().getLore());
        }
    }

    private List<String> updateLore(List<String> lore, int usage, int level, Double hasAmount, double needAmount, String progress) {
        return new ArrayList<>(
                lore.stream()
                        .map(line -> line.replace("{requirement-int}", String.format(Locale.US, "%d", hasAmount.intValue()))
                                .replace("{requirement-double}", String.format(Locale.US, "%.2f", hasAmount))
                                .replace("{need-int}", String.format(Locale.US, "%d", (int) needAmount))
                                .replace("{need-double}", String.format(Locale.US, "%.2f", needAmount))
                                .replace("{progress}", progress)
                                .replace("{percent}", String.format(Locale.US, "%.2f", (hasAmount/needAmount)*100))
                                .replace("{level}", String.valueOf(level))
                                .replace("{level-increase}", String.valueOf(level + 1))
                                .replace("{level-decrease}", String.valueOf(level - 1))
                                .replace("{level-roman}", IntegerToRoman.intToRoman(level))
                                .replace("{level-roman-increase}", IntegerToRoman.intToRoman(level + 1))
                                .replace("{level-roman-decrease}", IntegerToRoman.intToRoman(level - 1)))
                        .toList()
        );
    }

    public void action(Player player, ActionEvent actionEvent, Object type, int amount, ItemStack itemStack) {
        Optional<Integer> levelOptional = getLevel(itemStack);
        if (levelOptional.isEmpty()) return;

        int level = levelOptional.get() + 1;
        TierData tierData = this.tierData.get(level);
        if (tierData == null) return;

        Map<Object, Double> objectDoubleMap = tierData.getActionEvents().get(actionEvent);
        if (objectDoubleMap == null) return;

        Double amountToAdd = objectDoubleMap.get(type.toString());
        if (amountToAdd == null) return;

        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return;

        Double actuallyProgress = itemMeta.getPersistentDataContainer().get(ITEM_TIER_PROGRESS, PersistentDataType.DOUBLE);
        if (actuallyProgress == null) {
            actuallyProgress = 0.0;
        }
        actuallyProgress += (amountToAdd * amount);

        if (actuallyProgress > tierData.getRequirementValue()) {
            double rest = actuallyProgress - tierData.getRequirementValue();
            upgradeItem(player, rest, level, itemStack);
        } else {
            itemMeta.getPersistentDataContainer().set(ITEM_TIER_PROGRESS, PersistentDataType.DOUBLE, actuallyProgress);
            itemStack.setItemMeta(itemMeta);
            updateItem(itemStack);
        }
    }

    private void upgradeItem(Player player, double rest, int level, ItemStack itemStack) {
        Integer usage = itemStack.getItemMeta().getPersistentDataContainer().get(namespacedKey, PersistentDataType.INTEGER);
        int slot = ItemUtil.removeItem(player, itemStack);
        TierData tierDataItem = this.tierData.get(level);
        ItemStack item = tierDataItem.getItemStack().clone();
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta != null) {
            itemMeta.getPersistentDataContainer().set(ITEM_KEY, PersistentDataType.BYTE, (byte) 1);
            itemMeta.getPersistentDataContainer().set(ITEM_TIER_PROGRESS, PersistentDataType.DOUBLE, rest);
            itemMeta.getPersistentDataContainer().set(ITEM_TIER, PersistentDataType.INTEGER, level);
            itemMeta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.INTEGER, usage);
            item.setItemMeta(itemMeta);
        }
        updateItem(item);

        player.getInventory().setItem(slot, item);
    }

    public boolean isMaxLevel(ItemStack itemStack) {
        Optional<Integer> level = getLevel(itemStack);
        if (level.isEmpty())
            return true;
        Integer lvl = level.get();
        TierData tierData = this.tierData.get(Integer.valueOf(lvl.intValue() + 1));
        if (tierData == null)
            return true;
        return false;
    }
}