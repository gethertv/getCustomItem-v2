package dev.gether.getcustomitem.item;

import dev.gether.getconfig.utils.ItemUtil;
import dev.gether.getcustomitem.file.FileManager;
import dev.gether.getcustomitem.file.config.Config;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Optional;

public class ItemManager {

    private final FileManager fileManager;
    public ItemManager(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    public Optional<CustomItem> findCustomItemByType(ItemType itemType, ItemStack itemStack) {
        return fileManager.getCustomItems().stream()
                .filter(item -> item.getItemType() == itemType)
                .filter(item -> ItemUtil.sameItemName(item.getItemStack(), itemStack)).findFirst();
    }

    public Optional<CustomItem> findCustomItemByKey(String key) {
        return fileManager.getCustomItems().stream()
                .filter(item -> item.getKey().equalsIgnoreCase(key)).findFirst();
    }

//    public Optional<CustomItem> findCustomItemByType(ItemType itemType) {
//        return fileManager.getCustomItems().stream()
//                .filter(item -> item.getItemType() == itemType)
//                .findFirst();
//    }

    public List<CustomItem> findAllCustomItemByType(ItemType itemType) {
        return fileManager.getCustomItems().stream()
                .filter(item -> item.getItemType() == itemType).toList();
    }

    public SuggestionResult getAllItemKey() {
        return fileManager.getCustomItems().stream()
                .map(CustomItem::getKey).collect(SuggestionResult.collector());
    }

    public void initItems() {
        fileManager.getCustomItems().forEach(CustomItem::init);
    }

    public boolean isCustomItem(ItemStack itemStack) {
        return fileManager.getCustomItems().stream()
                .anyMatch(item -> ItemUtil.sameItemName(item.getItemStack(), itemStack));
    }


    /*
    public Optional<ItemStack> findItemStackByType(ItemType itemType) {
        return config.getCustomItems().stream()
                .filter(item -> item.getItemType() == itemType)
                .map(item -> item.getItem().getItemStack()).findFirst();
    }
    */




}
