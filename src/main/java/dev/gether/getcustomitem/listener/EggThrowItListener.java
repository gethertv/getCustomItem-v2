package dev.gether.getcustomitem.listener;

import com.sk89q.worldguard.protection.flags.Flags;
import dev.gether.getconfig.utils.MessageUtil;
import dev.gether.getcustomitem.file.FileManager;
import dev.gether.getcustomitem.file.config.Config;
import dev.gether.getcustomitem.cooldown.CooldownManager;
import dev.gether.getcustomitem.item.CustomItem;
import dev.gether.getcustomitem.item.ItemManager;
import dev.gether.getcustomitem.item.ItemType;
import dev.gether.getcustomitem.item.customize.EggThrowItItem;
import dev.gether.getcustomitem.item.customize.StopFlyingItem;
import dev.gether.getcustomitem.utils.WorldGuardUtil;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.List;
import java.util.Optional;

public class EggThrowItListener implements Listener {

    private final ItemManager itemManager;
    private final CooldownManager cooldownManager;
    private final FileManager fileManager;

    public EggThrowItListener(ItemManager itemManager,
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

        Optional<CustomItem> customItemByType = itemManager.findCustomItemByType(ItemType.EGG_THROW_UP, itemStack);
        if (customItemByType.isEmpty() || !(customItemByType.get() instanceof EggThrowItItem eggThrowItItem))
            return;

        // check the item is enabled / if not then cancel
        if (!eggThrowItItem.isEnabled())
            return;

        event.setCancelled(true);

        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_BLOCK && action != Action.RIGHT_CLICK_AIR) {
            return;
        }

        double cooldownSeconds = cooldownManager.getCooldownSecond(player, eggThrowItItem);
        if (cooldownSeconds <= 0 || player.hasPermission(eggThrowItItem.getPermissionBypass())) {
            // particles and sound
            eggThrowItItem.playSound(player.getLocation()); // play sound

            // clean cobweb
            eggThrowItItem.throwEgg(player);

            // cooldown
            cooldownManager.setCooldown(player, eggThrowItItem);

            // verify a value to usage of item
            eggThrowItItem.takeUsage(player, itemStack, event.getHand());

        } else {
            MessageUtil.sendMessage(player, fileManager.getLangConfig().getHasCooldown().replace("{time}", String.valueOf(cooldownSeconds)));
        }

    }

    @EventHandler
    public void test(PlayerEggThrowEvent event) {
        Egg egg = event.getEgg();

        List<CustomItem> customItemByType = itemManager.findAllCustomItemByType(ItemType.EGG_THROW_UP);
        if (customItemByType.isEmpty())
            return;

        for (CustomItem customItem : customItemByType) {
            if (!(customItem instanceof EggThrowItItem eggThrowItItem))
                continue;

            if (egg.getItem().isSimilar(eggThrowItItem.getThrowingItemStack())) {
                event.setHatching(false);
                return;
            }
        }
    }


    @EventHandler
    public void onHit(ProjectileHitEvent event) {

        if (event.getHitEntity() == null || !(event.getHitEntity() instanceof Player hitPlayer))
            return;

        // check is not the npc
        boolean isCitizensNPC = hitPlayer.hasMetadata("NPC");
        if (isCitizensNPC) return;

        if (event.getEntity() instanceof Egg egg) {
            ItemStack item = egg.getItem();
            List<CustomItem> customItemByType = itemManager.findAllCustomItemByType(ItemType.EGG_THROW_UP);
            if (customItemByType.isEmpty())
                return;

            for (CustomItem customItem : customItemByType) {
                if (!(customItem instanceof EggThrowItItem eggThrowItItem))
                    continue;

                if (!eggThrowItItem.getThrowingItemStack().isSimilar(item)) {
                    continue;
                }

                event.setCancelled(true);
                if (!(egg.getShooter() instanceof Player shooter))
                    return;

                if (hitPlayer.hasPermission(eggThrowItItem.getPermissionByPass()))
                    return;

                egg.remove();

                /* world-guard section */
                // check if the hit player is in the pvp region
                if(WorldGuardUtil.isDeniedFlag(hitPlayer.getLocation(), hitPlayer, Flags.PVP)) {
                    return;
                }

                // check if the shooter player is in the pvp region
                if(WorldGuardUtil.isDeniedFlag(shooter.getLocation(), shooter, Flags.PVP)) {
                    return;
                }

                Vector velocity = hitPlayer.getVelocity();
                velocity.setY(eggThrowItItem.getPushPower());

                hitPlayer.setVelocity(velocity);

                // alert yourself
                eggThrowItItem.notifyYourself(shooter);
                eggThrowItItem.notifyOpponents(hitPlayer);


                // set cooldown
                cooldownManager.setCooldown(shooter, eggThrowItItem);
                return;
            }

        }
    }


}
