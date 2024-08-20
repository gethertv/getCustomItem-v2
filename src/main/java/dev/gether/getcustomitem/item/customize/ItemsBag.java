package dev.gether.getcustomitem.item.customize;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;
import dev.gether.getconfig.domain.Item;
import dev.gether.getconfig.domain.config.ItemInventory;
import dev.gether.getconfig.domain.config.sound.SoundConfig;
import dev.gether.getcustomitem.GetCustomItem;
import dev.gether.getcustomitem.item.CustomItem;
import dev.gether.getcustomitem.item.ItemType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeName("items_bag")
public class ItemsBag extends CustomItem {

    private int size;
    private String title;
    public NamespacedKey ITEMSBAG_KEY;
    public NamespacedKey ITEMSBAG_UUID_KEY;
    private ItemInventory itemInventoryTakeAll;

    @JsonIgnore
    private ItemStack itemBag;
    @JsonIgnore
    private ItemStack takeAllItemStack;
    @JsonIgnore
    private Random random;

    public ItemsBag(String key,
                    String categoryName,
                    int usage,
                    Item item,
                    ItemType itemType,
                    int cooldown,
                    String permissionBypass,
                    SoundConfig soundConfig,
                    int size,
                    String title,
                    ItemInventory itemInventoryTakeAll
    ) {

        super(key, categoryName, usage, item, itemType, cooldown,
                permissionBypass, soundConfig, null,
                null, null, null);

        this.size = size;
        this.title = title;
        this.itemInventoryTakeAll = itemInventoryTakeAll;
        this.takeAllItemStack = itemInventoryTakeAll.getItemStack();

    }

    @Override
    protected Map<String, String> replacementValues() {
        return Map.of();
    }

    @Override
    public void init() {
        super.init();
        this.random = new Random();
        this.takeAllItemStack = itemInventoryTakeAll.getItemStack();
        this.ITEMSBAG_KEY = new NamespacedKey(GetCustomItem.getInstance(), getKey());
        this.ITEMSBAG_UUID_KEY = new NamespacedKey(GetCustomItem.getInstance(), "itemsbag-uuid");
        // init items bag
        itemBag = getItemStack();
        ItemMeta itemMeta = itemBag.getItemMeta();
        itemMeta.getPersistentDataContainer().set(ITEMSBAG_KEY, PersistentDataType.STRING, getKey());
        itemBag.setItemMeta(itemMeta);
    }

    @JsonIgnore
    public UUID getBackpackUUID(ItemStack backpackItem) {
        if (!isBackpack(backpackItem)) {
            return null;
        }
        ItemMeta meta = backpackItem.getItemMeta();
        if (meta == null) {
            return null;
        }
        String uuidString = meta.getPersistentDataContainer().get(ITEMSBAG_UUID_KEY, PersistentDataType.STRING);
        return uuidString != null ? UUID.fromString(uuidString) : null;
    }

    @JsonIgnore
    public boolean isBackpack(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.getPersistentDataContainer().has(ITEMSBAG_KEY, PersistentDataType.STRING);
    }

    @JsonIgnore
    public ItemStack getItemBag() {
        ItemStack clone = itemBag.clone();
        ItemMeta itemMeta = clone.getItemMeta();
        itemMeta.setCustomModelData(random.nextInt());
        clone.setItemMeta(itemMeta);
        return clone;
    }


}
