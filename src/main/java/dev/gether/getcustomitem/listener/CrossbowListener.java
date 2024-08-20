package dev.gether.getcustomitem.listener;

import com.sk89q.worldguard.protection.flags.Flags;
import dev.gether.getconfig.utils.ItemBuilder;
import dev.gether.getconfig.utils.MessageUtil;
import dev.gether.getcustomitem.GetCustomItem;
import dev.gether.getcustomitem.file.FileManager;
import dev.gether.getcustomitem.file.config.Config;
import dev.gether.getcustomitem.cooldown.CooldownManager;
import dev.gether.getcustomitem.item.CustomItem;
import dev.gether.getcustomitem.item.ItemManager;
import dev.gether.getcustomitem.item.ItemType;
import dev.gether.getcustomitem.item.customize.CrossBowItem;
import dev.gether.getcustomitem.utils.WorldGuardUtil;
import org.bukkit.Material;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public class CrossbowListener implements Listener {

    private final GetCustomItem plugin;
    private final ItemManager itemManager;
    private final CooldownManager cooldownManager;
    private final String metadataKey = "getcustomitem_arrow";
    private final Random random = new Random();
    private final FileManager fileManager;

    public CrossbowListener(GetCustomItem plugin,
                            ItemManager itemManager,
                            CooldownManager cooldownManager,
                            FileManager fileManager) {
        this.plugin = plugin;
        this.itemManager = itemManager;
        this.cooldownManager = cooldownManager;
        this.fileManager = fileManager;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {

        ItemStack itemStack = event.getItem();

        if(itemStack == null)
            return;

        Optional<CustomItem> customItemByType = itemManager.findCustomItemByType(ItemType.CROSSBOW, itemStack);
        if(customItemByType.isEmpty() || !(customItemByType.get() instanceof CrossBowItem crossBowItem))
            return;

        // check the item is enabled / if not then cancel
        if(!crossBowItem.isEnabled())
            return;

        if(itemStack.getType() != Material.CROSSBOW)
            return;


        CrossbowMeta crossbowMeta = (CrossbowMeta) itemStack.getItemMeta();
        if(!crossbowMeta.hasChargedProjectiles()) {
            event.setCancelled(true);
            crossbowMeta.addChargedProjectile(ItemBuilder.of(Material.FIREWORK_ROCKET)
                            .name("headshot")
                            .glow(true)
                    .build());

            itemStack.setItemMeta(crossbowMeta);
        }


    }
    @EventHandler
    public void onEntityShootBow(EntityShootBowEvent event) {

        if (event.getEntity() instanceof Player shooter &&
                event.getProjectile() instanceof Firework firework) {

            ItemStack bow = event.getBow();
            Optional<CustomItem> customItemByType = itemManager.findCustomItemByType(ItemType.CROSSBOW, bow);
            if(customItemByType.isEmpty() || !(customItemByType.get() instanceof CrossBowItem crossBowItem))
                return;

            // check the item is enabled / if not then cancel
            if(!crossBowItem.isEnabled())
                return;

            double cooldownSeconds = cooldownManager.getCooldownSecond(shooter, crossBowItem);
            if(cooldownSeconds <= 0 || shooter.hasPermission(crossBowItem.getPermissionBypass())) {
                crossBowItem.runParticles(firework); // particles
                crossBowItem.playSound(firework.getLocation()); // play sound

                // add custom meta to arrow for help with verify custom arrow
                firework.setMetadata(metadataKey, new FixedMetadataValue(plugin, crossBowItem.getKey()));
                cooldownManager.setCooldown(shooter, crossBowItem); // set cooldown
                crossBowItem.maxRangeTask(firework, crossBowItem.getMaxRange());
                // verify a value to usage of item
                crossBowItem.takeUsage(shooter, bow, event.getHand());

            } else {
                event.setCancelled(true);
                MessageUtil.sendMessage(shooter, fileManager.getLangConfig().getHasCooldown().replace("{time}", String.valueOf(cooldownSeconds)));
            }

        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {

        if (event.getEntity() instanceof Firework firework &&
                firework.hasMetadata(metadataKey) &&
                event.getHitEntity() instanceof Player hitPlayer &&
                firework.getShooter() instanceof Player shooter) {


            List<MetadataValue> metadata = firework.getMetadata(metadataKey);
            if(metadata.isEmpty())
                return;

            String key = metadata.get(0).asString();
            Optional<CustomItem> customItemByKey = itemManager.findCustomItemByKey(key);
            if(customItemByKey.isEmpty() || !(customItemByKey.get() instanceof CrossBowItem crossBowItem))
                return;

            // take a permission which ignore received effect
            if(hitPlayer.hasPermission(crossBowItem.getPermissionIgnoreEffect()))
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

            // check is not the npc
            boolean isCitizensNPC = hitPlayer.hasMetadata("NPC");
            if(isCitizensNPC) return;

            double winTicket = random.nextDouble() * 100;
            if(winTicket <= crossBowItem.getChance() ) {

                // alert
                crossBowItem.notifyYourself(shooter);
                crossBowItem.notifyOpponents(hitPlayer);

                hitPlayer.teleport(shooter);

            }
        }
    }


}
