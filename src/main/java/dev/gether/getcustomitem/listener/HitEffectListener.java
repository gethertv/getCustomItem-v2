package dev.gether.getcustomitem.listener;

import com.sk89q.worldguard.protection.flags.Flags;
import dev.gether.getconfig.utils.MessageUtil;
import dev.gether.getconfig.utils.PotionConverUtil;
import dev.gether.getcustomitem.file.FileManager;
import dev.gether.getcustomitem.cooldown.CooldownManager;
import dev.gether.getcustomitem.item.CustomItem;
import dev.gether.getcustomitem.item.ItemManager;
import dev.gether.getcustomitem.item.ItemType;
import dev.gether.getcustomitem.item.customize.FrozenSword;
import dev.gether.getcustomitem.item.customize.HitEffectItem;
import dev.gether.getcustomitem.utils.WorldGuardUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public class HitEffectListener implements Listener {

    private final ItemManager itemManager;
    private final CooldownManager cooldownManager;
    private final FileManager fileManager;
    private final Random random = new Random();

    public HitEffectListener(ItemManager itemManager,
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

            ItemStack itemStack = damager.getInventory().getItemInMainHand();
            Optional<CustomItem> customItemByType = itemManager.findCustomItemByType(ItemType.HIT_EFFECT, itemStack);
            if(customItemByType.isEmpty() || !(customItemByType.get() instanceof HitEffectItem hitEffectItem))
                return;

            // check the item is enabled / if not then cancel
            if(!hitEffectItem.isEnabled())
                return;

//            event.setCancelled(true);

            // check is not the npc
            boolean isCitizensNPC = victim.hasMetadata("NPC");
            if(isCitizensNPC) return;

            /* world-guard section */
            // check the using player is in PVP region
            if(WorldGuardUtil.isDeniedFlag(damager.getLocation(), damager, Flags.PVP)) {
                return;
            }
            if(WorldGuardUtil.isDeniedFlag(victim.getLocation(), victim, Flags.PVP)) {
                return;
            }

            double cooldownSeconds = cooldownManager.getCooldownSecond(damager, hitEffectItem);
            if(cooldownSeconds <= 0 || damager.hasPermission(hitEffectItem.getPermissionBypass())) {

                // set cooldown
                cooldownManager.setCooldown(damager, hitEffectItem);

                double winTicket = random.nextDouble() * 100;
                if(winTicket <= hitEffectItem.getChance()) {

                    // particles and sound
                    hitEffectItem.playSound(damager.getLocation()); // play sound

                    // verify a value to usage of item
                    hitEffectItem.takeUsage(damager, itemStack, EquipmentSlot.HAND);

                    // alerts
                    hitEffectItem.notifyYourself(damager);

                    hitEffectItem.notifyOpponents(victim); // alert opponent

                    if(hitEffectItem.isOpponents()) {
                        giveEffect(victim, hitEffectItem);
                    }
                    if(hitEffectItem.isYourSelf()) {
                        giveEffect(damager, hitEffectItem);
                    }

                }

            } else {
                MessageUtil.sendMessage(damager, fileManager.getLangConfig().getHasCooldown().replace("{time}", String.valueOf(cooldownSeconds)));
            }

        }

    }

    private void giveEffect(Player player, HitEffectItem hitEffectItem) {
        List<PotionEffect> activePotionEffect = PotionConverUtil.getPotionEffectFromConfig(hitEffectItem.getPotionEffectConfigs());
        activePotionEffect.forEach(player::addPotionEffect); // set new effect
    }

    /**
     * cancel place/use custom item
     * @param event
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {

        ItemStack itemStack = event.getItem(); // using item

        if(itemStack == null)
            return;

        Optional<CustomItem> customItemByType = itemManager.findCustomItemByType(ItemType.FROZEN_SWORD, itemStack);
        if (customItemByType.isEmpty() || !(customItemByType.get() instanceof FrozenSword))
            return;

        event.setCancelled(true);
    }





}
