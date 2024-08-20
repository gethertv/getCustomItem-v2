package dev.gether.getcustomitem.listener;

import dev.gether.getconfig.utils.MessageUtil;
import dev.gether.getcustomitem.file.FileManager;
import dev.gether.getcustomitem.file.config.Config;
import dev.gether.getcustomitem.cooldown.CooldownManager;
import dev.gether.getcustomitem.item.ItemManager;
import dev.gether.getcustomitem.item.ItemType;
import dev.gether.getcustomitem.item.customize.MagicTotemItem;
import dev.gether.getcustomitem.item.manager.MagicTotemManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;

public class MagicTotemListener implements Listener {

    private final ItemManager itemManager;
    private final CooldownManager cooldownManager;
    private final FileManager fileManager;
    private final Random random = new Random();
    private final MagicTotemManager magicTotemManager;

    public MagicTotemListener(ItemManager itemManager,
                              CooldownManager cooldownManager,
                              FileManager fileManager, MagicTotemManager magicTotemManager) {
        this.itemManager = itemManager;
        this.cooldownManager = cooldownManager;
        this.fileManager = fileManager;
        this.magicTotemManager = magicTotemManager;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDeath(PlayerDeathEvent event) {

        Player player = event.getEntity();
        Player killer = player.getKiller();

        MagicTotemItem magicTotemItem = findMagicTotem(player);

        if (magicTotemItem == null || !magicTotemItem.isEnabled()) return;

        double cooldownSeconds = cooldownManager.getCooldownSecond(player, magicTotemItem);
        if (cooldownSeconds > 0 && !player.hasPermission(magicTotemItem.getPermissionBypass())) {
            MessageUtil.sendMessage(player, fileManager.getLangConfig().getHasCooldown().replace("{time}", String.valueOf(cooldownSeconds)));
            return;
        }

        // alerts
        magicTotemItem.notifyYourself(player);

        if(killer != null)
            magicTotemItem.notifyOpponents(killer);

        cooldownManager.setCooldown(player, magicTotemItem); // set cooldown
        processDeathWithTotem(player, magicTotemItem);
        event.getDrops().clear(); // prevent all items from dropping
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        magicTotemManager.getSavedItemsForPlayer(player.getUniqueId()).ifPresent(items -> {
            items.forEach(player.getInventory()::setItem);
            magicTotemManager.clearCache(player);
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityResurrect(EntityResurrectEvent event) {
        if(event.isCancelled()) return;

        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        MagicTotemItem magicTotemItem = findMagicTotem(player);

        if (magicTotemItem == null || !magicTotemItem.isEnabled()) return;

        double cooldownSeconds = cooldownManager.getCooldownSecond(player, magicTotemItem);
        if (cooldownSeconds <= 0 || player.hasPermission(magicTotemItem.getPermissionBypass())) {
            event.setCancelled(true);
        } else {
            MessageUtil.sendMessage(player, fileManager.getLangConfig().getHasCooldown().replace("{time}", String.valueOf(cooldownSeconds)));
        }
    }

    private MagicTotemItem findMagicTotem(Player player) {
        return Stream.of(player.getInventory().getItemInMainHand(), player.getInventory().getItemInOffHand())
                .map(item -> itemManager.findCustomItemByType(ItemType.MAGIC_TOTEM, item))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(MagicTotemItem.class::isInstance)
                .map(MagicTotemItem.class::cast)
                .findFirst()
                .orElse(null);
    }
    private void processDeathWithTotem(Player player, MagicTotemItem magicTotemItem) {
        HashMap<Integer, ItemStack> savedItems = new HashMap<>();

        // verify a value to usage of item
        magicTotemItem.takeUsage(player);

        // after remove the magic totem, take a player inventory to get actually items
        PlayerInventory inventory = player.getInventory();


        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (item == null || item.getType() == Material.AIR) continue;

            double chance = random.nextDouble() * 100;
            if (magicTotemItem.getChanceLostItem() >= chance) {
                savedItems.put(i, item.clone());
            } else {
                player.getWorld().dropItemNaturally(player.getLocation(), item);
            }
        }

        // save the selected items to be restored after respawn
        magicTotemManager.saveItems(player, savedItems);

    }

}
