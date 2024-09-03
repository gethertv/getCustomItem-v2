package dev.gether.getcustomitem.listener;

import dev.gether.getconfig.utils.MessageUtil;
import dev.gether.getcustomitem.GetCustomItem;
import dev.gether.getcustomitem.cooldown.CooldownManager;
import dev.gether.getcustomitem.file.FileManager;
import dev.gether.getcustomitem.item.ItemManager;
import dev.gether.getcustomitem.item.ItemType;
import dev.gether.getcustomitem.item.customize.MagicTotemItem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;

public class MagicTotemListener implements Listener {

    private final ItemManager itemManager;
    private final CooldownManager cooldownManager;
    private final FileManager fileManager;
    private final Random random = new Random();

    public MagicTotemListener(ItemManager itemManager,
                              CooldownManager cooldownManager,
                              FileManager fileManager) {
        this.itemManager = itemManager;
        this.cooldownManager = cooldownManager;
        this.fileManager = fileManager;
    }

    @EventHandler
    public void onEntityResurrect(EntityResurrectEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        MagicTotemItem magicTotemItem = findMagicTotem(player);
        if (magicTotemItem == null) return;
        if (!magicTotemItem.isEnabled()) return;

        double cooldownSeconds = cooldownManager.getCooldownSecond(player, magicTotemItem);
        if (cooldownSeconds <= 0 || player.hasPermission(magicTotemItem.getPermissionBypass())) {
            cooldownManager.setCooldown(player, magicTotemItem);

            magicTotemItem.notifyYourself(player);

            processDeathWithTotem(player, magicTotemItem);

            Location spawnLocation = fileManager.getConfig().getSpawnLocation();
            if (spawnLocation != null) {
                Bukkit.getScheduler().runTask(GetCustomItem.getInstance(), () -> player.teleport(spawnLocation));
            }
        } else {
            MessageUtil.sendMessage(player, fileManager.getLangConfig().getHasCooldown()
                    .replace("{time}", String.valueOf(cooldownSeconds)));
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
        // verify a value to usage of item
        magicTotemItem.takeUsage(player);

        // after remove the magic totem, take a player inventory to get actually items
        PlayerInventory inventory = player.getInventory();

        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (item == null || item.getType() == Material.AIR) continue;

            double chance = random.nextDouble() * 100;
            if (chance < magicTotemItem.getChanceLostItem()) {
                player.getWorld().dropItemNaturally(player.getLocation(), item);
                inventory.setItem(i, null);
            }
        }
    }
}