package dev.gether.getcustomitem.listener;

import com.sk89q.worldguard.protection.flags.Flags;
import dev.gether.getconfig.utils.MessageUtil;
import dev.gether.getcustomitem.cooldown.CooldownManager;
import dev.gether.getcustomitem.file.FileManager;
import dev.gether.getcustomitem.item.CustomItem;
import dev.gether.getcustomitem.item.ItemManager;
import dev.gether.getcustomitem.item.ItemType;
import dev.gether.getcustomitem.item.customize.PokeballItem;
import dev.gether.getcustomitem.utils.WorldGuardUtil;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public class PokeballListener implements Listener {
    private final ItemManager itemManager;

    private final CooldownManager cooldownManager;

    private final FileManager fileManager;

    private final Random random = new Random();

    public PokeballListener(ItemManager itemManager, CooldownManager cooldownManager, FileManager fileManager) {
        this.itemManager = itemManager;
        this.cooldownManager = cooldownManager;
        this.fileManager = fileManager;
    }

    @EventHandler
    public void onHit(ProjectileHitEvent event) {
        Player hitPlayer;
        if (event.getHitEntity() != null) {
            Entity entity = event.getHitEntity();
            if (entity instanceof Player) {
                hitPlayer = (Player)entity;
            } else {
                return;
            }
        } else {
            return;
        }
        boolean isCitizensNPC = hitPlayer.hasMetadata("NPC");
        if (isCitizensNPC)
            return;

        Projectile projectile = event.getEntity();
        if (projectile instanceof Arrow arrow) {
            List<CustomItem> customItemByType = this.itemManager.findAllCustomItemByType(ItemType.POKE_BALL);
            if (customItemByType.isEmpty())
                return;
            for (CustomItem customItem : customItemByType) {
                if (customItem instanceof PokeballItem pokeballItem) {
                    if (!arrow.hasMetadata(pokeballItem.getKey()))
                        continue;

                    event.setCancelled(true);
                    if(!(arrow.getShooter() instanceof Player shooter))
                        return;

                    if (hitPlayer.hasPermission(pokeballItem.getPermissionByPass()))
                        return;

                    arrow.remove();
                    if (WorldGuardUtil.isDeniedFlag(hitPlayer.getLocation(), hitPlayer, Flags.PVP))
                        return;

                    if (WorldGuardUtil.isDeniedFlag(shooter.getLocation(), shooter, Flags.PVP))
                        return;

                    if (hitPlayer.hasPermission(pokeballItem.getPermissionByPass()))
                        return;

                    double winTicket = this.random.nextDouble() * 100.0D;

                    if (winTicket <= pokeballItem.getChance()) {

                        if (shooter.isFlying())
                            return;

                        pokeballItem.notifyYourself(shooter);
                        pokeballItem.notifyOpponents(hitPlayer);
                        hitPlayer.teleport(shooter);
                    }
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = event.getItem();
        if (itemStack == null)
            return;
        Optional<CustomItem> customItemByType = this.itemManager.findCustomItemByType(ItemType.POKE_BALL, itemStack);
        if (customItemByType.isEmpty() || !(customItemByType.get() instanceof PokeballItem pokeballItem))
            return;

        event.setCancelled(true);
        if (!pokeballItem.isEnabled())
            return;

        if (WorldGuardUtil.isDeniedFlag(player.getLocation(), player, Flags.PVP))
            return;

        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_BLOCK && action != Action.RIGHT_CLICK_AIR)
            return;

        double cooldownSeconds = this.cooldownManager.getCooldownSecond(player, (CustomItem)pokeballItem);
        if (cooldownSeconds <= 0.0D || player.hasPermission(pokeballItem.getPermissionBypass())) {
            this.cooldownManager.setCooldown(player, pokeballItem);
            pokeballItem.throwEntity(player);
            pokeballItem.playSound(player.getLocation());
            pokeballItem.takeUsage(player, itemStack, event.getHand());
        } else {
            MessageUtil.sendMessage(player, this.fileManager.getLangConfig().getHasCooldown().replace("{time}", String.valueOf(cooldownSeconds)));
        }
    }
}
