package dev.gether.getcustomitem.item.manager.itembag;

import dev.gether.getconfig.utils.ColorFixer;
import dev.gether.getconfig.utils.PlayerUtil;
import dev.gether.getcustomitem.item.customize.ItemsBag;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class ItemBagInventoryHolder implements InventoryHolder {
    private final Inventory inventory;
    @Getter
    private final String key;
    @Getter
    private final ItemsBag itemsBag;

    private final ItemBagInventory itemBagInventory;

    public ItemBagInventoryHolder(ItemBagInventory itemBagInventory, final ItemsBag itemsBag) {
        this.itemBagInventory = itemBagInventory;
        this.inventory = Bukkit.createInventory(
                this,
                itemsBag.getSize(),
                ColorFixer.addColors(itemsBag.getTitle())
        );
        this.inventory.setContents(itemBagInventory.getInventory().getContents());

        this.key = itemBagInventory.getKey().toString();
        this.itemsBag = itemsBag;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public void action(Player player, int clickedSlot, ItemStack clickedItem) {
        if (clickedSlot == 53) {
            handleTakeAllItems(player);
            return;
        }
        if(clickedItem == null)
            return;

        PlayerUtil.giveItem(player, clickedItem);
        inventory.setItem(clickedSlot, null);
        itemBagInventory.getInventory().setItem(clickedSlot, null);
        return;
    }


    private void handleTakeAllItems(Player player) {
        for (int i = 0; i < 53; i++) {
            ItemStack item = inventory.getItem(i);
            if (item != null && item.getType() != Material.AIR) {
                PlayerUtil.giveItem(player, item);
                inventory.setItem(i, null);
                itemBagInventory.getInventory().setItem(i, null);
            }
        }

    }
}
