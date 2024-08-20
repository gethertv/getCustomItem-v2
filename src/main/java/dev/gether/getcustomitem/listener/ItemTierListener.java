package dev.gether.getcustomitem.listener;

import com.sk89q.worldguard.protection.flags.Flags;
import dev.gether.getcustomitem.cooldown.CooldownManager;
import dev.gether.getcustomitem.file.FileManager;
import dev.gether.getcustomitem.item.CustomItem;
import dev.gether.getcustomitem.item.ItemManager;
import dev.gether.getcustomitem.item.ItemType;
import dev.gether.getcustomitem.item.customize.StopFlyingItem;
import dev.gether.getcustomitem.item.customize.itemtier.ActionEvent;
import dev.gether.getcustomitem.item.customize.itemtier.ItemTier;
import dev.gether.getcustomitem.utils.WorldGuardUtil;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Optional;

public class ItemTierListener implements Listener {

    private final ItemManager itemManager;
    private final CooldownManager cooldownManager;
    private final FileManager fileManager;
    public ItemTierListener(ItemManager itemManager,
                            CooldownManager cooldownManager,
                            FileManager fileManager) {
        this.itemManager = itemManager;
        this.cooldownManager = cooldownManager;
        this.fileManager = fileManager;
    }

    @EventHandler
    public void onBreakBlock(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        if(WorldGuardUtil.isDeniedFlag(block.getLocation(), player, Flags.BLOCK_BREAK)) {
            return;
        }

        List<CustomItem> customItemByType = itemManager.findAllCustomItemByType(ItemType.ITEM_TIER);
        if (customItemByType.isEmpty())
            return;

        for (CustomItem customItem : customItemByType) {
            if (!(customItem instanceof ItemTier itemTier))
                continue;

            itemTier.getEquipmentSlots().forEach(equipmentSlot -> {
                ItemStack itemStack = player.getInventory().getItem(equipmentSlot);
                if(itemStack == null) return;

                handlePlayerDeath(player, ActionEvent.BREAK_BLOCK, block.getType(), itemTier, itemStack);
            });
        }

    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Player killer = entity.getKiller();
        if(killer == null) return;

        if(killer == event.getEntity()) return;

        List<CustomItem> customItemByType = itemManager.findAllCustomItemByType(ItemType.ITEM_TIER);
        if (customItemByType.isEmpty())
            return;

        for (CustomItem customItem : customItemByType) {
            if (!(customItem instanceof ItemTier itemTier))
                continue;

            itemTier.getEquipmentSlots().forEach(equipmentSlot -> {
                ItemStack itemStack = killer.getInventory().getItem(equipmentSlot);
                if(itemStack == null) return;

                handlePlayerDeath(killer, ActionEvent.KILL_ENTITY, entity.getType(), itemTier, itemStack);
            });
        }
    }

    public void handlePlayerDeath(Player player, ActionEvent actionEvent, Object entityType, ItemTier itemTier, ItemStack itemStack) {
        if(itemStack == null) return;
        boolean status = itemTier.isItemTier(itemStack);
        if(!status)
            return;

        itemTier.action(player, actionEvent, entityType, 1, itemStack);
        return;
    }


}
