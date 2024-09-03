package dev.gether.getcustomitem.listener;

import com.sk89q.worldguard.protection.flags.Flags;
import dev.gether.getconfig.utils.MessageUtil;
import dev.gether.getcustomitem.cooldown.CooldownManager;
import dev.gether.getcustomitem.file.FileManager;
import dev.gether.getcustomitem.item.CustomItem;
import dev.gether.getcustomitem.item.ItemManager;
import dev.gether.getcustomitem.item.ItemType;
import dev.gether.getcustomitem.item.customize.PushItem;
import dev.gether.getcustomitem.utils.WorldGuardUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Optional;
import java.util.Random;

public class PushItemListener implements Listener {

    private final ItemManager itemManager;
    private final CooldownManager cooldownManager;
    private final FileManager fileManager;
    private final Random random = new Random();

    public PushItemListener(ItemManager itemManager,
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
            Optional<CustomItem> customItemByType = itemManager.findCustomItemByType(ItemType.PUSH_ITEM, itemStack);
            if (customItemByType.isEmpty() || !(customItemByType.get() instanceof PushItem pushItem))
                return;

            // check the item is enabled / if not then cancel
            if (!pushItem.isEnabled())
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

            double cooldownSeconds = cooldownManager.getCooldownSecond(damager, pushItem);
            if (cooldownSeconds <= 0 || damager.hasPermission(pushItem.getPermissionBypass())) {

                // set cooldown
                cooldownManager.setCooldown(damager, pushItem);

                double winTicket = random.nextDouble() * 100;
                if (winTicket <= pushItem.getChance()) {

                    // calculate the direction and final position
                    Vector direction = damager.getLocation().getDirection().normalize().multiply(pushItem.getPushPower());
                    Location finalLocation = victim.getLocation().add(direction);

                    // check if the final location is in a non-PvP zone
                    if (WorldGuardUtil.isDeniedFlag(finalLocation, victim, Flags.PVP)) {
                        return;
                    }

                    // if it's safe to push, proceed with the push
                    pushItem.playSound(damager.getLocation()); // play sound

                    // verify a value to usage of item
                    pushItem.takeUsage(damager, itemStack, EquipmentSlot.HAND);

                    // alerts
                    pushItem.notifyYourself(damager);
                    pushItem.notifyOpponents(victim); // alert opponent

                    // Apply the push
                    victim.setVelocity(direction);

                }

            } else {
                MessageUtil.sendMessage(damager, fileManager.getLangConfig().getHasCooldown()
                        .replace("{time}", String.valueOf(cooldownSeconds))
                );
            }

        }
    }

}
