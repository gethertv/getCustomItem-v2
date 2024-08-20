package dev.gether.getcustomitem.listener;

import com.sk89q.worldguard.protection.flags.Flags;
import dev.gether.getconfig.utils.MessageUtil;
import dev.gether.getconfig.utils.PlayerUtil;
import dev.gether.getcustomitem.file.FileManager;
import dev.gether.getcustomitem.file.config.Config;
import dev.gether.getcustomitem.cooldown.CooldownManager;
import dev.gether.getcustomitem.item.CustomItem;
import dev.gether.getcustomitem.item.ItemManager;
import dev.gether.getcustomitem.item.ItemType;
import dev.gether.getcustomitem.item.customize.ThrowUpItem;
import dev.gether.getcustomitem.utils.WorldGuardUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ThrowUpListener implements Listener {

    private final ItemManager itemManager;
    private final CooldownManager cooldownManager;
    private final FileManager fileManager;

    public ThrowUpListener(ItemManager itemManager,
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

        if(itemStack == null)
            return;

        Optional<CustomItem> customItemByType = itemManager.findCustomItemByType(ItemType.THROW_UP, itemStack);
        if(customItemByType.isEmpty() || !(customItemByType.get() instanceof ThrowUpItem tossUpwardItem))
            return;

        event.setCancelled(true);

        if(!tossUpwardItem.isEnabled())
            return;

        Action action = event.getAction();
        if(action != Action.RIGHT_CLICK_BLOCK && action != Action.RIGHT_CLICK_AIR) {
            return;
        }

        double cooldownSeconds = cooldownManager.getCooldownSecond(player, tossUpwardItem);
        if(cooldownSeconds <= 0 || player.hasPermission(tossUpwardItem.getPermissionBypass())) {
            // set cooldown
            cooldownManager.setCooldown(player, tossUpwardItem);

            // find near players
            List<Player> nearPlayers = PlayerUtil.findNearPlayers(player.getLocation(), tossUpwardItem.getRadius());
            // filter players from NPC and WorldGuard
            List<Player> players = filterPlayers(nearPlayers);

            // particles and sound
            tossUpwardItem.playSound(player.getLocation()); // play sound

            /* world-guard section */
            // check the using player is in PVP region
            if(WorldGuardUtil.isDeniedFlag(player.getLocation(), player, Flags.PVP)) {
                return;
            }

            // alert
            tossUpwardItem.notifyYourself(player);
            players.forEach(p -> {
                if(p.getName().equalsIgnoreCase(player.getName()))
                    return;

                tossUpwardItem.notifyOpponents(p);
            });

            tossUpward(tossUpwardItem, player, nearPlayers);

            // verify a value to usage of item
            tossUpwardItem.takeUsage(player, itemStack, event.getHand());


        } else {
            MessageUtil.sendMessage(player, fileManager.getLangConfig().getHasCooldown().replace("{time}", String.valueOf(cooldownSeconds)));
        }

    }

    private List<Player> filterPlayers(List<Player> listPlayers) {
        List<Player> players = new ArrayList<>();
        listPlayers.forEach(p -> {
            // check is not the npc
            boolean isCitizensNPC = p.hasMetadata("NPC");
            if (isCitizensNPC) return;

            if (WorldGuardUtil.isInRegion(p) &&
                    WorldGuardUtil.isDeniedFlag(p.getLocation(), p, Flags.PVP)) {
                return;
            }

            players.add(p);
        });
        return players;
    }

    private void tossUpward(ThrowUpItem tossUpwardItem, Player player, List<Player> nearPlayers) {
        // check every player who can't be in the pvp region
        nearPlayers.forEach(p -> {

            // if this option is disabled then don't give the effects to you
            if(tossUpwardItem.isIncludingYou() && p.getName().equalsIgnoreCase(player.getName())) {
                Vector direction = player.getLocation().getDirection().normalize().multiply(-tossUpwardItem.getPushYourself());
                player.setVelocity(direction);
                return;
            }
            // if this option is disabled then don't give the effects players in radius
            if(!tossUpwardItem.isOtherPlayers() && !p.getName().equalsIgnoreCase(player.getName()))
                return;

            Vector direction = p.getLocation().getDirection().normalize().multiply(-tossUpwardItem.getPushOpponents());
            p.setVelocity(direction);
        });
    }
}

