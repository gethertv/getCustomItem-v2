package dev.gether.getcustomitem.listener;

import dev.gether.getconfig.utils.MessageUtil;
import dev.gether.getcustomitem.cooldown.CooldownManager;
import dev.gether.getcustomitem.file.FileManager;
import dev.gether.getcustomitem.item.CustomItem;
import dev.gether.getcustomitem.item.ItemManager;
import dev.gether.getcustomitem.item.ItemType;
import dev.gether.getcustomitem.item.customize.HookItem;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Optional;

public class FishRodListener implements Listener {

    private final ItemManager itemManager;
    private final CooldownManager cooldownManager;
    private final FileManager fileManager;

    public FishRodListener(ItemManager itemManager, CooldownManager cooldownManager, FileManager fileManager) {
        this.itemManager = itemManager;
        this.cooldownManager = cooldownManager;
        this.fileManager = fileManager;
    }

    @EventHandler
    public void onFish(PlayerFishEvent event) {

        Player player = event.getPlayer();

        boolean status = handleEvent(event, player, player.getInventory().getItemInMainHand(), EquipmentSlot.HAND);
        if (status) return;

        handleEvent(event, player, player.getInventory().getItemInOffHand(), EquipmentSlot.OFF_HAND);


    }

    private boolean handleEvent(PlayerFishEvent event, Player player, ItemStack itemStack, EquipmentSlot equipmentSlot) {
        Optional<CustomItem> customItemByType = itemManager.findCustomItemByType(ItemType.HOOK, itemStack);
        if (customItemByType.isPresent()) {
            if(customItemByType.get() instanceof HookItem hookItem) {
                if (hookItem == null || !hookItem.isEnabled()) return true;

                handleEvent(event, player, hookItem, itemStack, equipmentSlot);
                return true;
            }
        }
        return false;
    }
    private void handleEvent(PlayerFishEvent event, Player player, HookItem hookItem, ItemStack itemStack, EquipmentSlot equipmentSlot) {
        PlayerFishEvent.State state = event.getState();
        if (state == PlayerFishEvent.State.IN_GROUND ||
                state == PlayerFishEvent.State.CAUGHT_ENTITY ||
                state == PlayerFishEvent.State.REEL_IN) {

            double cooldownSeconds = cooldownManager.getCooldownSecond(player, hookItem);
            String permissionBypass = hookItem.getPermissionBypass();
            if (cooldownSeconds <= 0 || player.hasPermission(permissionBypass)) {

                // verify a value to usage of item
                hookItem.takeUsage(player, itemStack, equipmentSlot);

                Location hooklocation = event.getHook().getLocation();
                Location playerLocation = player.getLocation();

                Vector vector = playerLocation.toVector();
                Vector direction = hooklocation.toVector().subtract(vector).normalize();

                double divideGliding = hookItem.getDivideGliding();
                double multiply = hookItem.getMultiply();
                if (player.isGliding() && divideGliding != 0)
                    multiply /= divideGliding;

                Vector velocity = direction.multiply(multiply);
                velocity.setY(velocity.getY() / hookItem.getDivideHeight());
                player.setVelocity(velocity);

                // set cooldown
                cooldownManager.setCooldown(player, hookItem);


                // alert
                hookItem.notifyYourself(player);

            } else {
                MessageUtil.sendMessage(player, fileManager.getLangConfig().getHasCooldown().replace("{time}", String.valueOf(cooldownSeconds)));
            }

        }
    }

}
