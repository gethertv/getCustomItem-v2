package dev.gether.getcustomitem.listener;

import com.sk89q.worldguard.protection.flags.Flags;
import dev.gether.getconfig.utils.MessageUtil;
import dev.gether.getcustomitem.cooldown.CooldownManager;
import dev.gether.getcustomitem.file.FileManager;
import dev.gether.getcustomitem.item.CustomItem;
import dev.gether.getcustomitem.item.ItemManager;
import dev.gether.getcustomitem.item.ItemType;
import dev.gether.getcustomitem.item.customize.ShieldItem;
import dev.gether.getcustomitem.utils.WorldGuardUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;

public class ShieldItemListener implements Listener {


    private final ItemManager itemManager;
    private final CooldownManager cooldownManager;
    private final FileManager fileManager;
    private final Random random = new Random();

    public ShieldItemListener(ItemManager itemManager,
                              CooldownManager cooldownManager,
                              FileManager fileManager) {
        this.itemManager = itemManager;
        this.cooldownManager = cooldownManager;
        this.fileManager = fileManager;
    }

    @EventHandler
    public void onPlayerInteract(EntityDamageByEntityEvent event) {

        if(event.getDamager() instanceof Player damager &&
                event.getEntity() instanceof Player victim
        ) {
            /* world-guard section */
            // check the using player is in PVP region
            if(WorldGuardUtil.isDeniedFlag(damager.getLocation(), damager, Flags.PVP)) {
                return;
            }
            if(WorldGuardUtil.isDeniedFlag(victim.getLocation(), victim, Flags.PVP)) {
                return;
            }

            // check is not the npc
            boolean isCitizensNPC = victim.hasMetadata("NPC");
            if(isCitizensNPC) return;

            List<CustomItem> customItemByType = itemManager.findAllCustomItemByType(ItemType.SHIELD_ITEM);
            if(customItemByType.isEmpty())
                return;


            for (CustomItem customItem : customItemByType) {
                if (!(customItem instanceof ShieldItem shieldItem))
                    continue;

                // check the item is enabled / if not then cancel
                if(!shieldItem.isEnabled())
                    return;

                for (EquipmentSlot equipmentSlot : shieldItem.getEquipmentSlots()) {
                    ItemStack itemStack = victim.getInventory().getItem(equipmentSlot);
                    if(shieldItem.isCustomItem(itemStack)) {
                        double cooldownSeconds = cooldownManager.getCooldownSecond(victim, shieldItem);
                        if(cooldownSeconds <= 0 || victim.hasPermission(shieldItem.getPermissionBypass())) {

                            // set cooldown
                            cooldownManager.setCooldown(damager, shieldItem);

                            double winTicket = random.nextDouble() * 100;
                            if(winTicket <= shieldItem.getBlockChance()) {
                                event.setCancelled(true);

                                // particles and sound
                                shieldItem.playSound(victim.getLocation()); // play sound

                                // alerts
                                shieldItem.notifyYourself(damager);
                                shieldItem.notifyOpponents(victim);

                                // verify a value to usage of item
                                shieldItem.takeUsage(victim, itemStack, equipmentSlot);
                                return;
                            }
                        } else {
                            MessageUtil.sendMessage(victim, fileManager.getLangConfig().getHasCooldown().replace("{time}", String.valueOf(cooldownSeconds)));
                        }
                    }
                }

            }
        }

    }
}