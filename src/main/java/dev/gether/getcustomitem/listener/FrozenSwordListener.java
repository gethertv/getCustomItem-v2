package dev.gether.getcustomitem.listener;

import com.sk89q.worldguard.protection.flags.Flags;
import dev.gether.getconfig.utils.MessageUtil;
import dev.gether.getcustomitem.cooldown.CooldownManager;
import dev.gether.getcustomitem.file.FileManager;
import dev.gether.getcustomitem.item.CustomItem;
import dev.gether.getcustomitem.item.ItemManager;
import dev.gether.getcustomitem.item.ItemType;
import dev.gether.getcustomitem.item.customize.FrozenSword;
import dev.gether.getcustomitem.item.manager.FrozenManager;
import dev.gether.getcustomitem.utils.WorldGuardUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;
import java.util.Random;

public class FrozenSwordListener implements Listener {

    private final ItemManager itemManager;
    private final CooldownManager cooldownManager;
    private final FileManager fileManager;

    private final FrozenManager frozenManager;
    private final Random random = new Random();

    public FrozenSwordListener(ItemManager itemManager,
                               CooldownManager cooldownManager,
                               FileManager fileManager,
                               FrozenManager frozenManager) {
        this.itemManager = itemManager;
        this.cooldownManager = cooldownManager;
        this.fileManager = fileManager;
        this.frozenManager = frozenManager;
    }


    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if(event.isCancelled()) return;

        Player player = event.getPlayer();

        double frozenTime = frozenManager.getFrozenTime(player);
        if(frozenTime > 0) {
            event.setCancelled(true);
        } else {
            frozenManager.cleanCache(player);
        }

    }
    @EventHandler
    public void onPlayerInteract(EntityDamageByEntityEvent event) {
        if(event.isCancelled()) return;

        if(event.getDamager() instanceof Player damager &&
                event.getEntity() instanceof Player victim
        ) {

            ItemStack itemStack = damager.getInventory().getItemInMainHand();
            Optional<CustomItem> customItemByType = itemManager.findCustomItemByType(ItemType.FROZEN_SWORD, itemStack);
            if(customItemByType.isEmpty() || !(customItemByType.get() instanceof FrozenSword frozenSword))
                return;

            // check the item is enabled / if not then cancel
            if(!frozenSword.isEnabled())
                return;

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

            double cooldownSeconds = cooldownManager.getCooldownSecond(damager, frozenSword);
            if(cooldownSeconds <= 0 || damager.hasPermission(frozenSword.getPermissionBypass())) {

                // set cooldown
                cooldownManager.setCooldown(damager, frozenSword);

                double winTicket = random.nextDouble() * 100;
                if(winTicket <= frozenSword.getChanceToFrozen()) {

                    // particles and sound
                    frozenSword.playSound(damager.getLocation()); // play sound

                    // alerts
                    frozenSword.notifyYourself(damager);
                    frozenSword.notifyOpponents(victim);

                    // freeze the player
                    frozenManager.freeze(victim, frozenSword);

                    // verify a value to usage of item
                    frozenSword.takeUsage(damager, itemStack, EquipmentSlot.HAND);

                }

            } else {
                MessageUtil.sendMessage(damager, fileManager.getLangConfig().getHasCooldown().replace("{time}", String.valueOf(cooldownSeconds)));
            }


        }

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