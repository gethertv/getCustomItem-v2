package dev.gether.getcustomitem.listener;

import dev.gether.getconfig.utils.MessageUtil;
import dev.gether.getconfig.utils.PlayerUtil;
import dev.gether.getcustomitem.cooldown.CooldownManager;
import dev.gether.getcustomitem.file.FileManager;
import dev.gether.getcustomitem.item.CustomItem;
import dev.gether.getcustomitem.item.ItemManager;
import dev.gether.getcustomitem.item.ItemType;
import dev.gether.getcustomitem.item.customize.DropToInventoryItem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DropToInventoryItemListener implements Listener {

    private final ItemManager itemManager;
    private final CooldownManager cooldownManager;
    private final FileManager fileManager;

    public DropToInventoryItemListener(ItemManager itemManager,
                                       CooldownManager cooldownManager,
                                       FileManager fileManager) {
        this.itemManager = itemManager;
        this.cooldownManager = cooldownManager;
        this.fileManager = fileManager;
    }

    /**
     * cancel place/use custom item
     *
     * @param event
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {

        Player player = event.getPlayer();
        ItemStack itemStack = event.getItem(); // using item

        if (itemStack == null)
            return;

        Optional<CustomItem> customItemByType = itemManager.findCustomItemByType(ItemType.DROP_TO_INV, itemStack);
        if (customItemByType.isEmpty())
            return;

        if (!(customItemByType.get() instanceof DropToInventoryItem dropToInventoryItem)) {
            return;
        }
        if (!dropToInventoryItem.isEnabled())
            return;

        event.setCancelled(true);
        double cooldownSeconds = cooldownManager.getCooldownSecond(player, dropToInventoryItem);
        if (cooldownSeconds <= 0 || player.hasPermission(dropToInventoryItem.getPermissionBypass())) {
            // set cooldown
            cooldownManager.setCooldown(player, dropToInventoryItem);

            // play sound
            dropToInventoryItem.playSound(player.getLocation());

        } else {
            MessageUtil.sendMessage(player, fileManager.getLangConfig().getHasCooldown().replace("{time}", String.valueOf(cooldownSeconds)));
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        if (killer == null) {
            return;
        }

        boolean itemDropToInv = findItemDropToInv(killer.getInventory());
        if (!itemDropToInv) {
            return;
        }

        List<ItemStack> itemsToAdd = new ArrayList<>(event.getDrops());
        event.getDrops().clear();
        itemsToAdd.forEach(itemStack -> {
            PlayerUtil.giveItem(killer, itemStack);
        });
    }

    private boolean findItemDropToInv(PlayerInventory inventory) {
        for (ItemStack item : inventory.getContents()) {
            if(item==null)
                continue;

            Optional<CustomItem> customItemByType = itemManager.findCustomItemByType(ItemType.DROP_TO_INV, item);
            if (customItemByType.isEmpty())
                continue;

            return true;
        }
        return false;
    }
}