package dev.gether.getcustomitem.listener;

import com.sk89q.worldguard.protection.flags.Flags;
import dev.gether.getconfig.utils.MessageUtil;
import dev.gether.getcustomitem.file.FileManager;
import dev.gether.getcustomitem.file.config.Config;
import dev.gether.getcustomitem.cooldown.CooldownManager;
import dev.gether.getcustomitem.item.CustomItem;
import dev.gether.getcustomitem.item.ItemManager;
import dev.gether.getcustomitem.item.ItemType;
import dev.gether.getcustomitem.item.customize.SnowballTPItem;
import dev.gether.getcustomitem.utils.WorldGuardUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Optional;

public class SnowballTeleport implements Listener {

    private final ItemManager itemManager;
    private final CooldownManager cooldownManager;
    private final FileManager fileManager;

    public SnowballTeleport(ItemManager itemManager,
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

        Optional<CustomItem> customItemByType = itemManager.findCustomItemByType(ItemType.SNOWBALL_TP, itemStack);
        if (customItemByType.isEmpty() || !(customItemByType.get() instanceof SnowballTPItem snowballTPItem))
            return;

        // check the item is enabled / if not then cancel
        if (!snowballTPItem.isEnabled())
            return;

        event.setCancelled(true);
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_BLOCK && action != Action.RIGHT_CLICK_AIR) {
            return;
        }

        double cooldownSeconds = cooldownManager.getCooldownSecond(player, snowballTPItem);
        if (cooldownSeconds <= 0 || player.hasPermission(snowballTPItem.getPermissionBypass())) {

            // particles and sound
            snowballTPItem.playSound(player.getLocation()); // play sound

            // clean cobweb
            snowballTPItem.throwSnowball(player);

            // verify a value to usage of item
            snowballTPItem.takeUsage(player, itemStack, event.getHand());


        } else {
            MessageUtil.sendMessage(player, fileManager.getLangConfig().getHasCooldown().replace("{time}", String.valueOf(cooldownSeconds)));
        }

    }

    @EventHandler
    public void onHit(ProjectileHitEvent event) {

        if (event.getHitEntity() == null || !(event.getHitEntity() instanceof Player hitPlayer))
            return;

        // check is not the npc
        boolean isCitizensNPC = hitPlayer.hasMetadata("NPC");
        if (isCitizensNPC) return;

        if (event.getEntity() instanceof Snowball snowball) {

            if (!(snowball.getShooter() instanceof Player shooter))
                return;

            ItemStack item = snowball.getItem();
            List<CustomItem> customItemByType = itemManager.findAllCustomItemByType(ItemType.SNOWBALL_TP);
            if (customItemByType.isEmpty())
                return;

            for (CustomItem customItem : customItemByType) {
                if(!(customItem instanceof SnowballTPItem snowballTPItem))
                    continue;

                if(!snowballTPItem.getThrowingItemStack().isSimilar(item))
                    continue;

                if (hitPlayer.hasPermission(snowballTPItem.getPermissionBypass()))
                    return;

                /* world-guard section */
                // check if the hit player is in the pvp region
                if(WorldGuardUtil.isDeniedFlag(hitPlayer.getLocation(), hitPlayer, Flags.PVP)) {
                    return;
                }

                // check if the shooter player is in the pvp region
                if(WorldGuardUtil.isDeniedFlag(shooter.getLocation(), shooter, Flags.PVP)) {
                    return;
                }

                Location shooterLoc = shooter.getLocation().clone();
                Location hitLocation = hitPlayer.getLocation().clone();

                hitPlayer.teleport(shooterLoc);
                shooter.teleport(hitLocation);

                // alert yourself
                snowballTPItem.notifyYourself(shooter);
                snowballTPItem.notifyOpponents(hitPlayer);

                // set cooldown
                cooldownManager.setCooldown(shooter, snowballTPItem);
            }
        }
    }



}
