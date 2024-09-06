package dev.gether.getcustomitem.listener;

import dev.gether.getconfig.utils.MessageUtil;
import dev.gether.getcustomitem.cooldown.CooldownManager;
import dev.gether.getcustomitem.file.FileManager;
import dev.gether.getcustomitem.item.CustomItem;
import dev.gether.getcustomitem.item.ItemManager;
import dev.gether.getcustomitem.item.ItemType;
import dev.gether.getcustomitem.item.customize.InstaHealItem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;
import java.util.Random;

public class InstaHealListener implements Listener {

    private final ItemManager itemManager;
    private final CooldownManager cooldownManager;
    private final FileManager fileManager;
    private final Random random = new Random();

    public InstaHealListener(ItemManager itemManager,
                             CooldownManager cooldownManager,
                             FileManager fileManager) {
        this.itemManager = itemManager;
        this.cooldownManager = cooldownManager;
        this.fileManager = fileManager;
    }

    /**
     * cancel place/use custom item
     * @param event
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {

        Player player = event.getPlayer();
        ItemStack itemStack = event.getItem(); // using item

        if(itemStack == null)
            return;

        Action action = event.getAction();
        if(action != Action.RIGHT_CLICK_BLOCK && action != Action.RIGHT_CLICK_AIR) {
            return;
        }


        Optional<CustomItem> customItemByType = itemManager.findCustomItemByType(ItemType.INSTA_HEAL, itemStack);
        if (customItemByType.isEmpty() || !(customItemByType.get() instanceof InstaHealItem instaHealItem))
            return;

        event.setCancelled(true);

        // check the item is enabled / if not then cancel
        if(!instaHealItem.isEnabled())
            return;


        double cooldownSeconds = cooldownManager.getCooldownSecond(player, instaHealItem);
        if(cooldownSeconds <= 0 || player.hasPermission(instaHealItem.getPermissionBypass())) {
            // set cooldown
            cooldownManager.setCooldown(player, instaHealItem);

            // play sound
            instaHealItem.playSound(player.getLocation()); // play sound

            // chance to insta heal
            if(random.nextDouble() < (instaHealItem.getChance() / 100d)) {
                player.setHealth(player.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).getValue());

                // alert yourself
                instaHealItem.notifyYourself(player);
            }

            // verify a value to usage of item
            instaHealItem.takeUsage(player, itemStack, event.getHand());


        } else {
            MessageUtil.sendMessage(player, fileManager.getLangConfig().getHasCooldown().replace("{time}", String.valueOf(cooldownSeconds)));
        }
    }
}