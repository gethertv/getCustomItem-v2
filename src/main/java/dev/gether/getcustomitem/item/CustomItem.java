package dev.gether.getcustomitem.item;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.gether.getconfig.GetConfig;
import dev.gether.getconfig.domain.Item;
import dev.gether.getconfig.domain.config.TitleMessage;
import dev.gether.getconfig.domain.config.sound.SoundConfig;
import dev.gether.getconfig.utils.ColorFixer;
import dev.gether.getconfig.utils.ItemUtil;
import dev.gether.getconfig.utils.MessageUtil;
import dev.gether.getconfig.utils.PlayerUtil;
import dev.gether.getcustomitem.GetCustomItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class CustomItem extends GetConfig {
    @JsonIgnore
    protected NamespacedKey namespacedKey;
    @JsonIgnore
    private NamespacedKey itemKey;
    private boolean enabled = true;
    private String key;
    private String categoryName;
    protected int usage;
    private Item item;
    private ItemType itemType;
    private int cooldown; // time in seconds
    private String permissionBypass;
    private SoundConfig soundConfig;
    private List<String> notifyYourself;
    private List<String> notifyOpponents;
    private TitleMessage titleYourself;
    private TitleMessage titleOpponents;
    private boolean visualCooldown = false;
    @JsonIgnore
    private ItemStack itemStack;


    public CustomItem(String key,
                      String categoryName,
                      int usage,
                      Item item,
                      ItemType itemType,
                      int cooldown,
                      String permissionBypass,
                      SoundConfig soundConfig,
                      List<String> notifyYourself,
                      List<String> notifyOpponents,
                      TitleMessage titleYourself,
                      TitleMessage titleOpponents) {
        this.namespacedKey = new NamespacedKey(GetCustomItem.getInstance(), key+"_usage");
        this.itemKey = new NamespacedKey(GetCustomItem.getInstance(), key);
        this.key = key;
        this.categoryName = categoryName;
        this.usage = usage;
        this.item = item;
        this.itemType = itemType;
        this.cooldown = cooldown;
        this.permissionBypass = permissionBypass;
        this.soundConfig = soundConfig;
        this.notifyYourself = notifyYourself;
        this.notifyOpponents = notifyOpponents;
        this.titleYourself = titleYourself;
        this.titleOpponents = titleOpponents;
    }

    public void init() {
        this.namespacedKey = new NamespacedKey(GetCustomItem.getInstance(), key+"_usage");
        this.itemKey = new NamespacedKey(GetCustomItem.getInstance(), key);
        itemStack = item.getItemStack().clone();
        ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemMeta != null) {

            // set usage to persistent data
            itemMeta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.INTEGER, usage);
            itemMeta.getPersistentDataContainer().set(itemKey, PersistentDataType.STRING, getKey());

            List<String> lore = new ArrayList<>();
            if (itemMeta.hasLore())
                lore.addAll(itemMeta.getLore());

            // get replaced lore
            lore = getLore(lore, usage);

            itemMeta.setLore(ColorFixer.addColors(lore));
        }
        itemStack.setItemMeta(itemMeta);
    }

    public void playSound(Location location) {
        // check sound is enabled
        if(!soundConfig.isEnable())
            return;

        location.getWorld().playSound(location, soundConfig.getSound(), 1F, 1F);
    }

    @JsonIgnore
    public ItemStack getItemStack() {
        return itemStack;
    }

    public int getUsage(ItemMeta itemMeta) {
        if (itemMeta == null)
            return 1;

        Integer value = itemMeta.getPersistentDataContainer().get(namespacedKey, PersistentDataType.INTEGER);
        return value != null ? value : 0;
    }

    @JsonIgnore
    public void maxRangeTask(Entity entity, double maxRange) {
        final Location startLocation = entity.getLocation();
        new BukkitRunnable() {
            @Override
            public void run() {
                if (entity.isValid()) {
                    double distanceTraveled = entity.getLocation().distance(startLocation);
                    if(entity.isOnGround()) {
                        entity.remove();
                        this.cancel();
                    }
                    if (distanceTraveled >= maxRange) {
                        entity.remove();
                        this.cancel();
                    }
                } else {
                    this.cancel();
                }
            }
        }.runTaskTimer(GetCustomItem.getInstance(), 1L, 1L);
    }

    @JsonIgnore
    public boolean isCustomItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.getPersistentDataContainer().has(itemKey, PersistentDataType.STRING);
    }

    public void takeAmount(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null)
            return;

        Integer usage = itemMeta.getPersistentDataContainer().get(namespacedKey, PersistentDataType.INTEGER);
        if (usage == null)
            return;

        // ignore verify usage value because in other case im verify the number of usage
        // and if the number is lower than 1 im just remove it
        itemMeta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.INTEGER, usage - 1);
        itemStack.setItemMeta(itemMeta);

    }

    public void updateItem(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null)
            return;

        Integer usage = itemMeta.getPersistentDataContainer().get(namespacedKey, PersistentDataType.INTEGER);
        if (usage == null)
            return;

        // default original item
        ItemStack originalItem = item.getItemStack().clone();
        ItemMeta originalMeta = originalItem.getItemMeta();

        if (originalMeta == null || !originalMeta.hasLore())
            return;

        List<String> lore = new ArrayList<>(originalMeta.getLore());
        lore = getLore(lore, usage);

        itemMeta.setLore(ColorFixer.addColors(lore));
        itemStack.setItemMeta(itemMeta);
    }

    protected List<String> getLore(List<String> lore, int usage) {
        Map<String, String> values = new HashMap<>(replacementValues());
        values.put("{usage}", usage == -1 ? GetCustomItem.getInstance().getFileManager().getLangConfig().getNoLimit() : String.valueOf(usage));

        return new ArrayList<>(lore.stream()
                .map(line -> {
                    for (Map.Entry<String, String> entry : values.entrySet()) {
                        line = line.replace(entry.getKey(), entry.getValue());
                    }
                    return line;
                })
                .toList());
    }
    public void takeUsage(Player player, ItemStack itemStack, EquipmentSlot equipmentSlot) {
        int usage = getUsage(itemStack.getItemMeta());
        if(usage == -1)
            return;

        int amount = itemStack.getAmount();
        // check if items is stacked
        ItemStack remainingItem = null;
        if(amount > 1) {
            remainingItem = itemStack.clone();
            remainingItem.setAmount(amount - 1);

            itemStack.setAmount(1); // set original item amount to one
        }
        if(usage == 1) {
            if(equipmentSlot == EquipmentSlot.OFF_HAND) {
                player.getInventory().setItemInOffHand(null);
            }
            else {
                player.getInventory().setItem(equipmentSlot, null);
            }
        } else {
            takeAmount(itemStack);
            updateItem(itemStack);
        }

        // give remaining item after the update USAGE in main item, because if
        // I'll give faster than update, they again will be stacked
        if(remainingItem != null)
            PlayerUtil.giveItem(player, remainingItem); // give other item to inv
    }

    public void takeUsage(Player player) {
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        if(ItemUtil.sameItem(mainHand, getItemStack())) {
            takeUsage(player, mainHand, EquipmentSlot.HAND);
        } else {
            takeUsage(player, player.getInventory().getItemInOffHand(), EquipmentSlot.OFF_HAND);
        }
    }

    public void notifyYourself(Player player) {
        // send title
        MessageUtil.titleMessage(player, titleYourself);

        if(notifyYourself.isEmpty())
            return;

        MessageUtil.sendMessage(player, String.join("\n", notifyYourself));
    }

    public void notifyOpponents(Player player) {
        // send title
        MessageUtil.titleMessage(player, titleOpponents);

        if(notifyOpponents.isEmpty())
            return;

        MessageUtil.sendMessage(player, String.join("\n", notifyOpponents));
    }
    protected abstract Map<String, String> replacementValues();
}
