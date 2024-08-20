package dev.gether.getcustomitem.listener.global;

import dev.gether.getcustomitem.item.ItemManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;

public class PrepareAnvilListener implements Listener {

    private final ItemManager itemManager;

    public PrepareAnvilListener(ItemManager itemManager) {
        this.itemManager = itemManager;
    }

    @EventHandler
    public void onAnvilPrepare(PrepareAnvilEvent event) {
        ItemStack firstItem = event.getInventory().getItem(0);
        ItemStack secondItem = event.getInventory().getItem(1);

        if (isProtectedItem(firstItem) || isProtectedItem(secondItem)) {
            event.setResult(null);
        }
    }

    private boolean isProtectedItem(ItemStack item) {
        if (item == null) return false;
        return itemManager.isCustomItem(item);
    }

}
