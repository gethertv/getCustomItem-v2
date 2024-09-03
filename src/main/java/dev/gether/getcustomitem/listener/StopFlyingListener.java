package dev.gether.getcustomitem.listener;

import dev.gether.getconfig.utils.MessageUtil;
import dev.gether.getcustomitem.GetCustomItem;
import dev.gether.getcustomitem.cooldown.CooldownManager;
import dev.gether.getcustomitem.file.FileManager;
import dev.gether.getcustomitem.item.CustomItem;
import dev.gether.getcustomitem.item.ItemManager;
import dev.gether.getcustomitem.item.ItemType;
import dev.gether.getcustomitem.item.customize.StopFlyingItem;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Optional;

public class StopFlyingListener implements Listener {

    private static final String HOOKED_METADATA = "hooked_flying";

    private final ItemManager itemManager;
    private final CooldownManager cooldownManager;
    private final FileManager fileManager;
    private final GetCustomItem plugin;

    public StopFlyingListener(ItemManager itemManager,
                              CooldownManager cooldownManager,
                              FileManager fileManager,
                              GetCustomItem plugin) {
        this.itemManager = itemManager;
        this.cooldownManager = cooldownManager;
        this.fileManager = fileManager;
        this.plugin = plugin;
    }

    @EventHandler
    public void onElytraUse(EntityToggleGlideEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player player)) {
            return;
        }

        if (player.hasMetadata(HOOKED_METADATA)) {
            event.setCancelled(true);
            MessageUtil.sendMessage(player, fileManager.getLangConfig().getCannotUseElytraWhileHooked());
        }
    }

    @EventHandler
    public void onFish(PlayerFishEvent event) {

        Player player = event.getPlayer();

        boolean status = handleEvent(event, player, player.getInventory().getItemInMainHand(), EquipmentSlot.HAND);
        if (status) return;

        handleEvent(event, player, player.getInventory().getItemInOffHand(), EquipmentSlot.OFF_HAND);


    }

    private boolean handleEvent(PlayerFishEvent event, Player player, ItemStack itemStack, EquipmentSlot equipmentSlot) {
        Optional<CustomItem> customItemByType = itemManager.findCustomItemByType(ItemType.STOP_FLYING, itemStack);
        if (customItemByType.isPresent()) {
            if (customItemByType.get() instanceof StopFlyingItem stopFlyingItem) {
                if (stopFlyingItem == null || !stopFlyingItem.isEnabled()) return true;

                handleEvent(event, player, stopFlyingItem, itemStack, equipmentSlot);
                return true;
            }
        }
        return false;
    }

    private void handleEvent(PlayerFishEvent event, Player player, StopFlyingItem stopFlyingItem, ItemStack itemStack, EquipmentSlot equipmentSlot) {
        Entity caught = event.getCaught();
        if (event.getState() == PlayerFishEvent.State.CAUGHT_ENTITY) {
            if (caught instanceof Player hookedPlayer) {
                double cooldownSeconds = cooldownManager.getCooldownSecond(player, stopFlyingItem);
                String permissionBypass = stopFlyingItem.getPermissionBypass();
                if (cooldownSeconds <= 0 || player.hasPermission(permissionBypass)) {
                    cooldownManager.setCooldown(player, stopFlyingItem);
                    stopFlyingItem.takeUsage(player);
                    stopFlyingItem.notifyYourself(player);
                    stopFlyingItem.notifyOpponents(hookedPlayer);

                    applyStopFlyingEffect(hookedPlayer, stopFlyingItem.getStopFlyingTime());
                } else {
                    MessageUtil.sendMessage(player, fileManager.getLangConfig().getHasCooldown().replace("{time}", String.valueOf(cooldownSeconds)));
                }
            } else if (event.getState() == PlayerFishEvent.State.REEL_IN ||
                    event.getState() == PlayerFishEvent.State.IN_GROUND) {
                if (caught instanceof Player hookedPlayer) {
                    hookedPlayer.removeMetadata(HOOKED_METADATA, plugin);
                }
            }
        }

    }


    @EventHandler
    public void onFireworkUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player.hasMetadata(HOOKED_METADATA)) {
            List<CustomItem> customItemByType = itemManager.findAllCustomItemByType(ItemType.STOP_FLYING);
            if (customItemByType.isEmpty())
                return;

            for (CustomItem customItem : customItemByType) {
                if (!(customItem instanceof StopFlyingItem stopFlyingItem))
                    continue;

                if (event.getItem() != null && stopFlyingItem.getCannotUseMaterial().contains(event.getItem().getType())) {
                    event.setCancelled(true);
                    MessageUtil.sendMessage(player, fileManager.getLangConfig().getCannotUseItemWhileHooked());
                    return;
                }
            }

        }
    }

    private void applyStopFlyingEffect(Player player, int duration) {
        player.setMetadata(HOOKED_METADATA, new FixedMetadataValue(plugin, true));
        player.setGliding(false);

        new BukkitRunnable() {
            @Override
            public void run() {
                player.removeMetadata(HOOKED_METADATA, plugin);
                MessageUtil.sendMessage(player, fileManager.getLangConfig().getCanFlyAgain());
            }
        }.runTaskLater(plugin, duration * 20L);
    }

}