package dev.gether.getcustomitem.listener;

import com.sk89q.worldguard.protection.flags.Flags;
import dev.gether.getconfig.utils.MessageUtil;
import dev.gether.getcustomitem.cooldown.CooldownManager;
import dev.gether.getcustomitem.file.FileManager;
import dev.gether.getcustomitem.item.CustomItem;
import dev.gether.getcustomitem.item.ItemManager;
import dev.gether.getcustomitem.item.ItemType;
import dev.gether.getcustomitem.item.customize.ShuffleInventoryItem;
import dev.gether.getcustomitem.utils.WorldGuardUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ShuffleInvListener implements Listener {

    private final ItemManager itemManager;
    private final CooldownManager cooldownManager;
    private final FileManager fileManager;
    private final Random random = new Random();

    public ShuffleInvListener(ItemManager itemManager,
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
            Optional<CustomItem> customItemByType = itemManager.findCustomItemByType(ItemType.SHUFFLE_ITEM, itemStack);
            if(customItemByType.isEmpty() || !(customItemByType.get() instanceof ShuffleInventoryItem shuffleInventoryItem))
                return;

            // check the item is enabled / if not then cancel
            if(!shuffleInventoryItem.isEnabled())
                return;

            event.setCancelled(true);

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


            double cooldownSeconds = cooldownManager.getCooldownSecond(damager, shuffleInventoryItem);
            if(cooldownSeconds <= 0 || damager.hasPermission(shuffleInventoryItem.getPermissionBypass())) {
                // set cooldown
                cooldownManager.setCooldown(damager, shuffleInventoryItem);

                double winTicket = random.nextDouble() * 100;
                if(winTicket <= shuffleInventoryItem.getChance()) {

                    // particles and sound
                    shuffleInventoryItem.playSound(damager.getLocation()); // play sound

                    // verify a value to usage of item
                    shuffleInventoryItem.takeUsage(damager, itemStack, EquipmentSlot.HAND);

                    // alerts
                    shuffleInventoryItem.notifyYourself(damager);

                    shuffleInventoryItem.notifyOpponents(victim); // alert opponent

                    shuffle(victim);
                }

            } else {
                MessageUtil.sendMessage(damager, fileManager.getLangConfig().getHasCooldown().replace("{time}", String.valueOf(cooldownSeconds)));
            }

        }

    }
    private void shuffle(Player player) {
        Inventory inventory = player.getInventory();

        // Store armor items
        ItemStack[] armorContents = player.getInventory().getArmorContents();

        // Get all non-null, non-armor items
        List<ItemStack> items = IntStream.range(0, 36) // Only consider main inventory slots
                .mapToObj(inventory::getItem)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // Clear only the main inventory
        for (int i = 0; i < 36; i++) {
            inventory.clear(i);
        }

        // Shuffle the items
        Collections.shuffle(items);

        // Place shuffled items back into the inventory
        int index = 0;
        for (int i = 0; i < 36; i++) {
            if (index < items.size()) {
                inventory.setItem(i, items.get(index++));
            }
        }

        // Restore armor items
        player.getInventory().setArmorContents(armorContents);
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

        Optional<CustomItem> customItemByType = itemManager.findCustomItemByType(ItemType.SHUFFLE_ITEM, itemStack);
        if (customItemByType.isEmpty() || !(customItemByType.get() instanceof ShuffleInventoryItem))
            return;

        event.setCancelled(true);
    }





}