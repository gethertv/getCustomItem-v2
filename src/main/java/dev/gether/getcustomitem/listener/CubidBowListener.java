package dev.gether.getcustomitem.listener;

import com.sk89q.worldguard.protection.flags.Flags;
import dev.gether.getconfig.utils.MessageUtil;
import dev.gether.getconfig.utils.PotionConverUtil;
import dev.gether.getcustomitem.GetCustomItem;
import dev.gether.getcustomitem.cooldown.CooldownManager;
import dev.gether.getcustomitem.file.FileManager;
import dev.gether.getcustomitem.item.CustomItem;
import dev.gether.getcustomitem.item.ItemManager;
import dev.gether.getcustomitem.item.ItemType;
import dev.gether.getcustomitem.item.customize.CupidBowItem;
import dev.gether.getcustomitem.utils.WorldGuardUtil;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public class CubidBowListener implements Listener {

    private final GetCustomItem plugin;
    private final ItemManager itemManager;
    private final CooldownManager cooldownManager;
    private final Random random = new Random();
    private final FileManager fileManager;

    public CubidBowListener(GetCustomItem plugin,
                            ItemManager itemManager,
                            CooldownManager cooldownManager,
                            FileManager fileManager) {
        this.plugin = plugin;
        this.itemManager = itemManager;
        this.cooldownManager = cooldownManager;
        this.fileManager = fileManager;
    }


    @EventHandler
    public void onEntityShootBow(EntityShootBowEvent event) {

        if (event.getEntity() instanceof Player shooter &&
                event.getProjectile() instanceof Arrow arrow) {

            ItemStack bow = event.getBow();
            Optional<CustomItem> customItemByType = itemManager.findCustomItemByType(ItemType.CUPIDS_BOW, bow);
            if (customItemByType.isEmpty() || !(customItemByType.get() instanceof CupidBowItem cupidBowItem))
                return;

            // check the item is enabled / if not then cancel
            if (!cupidBowItem.isEnabled())
                return;

            double cooldownSeconds = cooldownManager.getCooldownSecond(shooter, cupidBowItem);
            if (cooldownSeconds <= 0 || shooter.hasPermission(cupidBowItem.getPermissionBypass())) {
                cupidBowItem.runParticles(arrow); // particles
                cupidBowItem.playSound(arrow.getLocation()); // play sound

                cupidBowItem.maxRangeTask(arrow, cupidBowItem.getMaxRange());
                // add custom meta to arrow for help with verify custom arrow
                arrow.setMetadata(cupidBowItem.getKey(), new FixedMetadataValue(plugin, cupidBowItem.getKey()));
                cooldownManager.setCooldown(shooter, cupidBowItem); // set cooldown

                // verify a value to usage of item
                cupidBowItem.takeUsage(shooter, bow, event.getHand());

            } else {
                event.setCancelled(true);
                MessageUtil.sendMessage(shooter, fileManager.getLangConfig().getHasCooldown().replace("{time}", String.valueOf(cooldownSeconds)));
            }

        }
    }


    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Arrow arrow &&
                event.getHitEntity() instanceof Player hitPlayer &&
                arrow.getShooter() instanceof Player shooter) {

            List<CustomItem> customItemByKey = itemManager.findAllCustomItemByType(ItemType.CUPIDS_BOW);
            if (customItemByKey.isEmpty())
                return;

            for (CustomItem customItem : customItemByKey) {
                if (!(customItem instanceof CupidBowItem cupidBowItem))
                    continue;

                if(!arrow.hasMetadata(cupidBowItem.getKey()))
                    continue;

                // take a permission which ignore received effect
                if (hitPlayer.hasPermission(cupidBowItem.getPermissionIgnoreEffect()))
                    return;

                /* world-guard section */
                // check if the hit player is in the pvp region
                if (WorldGuardUtil.isDeniedFlag(hitPlayer.getLocation(), hitPlayer, Flags.PVP)) {
                    return;
                }

                // check if the shooter player is in the pvp region
                if (WorldGuardUtil.isDeniedFlag(shooter.getLocation(), shooter, Flags.PVP)) {
                    return;
                }
                // check is not the npc
                boolean isCitizensNPC = hitPlayer.hasMetadata("NPC");
                if (isCitizensNPC) return;

                double winTicket = random.nextDouble() * 100;
                if (winTicket <= cupidBowItem.getChance()) {

                    // alert
                    cupidBowItem.notifyYourself(shooter);
                    cupidBowItem.notifyOpponents(hitPlayer);

                    List<PotionEffect> activePotionEffect = PotionConverUtil.getPotionEffectFromConfig(cupidBowItem.getPotionEffectConfigs());
                    activePotionEffect.forEach(hitPlayer::addPotionEffect); // set new effect
                }
                return;
            }

        }
    }


}