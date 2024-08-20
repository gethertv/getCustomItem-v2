package dev.gether.getcustomitem.listener;

import com.sk89q.worldguard.protection.flags.Flags;
import dev.gether.getconfig.utils.MessageUtil;
import dev.gether.getcustomitem.file.FileManager;
import dev.gether.getcustomitem.file.config.Config;
import dev.gether.getcustomitem.cooldown.CooldownManager;
import dev.gether.getcustomitem.item.CustomItem;
import dev.gether.getcustomitem.item.ItemManager;
import dev.gether.getcustomitem.item.ItemType;
import dev.gether.getcustomitem.item.customize.LightningItem;
import dev.gether.getcustomitem.utils.WorldGuardUtil;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;
import java.util.Random;

public class LightningItemListener implements Listener {

    private final ItemManager itemManager;
    private final CooldownManager cooldownManager;
    private final FileManager fileManager;
    private final Random random = new Random();

    public LightningItemListener(ItemManager itemManager,
                                 CooldownManager cooldownManager,
                                 FileManager fileManager) {
        this.itemManager = itemManager;
        this.cooldownManager = cooldownManager;
        this.fileManager = fileManager;
    }

    /**
     * cancel place/use custom item
     *
     * @param event
     */
    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {

        if (event.getDamager() instanceof Player damager &&
                event.getEntity() instanceof Player victim
        ) {

            ItemStack itemStack = damager.getInventory().getItemInMainHand();
            Optional<CustomItem> customItemByType = itemManager.findCustomItemByType(ItemType.LIGHTNING_ITEM, itemStack);
            if (customItemByType.isEmpty() || !(customItemByType.get() instanceof LightningItem lightningItem))
                return;

            // check the item is enabled / if not then cancel
            if (!lightningItem.isEnabled())
                return;

            // check is not the npc
            boolean isCitizensNPC = victim.hasMetadata("NPC");
            if (isCitizensNPC) return;

            /* world-guard section */
            // check the using player is in PVP region
            if (WorldGuardUtil.isDeniedFlag(damager.getLocation(), damager, Flags.PVP)) {
                return;
            }
            if (WorldGuardUtil.isDeniedFlag(victim.getLocation(), victim, Flags.PVP)) {
                return;
            }

            double cooldownSeconds = cooldownManager.getCooldownSecond(damager, lightningItem);
            if (cooldownSeconds <= 0 || damager.hasPermission(lightningItem.getPermissionBypass())) {

                // set cooldown
                cooldownManager.setCooldown(damager, lightningItem);

                double winTicket = random.nextDouble() * 100;
                if (winTicket <= lightningItem.getChance()) {

                    // particles and sound
                    lightningItem.playSound(damager.getLocation()); // play sound

                    // verify a value to usage of item
                    lightningItem.takeUsage(damager, itemStack, EquipmentSlot.HAND);

                    // alerts
                    lightningItem.notifyYourself(damager);

                    lightningItem.notifyOpponents(victim); // alert opponent

                    LightningStrike lightning = (LightningStrike) victim.getWorld().spawnEntity(victim.getLocation(), EntityType.LIGHTNING);
                    lightning.setFireTicks(0);
//                    victim.getWorld().strikeLightningEffect(victim.getLocation());
                    double maxHealth = victim.getMaxHealth();
                    double damageAmount = maxHealth * lightningItem.getMultiplyDamage();

                    victim.damage(damageAmount);

                }

            } else {
                MessageUtil.sendMessage(damager, fileManager.getLangConfig().getHasCooldown()
                        .replace("{time}", String.valueOf(cooldownSeconds))
                );
            }

        }
    }

}
