package dev.gether.getcustomitem.listener;

import com.sk89q.worldguard.protection.flags.Flags;
import dev.gether.getconfig.utils.MessageUtil;
import dev.gether.getconfig.utils.PotionConverUtil;
import dev.gether.getcustomitem.cooldown.CooldownManager;
import dev.gether.getcustomitem.file.FileManager;
import dev.gether.getcustomitem.item.CustomItem;
import dev.gether.getcustomitem.item.ItemManager;
import dev.gether.getcustomitem.item.ItemType;
import dev.gether.getcustomitem.item.customize.ReflectionEffectItem;
import dev.gether.getcustomitem.utils.WorldGuardUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public class ReflectionEffectListener implements Listener {

    private final ItemManager itemManager;
    private final CooldownManager cooldownManager;
    private final FileManager fileManager;
    private final Random random = new Random();

    public ReflectionEffectListener(ItemManager itemManager,
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

            // check is not the npc
            boolean isCitizensNPC = victim.hasMetadata("NPC");
            if(isCitizensNPC) return;

            List<CustomItem> customItemByType = itemManager.findAllCustomItemByType(ItemType.REFLECTION_EFFECT);
            if(customItemByType.isEmpty())
                return;

            for (CustomItem customItem : customItemByType) {
                if(!(customItem instanceof ReflectionEffectItem reflectionEffectItem))
                    continue;

                handleEffect(victim, damager, reflectionEffectItem);
            }
        }

    }

    private void handleEffect(Player victim, Player damager, ReflectionEffectItem reflectionEffectItem) {
        // check the item is enabled / if not then cancel
        if(!reflectionEffectItem.isEnabled())
            return;

        for (EquipmentSlot equipmentSlot : reflectionEffectItem.getEquipmentSlots()) {
            ItemStack item = victim.getInventory().getItem(equipmentSlot);
            if(item==null || item.getType()== Material.AIR)
                continue;

            if(!reflectionEffectItem.isCustomItem(item))
                continue;

            /* world-guard section */
            // check the using player is in PVP region
            if(WorldGuardUtil.isDeniedFlag(damager.getLocation(), damager, Flags.PVP)) {
                return;
            }
            if(WorldGuardUtil.isDeniedFlag(victim.getLocation(), victim, Flags.PVP)) {
                return;
            }

            double cooldownSeconds = cooldownManager.getCooldownSecond(damager, reflectionEffectItem);
            if(cooldownSeconds <= 0 || damager.hasPermission(reflectionEffectItem.getPermissionBypass())) {

                // set cooldown
                cooldownManager.setCooldown(damager, reflectionEffectItem);

                double winTicket = random.nextDouble() * 100;
                if(winTicket <= reflectionEffectItem.getChance()) {

                    // particles and sound
                    reflectionEffectItem.playSound(damager.getLocation()); // play sound

                    // verify a value to usage of item
                    reflectionEffectItem.takeUsage(damager, item, EquipmentSlot.HAND);

                    // alerts
                    reflectionEffectItem.notifyYourself(damager);

                    reflectionEffectItem.notifyOpponents(victim); // alert opponent

                    if(reflectionEffectItem.isOpponents()) {
                        giveEffect(damager, reflectionEffectItem);
                    }
                    if(reflectionEffectItem.isYourSelf()) {
                        giveEffect(victim, reflectionEffectItem);
                    }

                }

            } else {
                MessageUtil.sendMessage(damager, fileManager.getLangConfig().getHasCooldown().replace("{time}", String.valueOf(cooldownSeconds)));
            }
        }
    }

    private void giveEffect(Player player, ReflectionEffectItem reflectionEffectItem) {
        List<PotionEffect> activePotionEffect = PotionConverUtil.getPotionEffectFromConfig(reflectionEffectItem.getPotionEffectConfigs());
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

        Optional<CustomItem> customItemByType = itemManager.findCustomItemByType(ItemType.REFLECTION_EFFECT, itemStack);
        if (customItemByType.isEmpty() || !(customItemByType.get() instanceof ReflectionEffectItem))
            return;

        event.setCancelled(true);
    }





}