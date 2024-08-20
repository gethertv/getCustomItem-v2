package dev.gether.getcustomitem.listener;

import dev.gether.getconfig.utils.ItemUtil;
import dev.gether.getconfig.utils.MessageUtil;
import dev.gether.getcustomitem.file.FileManager;
import dev.gether.getcustomitem.file.config.Config;
import dev.gether.getcustomitem.cooldown.CooldownManager;
import dev.gether.getcustomitem.item.CustomItem;
import dev.gether.getcustomitem.item.ItemManager;
import dev.gether.getcustomitem.item.ItemType;
import dev.gether.getcustomitem.item.customize.CobwebGrenade;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Optional;

public class CobwebGrenadeListener implements Listener {

    private final ItemManager itemManager;
    private final CooldownManager cooldownManager;
    private final FileManager fileManager;

    public CobwebGrenadeListener(ItemManager itemManager, CooldownManager cooldownManager, FileManager fileManager) {
        this.itemManager = itemManager;
        this.cooldownManager = cooldownManager;
        this.fileManager = fileManager;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {

        Player player = event.getPlayer();
        ItemStack itemStack = event.getItem(); // using item

        if(itemStack == null)
            return;

        Optional<CustomItem> customItemByType = itemManager.findCustomItemByType(ItemType.COBWEB_GRENADE, itemStack);
        if(customItemByType.isEmpty() || !(customItemByType.get() instanceof CobwebGrenade cobwebGrenade))
            return;

        // check the item is enabled / if not then cancel
        if(!cobwebGrenade.isEnabled())
            return;

        event.setCancelled(true);

        Action action = event.getAction();
        if(action != Action.RIGHT_CLICK_BLOCK && action != Action.RIGHT_CLICK_AIR)
            return;

        double cooldownSeconds = cooldownManager.getCooldownSecond(player, cobwebGrenade);
        if(cooldownSeconds <= 0 || player.hasPermission(cobwebGrenade.getPermissionBypass())) {
            // set cooldown
            cooldownManager.setCooldown(player, cobwebGrenade);

            // create grenade
            ThrownPotion thrownPotion = (ThrownPotion) player.getWorld().spawnEntity(player.getLocation().clone().add(0, 1.1, 0), EntityType.SPLASH_POTION);
            thrownPotion.setItem(itemStack);

            Location playerLocation = player.getLocation().clone().add(0, cobwebGrenade.getHeightVelocity(), 0);
            Vector velocity = playerLocation.getDirection().multiply(cobwebGrenade.getMultiply());
            thrownPotion.setVelocity(velocity); // throw grenade

            // particles and sound
            cobwebGrenade.runParticles(thrownPotion); // particles
            cobwebGrenade.playSound(player.getLocation()); // play sound

            // verify a value to usage of item
            cobwebGrenade.takeUsage(player, itemStack, event.getHand());

            // alert yourself
            cobwebGrenade.notifyYourself(player);

        } else {
            MessageUtil.sendMessage(player, fileManager.getLangConfig().getHasCooldown().replace("{time}", String.valueOf(cooldownSeconds)));
        }

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPotionSplash(PotionSplashEvent event) {
        if(event.isCancelled()) return;

        ThrownPotion potion = event.getPotion();
        Location location = event.getEntity().getLocation();
        ItemStack itemStack = potion.getItem();

        Optional<CustomItem> customItemByType = itemManager.findCustomItemByType(ItemType.COBWEB_GRENADE, itemStack);
        if(customItemByType.isEmpty())
            return;

        CobwebGrenade cobwebGrenade = (CobwebGrenade) customItemByType.get();
        if(ItemUtil.sameItemName(itemStack, cobwebGrenade.getItem().getItemStack())) {
            cobwebGrenade.spawnCobweb(location); // spawn cobweb
        }
    }

}
