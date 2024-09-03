package dev.gether.getcustomitem.listener;

import com.sk89q.worldguard.protection.flags.Flags;
import dev.gether.getconfig.utils.MessageUtil;
import dev.gether.getcustomitem.cooldown.CooldownManager;
import dev.gether.getcustomitem.file.FileManager;
import dev.gether.getcustomitem.item.CustomItem;
import dev.gether.getcustomitem.item.ItemManager;
import dev.gether.getcustomitem.item.ItemType;
import dev.gether.getcustomitem.item.customize.itemtier.ActionEvent;
import dev.gether.getcustomitem.item.customize.itemtier.ItemTier;
import dev.gether.getcustomitem.utils.WorldGuardUtil;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.concurrent.TimeUnit;

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

                handlePlayerAction(player, null, ActionEvent.BREAK_BLOCK, block.getType(), itemTier, itemStack);
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

                handlePlayerAction(killer, entity, ActionEvent.KILL_ENTITY, entity.getType(), itemTier, itemStack);
            });
        }
    }

    public void handlePlayerAction(Player player, Entity entity, ActionEvent actionEvent, Object entityType, ItemTier itemTier, ItemStack itemStack) {
        if(itemStack == null) return;
        boolean status = itemTier.isItemTier(itemStack);
        if(!status)
            return;

        if(itemTier.isMaxLevel(itemStack))
            return;


        if (fileManager.getConfig().getCooldown().containsKey(entityType.toString())) {
            String playerUUID = player.getUniqueId().toString();
            String cooldownKey = playerUUID + ":" + entityType.toString();
            if(entityType == EntityType.PLAYER && entity != null){
                cooldownKey = playerUUID + ":"+ entity.getUniqueId();
            }

            if (cooldownManager.isOnCooldown(cooldownKey)) {
                long timeLeft = cooldownManager.getCooldownTime(cooldownKey);
                String formattedTime = formatTime(timeLeft);
                String cooldownMessage = fileManager.getConfig().getCooldownMessage()
                        .replace("{time}", formattedTime);
                MessageUtil.sendMessage(player, cooldownMessage);
                return;
            }
            // Set cooldown
            int cooldownSeconds = fileManager.getConfig().getCooldown().get(entityType.toString());
            cooldownManager.setCooldown(cooldownKey, cooldownSeconds);
        }

        itemTier.action(player, actionEvent, entityType, 1, itemStack);
    }

    private String formatTime(long milliseconds) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds) % 60;
        return String.format("%d:%02d", minutes, seconds);
    }
}