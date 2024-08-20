package dev.gether.getcustomitem.item;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import dev.gether.getconfig.GetConfig;
import dev.gether.getconfig.domain.Item;
import dev.gether.getconfig.domain.config.TitleMessage;
import dev.gether.getconfig.domain.config.sound.SoundConfig;
import dev.gether.getconfig.utils.*;
import dev.gether.getcustomitem.GetCustomItem;
import dev.gether.getcustomitem.item.customize.*;
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
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
//@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
//        include = JsonTypeInfo.As.PROPERTY, property = "type")
//@JsonSubTypes({
//        @JsonSubTypes.Type(value = HookItem.class, name = "hook"),
//        @JsonSubTypes.Type(value = CrossBowItem.class, name = "crossbow"),
//        @JsonSubTypes.Type(value = CobwebGrenade.class, name = "cobweb_grenade"),
//        @JsonSubTypes.Type(value = EffectRadiusItem.class, name = "effect_radius"),
//        @JsonSubTypes.Type(value = FrozenSword.class, name = "frozen_sword"),
//        @JsonSubTypes.Type(value = AntyCobweb.class, name = "anty_cobweb"),
//        @JsonSubTypes.Type(value = BearFurItem.class, name = "bear_fur"),
//        @JsonSubTypes.Type(value = MagicTotemItem.class, name = "magic_totem"),
//        @JsonSubTypes.Type(value = HitEffectItem.class, name = "hit_effect"),
//        @JsonSubTypes.Type(value = SnowballTPItem.class, name = "snowball_tp"),
//        @JsonSubTypes.Type(value = InstaHealItem.class, name = "insta_heal"),
//        @JsonSubTypes.Type(value = PushItem.class, name = "push_item"),
//        @JsonSubTypes.Type(value = EggThrowItItem.class, name = "egg_throw_it"),
//        @JsonSubTypes.Type(value = ItemEffect.class, name = "item_effect"),
//        @JsonSubTypes.Type(value = LightningItem.class, name = "lighting_item"),
//        @JsonSubTypes.Type(value = ShieldItem.class, name = "shield_item"),
//        @JsonSubTypes.Type(value = ThrowingEnderPearlsItem.class, name = "throwing_ender_pearls"),
//        @JsonSubTypes.Type(value = ThrowUpItem.class, name = "throw_up"),
//        @JsonSubTypes.Type(value = CupidBowItem.class, name = "cubid_bow"),
//        @JsonSubTypes.Type(value = ShuffleInventoryItem.class, name = "shuffle_inv_item"),
//        @JsonSubTypes.Type(value = StopFlyingItem.class, name = "stop_flying"),
//})

public abstract class CustomItem extends GetConfig {
    @JsonIgnore
    protected NamespacedKey namespacedKey;
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

//    public CustomItem() {
//        this.namespacedKey = new NamespacedKey(GetCustomItem.getInstance(), key+"_usage");
//    }

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
        itemStack = item.getItemStack().clone();
        ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemMeta != null) {

            // set usage to persistent data
            itemMeta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.INTEGER, usage);

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
                    double distanceTraveled = entity.getLocation().distanceSquared(startLocation);
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
