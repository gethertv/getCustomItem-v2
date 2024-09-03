package dev.gether.getcustomitem.listener;

import dev.gether.getconfig.utils.MessageUtil;
import dev.gether.getcustomitem.cooldown.CooldownManager;
import dev.gether.getcustomitem.file.FileManager;
import dev.gether.getcustomitem.item.CustomItem;
import dev.gether.getcustomitem.item.ItemManager;
import dev.gether.getcustomitem.item.ItemType;
import dev.gether.getcustomitem.item.customize.ThrowingEnderPearlsItem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class ThrowingEnderPearlsListener implements Listener {

    private final ItemManager itemManager;
    private final CooldownManager cooldownManager;
    private final FileManager fileManager;

    public ThrowingEnderPearlsListener(ItemManager itemManager,
                                       CooldownManager cooldownManager,
                                       FileManager fileManager) {
        this.itemManager = itemManager;
        this.cooldownManager = cooldownManager;
        this.fileManager = fileManager;
    }


    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = event.getItem(); // using item

        if (itemStack == null)
            return;

        Optional<CustomItem> customItemByType = itemManager.findCustomItemByType(ItemType.THROWING_ENDER_PEARLS, itemStack);
        if (customItemByType.isEmpty() || !(customItemByType.get() instanceof ThrowingEnderPearlsItem throwingEnderPearlsItem))
            return;

        // check the item is enabled / if not then cancel
        if (!throwingEnderPearlsItem.isEnabled())
            return;

        event.setCancelled(true);

        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_BLOCK && action != Action.RIGHT_CLICK_AIR) {
            return;
        }

        double cooldownSeconds = cooldownManager.getCooldownSecond(player, throwingEnderPearlsItem);
        if (cooldownSeconds <= 0 || player.hasPermission(throwingEnderPearlsItem.getPermissionBypass())) {

            // particles and sound
            throwingEnderPearlsItem.playSound(player.getLocation()); // play sound

            // clean cobweb
            throwingEnderPearlsItem.throwEnderPearls(player);

            // verify a value to usage of item
            throwingEnderPearlsItem.takeUsage(player, itemStack, event.getHand());


        } else {
            MessageUtil.sendMessage(player, fileManager.getLangConfig().getHasCooldown().replace("{time}", String.valueOf(cooldownSeconds)));
        }

    }

}