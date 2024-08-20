package dev.gether.getcustomitem.item.manager.itembag;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.gether.getconfig.utils.ColorFixer;
import dev.gether.getcustomitem.GetCustomItem;
import dev.gether.getcustomitem.file.FileManager;
import dev.gether.getcustomitem.item.CustomItem;
import dev.gether.getcustomitem.item.ItemManager;
import dev.gether.getcustomitem.item.customize.ItemsBag;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ItemBagManager {
    private final FileManager fileManager;
    private final Map<UUID, ItemBagInventory> inventories;
    private final ItemBagService itemBagService;
    private final Random random = new Random();
    private final ItemManager itemManager;;
    public ItemBagManager(GetCustomItem plugin, FileManager fileManager, ItemBagService itemBagService, ItemManager itemManager) {
        this.fileManager = fileManager;
        this.inventories = new ConcurrentHashMap<>();
        this.itemBagService = itemBagService;
        this.itemManager = itemManager;

        loadAllInventories();

        // Schedule periodic saving
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::saveAllBackpacks, 6000L, 6000L); // Every 5 minutes
    }


    public List<ItemStack> addItemsToBackpack(UUID backpackUUID, List<ItemStack> items) {
        ItemBagInventory itemBagInventory = inventories.get(backpackUUID);
        if (itemBagInventory == null) {
            return new ArrayList<>(items);
        }
        List<ItemStack> notAddedItems = new ArrayList<>();
        for (ItemStack item : items) {
            if (item == null) continue;

            int amountToAdd = item.getAmount();
            ItemStack clone = item.clone();

            while (amountToAdd > 0) {
                int maxStackSize = clone.getMaxStackSize();
                clone.setAmount(Math.min(amountToAdd, maxStackSize));

                HashMap<Integer, ItemStack> notAdded = itemBagInventory.getInventory().addItem(clone);
                if (!notAdded.isEmpty()) {
                    notAddedItems.add(notAdded.get(0));
                    break;
                }

                amountToAdd -= clone.getAmount();
            }
        }

        return notAddedItems;
    }


    private void loadAllInventories() {
        Map<UUID, ItemBagDTO> loadedInventories = itemBagService.loadAllInventories();
        for (Map.Entry<UUID, ItemBagDTO> entry : loadedInventories.entrySet()) {
            ItemBagInventory itemBagInventory = new ItemBagInventory(
                    deserializeInventory(entry.getValue().inventory(), entry.getValue().key()),
                    entry.getValue().key()
            );
            if(itemBagInventory.getInventory() == null)
                continue;

            inventories.put(
                    entry.getKey(),
                    itemBagInventory
            );
        }
    }

    public void saveAllBackpacks() {
        Map<UUID, ItemBagDTO> serializedInventories = new HashMap<>();
        for (Map.Entry<UUID, ItemBagInventory> entry : inventories.entrySet()) {
            serializedInventories.put(entry.getKey(), serializeInventory(entry.getValue()));
        }
        itemBagService.batchUpdateInventories(serializedInventories);
    }

    private ItemBagDTO serializeInventory(ItemBagInventory itemBagInventory) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            dataOutput.writeInt(itemBagInventory.getInventory().getSize());

            for (int i = 0; i < itemBagInventory.getInventory().getSize(); i++) {
                dataOutput.writeObject(itemBagInventory.getInventory().getItem(i));
            }

            dataOutput.close();
            return new ItemBagDTO(Base64Coder.encodeLines(outputStream.toByteArray()), itemBagInventory.getKey());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save inventory", e);
        }
    }

    private Inventory deserializeInventory(String data, String key) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            Optional<CustomItem> customItemByKey = itemManager.findCustomItemByKey(key);
            if(customItemByKey.isEmpty() || !(customItemByKey.get() instanceof ItemsBag itemsBag))
                return null;

            Inventory inventory = Bukkit.createInventory(null, dataInput.readInt(), ColorFixer.addColors(itemsBag.getTitle()));

            for (int i = 0; i < inventory.getSize(); i++) {
                inventory.setItem(i, (ItemStack) dataInput.readObject());
            }

            dataInput.close();
            return inventory;
        } catch (ClassNotFoundException | IOException e) {
            throw new IllegalStateException("Unable to load inventory", e);
        }
    }

    @JsonIgnore
    public void openBackpack(Player player, ItemStack itemStack, ItemsBag itemsBag) {
        if (!itemsBag.isBackpack(itemStack)) return;

        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return;

        String uuidString = meta.getPersistentDataContainer().get(itemsBag.ITEMSBAG_UUID_KEY, PersistentDataType.STRING);
        UUID backpackUUID;

        if (uuidString == null) {
            backpackUUID = UUID.randomUUID();
            meta.getPersistentDataContainer().set(itemsBag.ITEMSBAG_UUID_KEY, PersistentDataType.STRING, backpackUUID.toString());
            itemStack.setItemMeta(meta);
        } else {
            backpackUUID = UUID.fromString(uuidString);
        }

        ItemBagInventory itemBagInventory = inventories.computeIfAbsent(backpackUUID, k ->
                new ItemBagInventory(
                        Bukkit.createInventory(null, itemsBag.getSize(), ColorFixer.addColors(itemsBag.getTitle())),
                        itemsBag.getKey()
                )
        );

        updateTakeAllButton(itemBagInventory.getInventory(), itemsBag);

        ItemBagInventoryHolder itemBagInventoryHolder = new ItemBagInventoryHolder(itemBagInventory, itemsBag);
        player.openInventory(itemBagInventoryHolder.getInventory());
    }

    public void updateTakeAllButton(Inventory inventory, ItemsBag itemsBag) {
        inventory.setItem(itemsBag.getItemInventoryTakeAll().getSlot(), itemsBag.getTakeAllItemStack());
    }

    public Map<UUID, ItemBagInventory> getInventories() {
        return inventories;
    }

}