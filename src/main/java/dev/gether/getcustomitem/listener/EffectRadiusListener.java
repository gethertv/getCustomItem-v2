package dev.gether.getcustomitem.listener;

import com.sk89q.worldguard.protection.flags.Flags;
import dev.gether.getconfig.utils.MessageUtil;
import dev.gether.getconfig.utils.PlayerUtil;
import dev.gether.getconfig.utils.PotionConverUtil;
import dev.gether.getcustomitem.cooldown.CooldownManager;
import dev.gether.getcustomitem.file.FileManager;
import dev.gether.getcustomitem.item.CustomItem;
import dev.gether.getcustomitem.item.ItemManager;
import dev.gether.getcustomitem.item.ItemType;
import dev.gether.getcustomitem.item.customize.EffectRadiusItem;
import dev.gether.getcustomitem.utils.WorldGuardUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EffectRadiusListener implements Listener {

    private final ItemManager itemManager;
    private final CooldownManager cooldownManager;
    private final FileManager fileManager;

    public EffectRadiusListener(ItemManager itemManager,
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

        Optional<CustomItem> customItemByType = itemManager.findCustomItemByType(ItemType.EFFECT_RADIUS, itemStack);
        if(customItemByType.isEmpty() || !(customItemByType.get() instanceof EffectRadiusItem effectRadiusItem))
            return;

        event.setCancelled(true);

        if(!effectRadiusItem.isEnabled())
            return;

        Action action = event.getAction();
        if(action != Action.RIGHT_CLICK_BLOCK && action != Action.RIGHT_CLICK_AIR) {
            return;
        }

        double cooldownSeconds = cooldownManager.getCooldownSecond(player, effectRadiusItem);
        if(cooldownSeconds <= 0 || player.hasPermission(effectRadiusItem.getPermissionBypass())) {
            // set cooldown
            cooldownManager.setCooldown(player, effectRadiusItem);

            // find near players
            List<Player> nearPlayers = PlayerUtil.findNearPlayers(player.getLocation(), effectRadiusItem.getRadius());
            // filter players from NPC and WorldGuard
            List<Player> players = filterPlayers(nearPlayers);

            /* world-guard section */
            // check the using player is in PVP region
            if(WorldGuardUtil.isDeniedFlag(player.getLocation(), player, Flags.PVP)) {
                return;
            }

            // alert
            effectRadiusItem.notifyYourself(player);
            players.forEach(p -> {
                if(p.getName().equalsIgnoreCase(player.getName()))
                    return;

                effectRadiusItem.notifyOpponents(p);
            });


            // particles and sound
            effectRadiusItem.playSound(player.getLocation()); // play sound

            givePotionEffect(effectRadiusItem, player, players); // give potion effect
            removePotionEffect(effectRadiusItem, player, players); // remove potion effect

            // verify a value to usage of item
            effectRadiusItem.takeUsage(player, itemStack, event.getHand());


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
    private void givePotionEffect(EffectRadiusItem effectRadiusItem,
                                  Player player,
                                  List<Player> nearPlayers) {
        // check every player who can't be in the pvp region
        nearPlayers.forEach(p -> {

            // if this option is disabled then don't give the effects to you
            if(!effectRadiusItem.isIncludingYou() && p.getName().equalsIgnoreCase(player.getName()))
                return;

            // if this option is disabled then don't give the effects players in radius
            if(!effectRadiusItem.isOtherPlayers() && !p.getName().equalsIgnoreCase(player.getName()))
                return;


            // give all effect from list to player
            List<PotionEffect> activePotionEffect = PotionConverUtil.getPotionEffectFromConfig(effectRadiusItem.getActiveEffect());
            activePotionEffect.forEach(p::addPotionEffect);
        });
    }

    private void removePotionEffect(EffectRadiusItem effectRadiusItem,
                                    Player player,
                                    List<Player> nearPlayers) {
        // check every player who can't be in the pvp region
        nearPlayers.forEach(p -> {

            // check is not the npc
            boolean isCitizensNPC = p.hasMetadata("NPC");
            if(isCitizensNPC) return;

            // if this option is disabled then don't remove the effects from you
            if(!effectRadiusItem.isIncludingYou() && p.getName().equalsIgnoreCase(player.getName()))
                return;

            // if this option is disabled then don't remove the effects from players in radius
            if(!effectRadiusItem.isOtherPlayers() && !p.getName().equalsIgnoreCase(player.getName()))
                return;


            if(WorldGuardUtil.isInRegion(p) &&
                    WorldGuardUtil.isDeniedFlag(p.getLocation(), p, Flags.PVP)) {
                return;
            }
            // remove potion effect
            List<PotionEffectType> potionEffectTypes = PotionConverUtil.getPotionEffectByName(effectRadiusItem.getRemoveEffect());
            p.getActivePotionEffects().forEach(potionEffect -> {
                if(potionEffectTypes.contains(potionEffect.getType()))
                    p.removePotionEffect(potionEffect.getType());
            });
        });
    }




}