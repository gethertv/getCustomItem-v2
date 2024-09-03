package dev.gether.getcustomitem.listener;

import com.sk89q.worldguard.protection.flags.Flags;
import dev.gether.getconfig.utils.MessageUtil;
import dev.gether.getconfig.utils.PlayerUtil;
import dev.gether.getcustomitem.cooldown.CooldownManager;
import dev.gether.getcustomitem.file.FileManager;
import dev.gether.getcustomitem.item.CustomItem;
import dev.gether.getcustomitem.item.ItemManager;
import dev.gether.getcustomitem.item.ItemType;
import dev.gether.getcustomitem.item.customize.ExplosionBallItem;
import dev.gether.getcustomitem.utils.WorldGuardUtil;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.MetadataValue;

import java.util.List;
import java.util.Optional;

public class ExplosionBallListener implements Listener {

    private final ItemManager itemManager;
    private final CooldownManager cooldownManager;
    private final FileManager fileManager;

    public ExplosionBallListener(ItemManager itemManager,
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

        Optional<CustomItem> customItemByType = itemManager.findCustomItemByType(ItemType.EXPLOSION_BALL, itemStack);
        if (customItemByType.isEmpty() || !(customItemByType.get() instanceof ExplosionBallItem explosionBallItem))
            return;

        // check the item is enabled / if not then cancel
        if (!explosionBallItem.isEnabled())
            return;

        event.setCancelled(true);
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_BLOCK && action != Action.RIGHT_CLICK_AIR) {
            return;
        }

        double cooldownSeconds = cooldownManager.getCooldownSecond(player, explosionBallItem);
        if (cooldownSeconds <= 0 || player.hasPermission(explosionBallItem.getPermissionBypass())) {

            // particles and sound
            explosionBallItem.playSound(player.getLocation()); // play sound

            // clean cobweb
            explosionBallItem.throwExplosionBall(player);

            // verify a value to usage of item
            explosionBallItem.takeUsage(player, itemStack, event.getHand());


        } else {
            MessageUtil.sendMessage(player, fileManager.getLangConfig().getHasCooldown().replace("{time}", String.valueOf(cooldownSeconds)));
        }

    }

    @EventHandler
    public void onExplostion(EntityExplodeEvent event) {
        if (event.getEntity() instanceof Fireball fireball) {
            List<CustomItem> customItemByType = itemManager.findAllCustomItemByType(ItemType.EXPLOSION_BALL);
            if (customItemByType.isEmpty())
                return;

            for (CustomItem customItem : customItemByType) {
                if(!(customItem instanceof ExplosionBallItem explosionBallItem))
                    continue;

                List<MetadataValue> metadata = fireball.getMetadata(explosionBallItem.getKey());
                if (metadata.isEmpty())
                    continue;

                List<Block> blocks = event.blockList();
                event.setCancelled(true);
                if (!explosionBallItem.isBreakBlocks())
                    return;

                blocks.forEach(block -> {
                    if (explosionBallItem.getWhitelistMaterial().contains(block.getType()))
                        return;

                    if (WorldGuardUtil.isDeniedFlag(block.getLocation(), null, Flags.BLOCK_BREAK)) {
                        return;
                    }
                });
                return;
            }

        }
    }

    @EventHandler
    public void onHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Fireball fireball) {
            List<CustomItem> customItemByType = itemManager.findAllCustomItemByType(ItemType.EXPLOSION_BALL);
            if (customItemByType.isEmpty())
                return;

            for (CustomItem customItem : customItemByType) {
                if (!(customItem instanceof ExplosionBallItem explosionBallItem))
                    continue;

                List<MetadataValue> metadata = fireball.getMetadata(explosionBallItem.getKey());
                if (metadata.isEmpty())
                    continue;

                event.setCancelled(true);

                if (WorldGuardUtil.isDeniedFlag(fireball.getLocation(), null, Flags.PVP)) {
                    return;
                }

                if (!(fireball.getShooter() instanceof Player shooter))
                    return;


                Location hitLocation = fireball.getLocation();

                hitLocation.getWorld()
                        .createExplosion(hitLocation,
                                explosionBallItem.getExplosionPower(),
                                explosionBallItem.isSetFire(),
                                explosionBallItem.isBreakBlocks(),
                                shooter
                        );

                List<Player> nearPlayers = PlayerUtil.findNearPlayers(hitLocation, (int) explosionBallItem.getExplosionPower());
                takeDurability(nearPlayers, explosionBallItem);

                // alert yourself
                explosionBallItem.notifyYourself(shooter);

                // set cooldown
                cooldownManager.setCooldown(shooter, explosionBallItem);
                return;
            }

        }
    }

    private void takeDurability(List<Player> nearPlayers, ExplosionBallItem explosionBallItem) {
        if (explosionBallItem.getDestroyDurability() <= 0) {
            return;
        }

        for (Player player : nearPlayers) {
            for (ItemStack armorPiece : player.getInventory().getArmorContents()) {
                if (armorPiece != null && armorPiece.getType().getMaxDurability() > 0) {
                    ItemMeta meta = armorPiece.getItemMeta();

                    if (meta instanceof Damageable) {
                        Damageable damageable = (Damageable) meta;
                        int currentDamage = damageable.getDamage();
                        int newDamage = currentDamage + explosionBallItem.getDestroyDurability();

                        damageable.setDamage(newDamage);
                        armorPiece.setItemMeta(meta);

                        if (newDamage >= armorPiece.getType().getMaxDurability()) {
                            player.getInventory().remove(armorPiece);
                        }
                    }
                }
            }
        }
    }



}