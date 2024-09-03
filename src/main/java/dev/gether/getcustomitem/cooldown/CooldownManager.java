package dev.gether.getcustomitem.cooldown;

import dev.gether.getcustomitem.file.FileManager;
import dev.gether.getcustomitem.item.CustomItem;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CooldownManager {

    private final FileManager fileManager;
    private Map<String, Long> cooldowns = new HashMap<>(); // key: UUID + ItemType, value: end time in ms


    public CooldownManager(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    /**
     *
     * @param player
     * @param customItem
     * @return cooldown time in second with 2 decimal places
     */
    public double getCooldownSecond(Player player, CustomItem customItem) {
        // check the item has cooldown //  default == 0 - no cooldown
        if(customItem.getCooldown() == 0)
            return 0;

        String key = getPlayerKeyItem(player, customItem); // key with specified user and item
        Long cooldownEndMS = cooldowns.get(key);
        long currentTimeMillis = System.currentTimeMillis(); // current time

        // no cooldown found or cooldown has expired
        if(cooldownEndMS == null || cooldownEndMS <= currentTimeMillis) {
            return 0;
        }

        // calc how many second you must wait for use item
        long diffTime = cooldownEndMS - currentTimeMillis; // there is a difference in MS like: 2500 - 2.5 sec
        double secondsLeft = (double) diffTime / 1000; // getting cooldown (second) but with wrong format - 1.44443423 sec

        return Double.parseDouble(String.format(Locale.US, "%.2f", secondsLeft)); // change double to 2 decimal places
    }

    public void setCooldown(Player player, CustomItem customItem) {
        if(customItem.getCooldown() == 0)
            return;

        String key = getPlayerKeyItem(player, customItem);
        long cooldownEndMS = System.currentTimeMillis() + 1000L * customItem.getCooldown();

        // put to map
        cooldowns.put(key, cooldownEndMS);

        // check a visual cooldown is enabled
        if(customItem.isVisualCooldown() && !player.hasPermission(customItem.getPermissionBypass()))
            player.setCooldown(customItem.getItemStack().getType(), 20 * customItem.getCooldown());
    }

    private String getPlayerKeyItem(Player player, CustomItem customItem) {
        return player.getUniqueId() + customItem.getCategoryName();
    }

    /**
     * clear user cache
     * @param player
     */
    public void clearAllCache(Player player) {
        fileManager.getCustomItems().forEach(customItem -> {
            String key = getPlayerKeyItem(player, customItem);
            cooldowns.remove(key);
        });
    }

    public boolean isOnCooldown(String key) {
        if (!cooldowns.containsKey(key)) {
            return false;
        }
        return System.currentTimeMillis() < cooldowns.get(key);
    }

    /**
     * Get the remaining cooldown time in milliseconds.
     *
     * @param key The cooldown key to check
     * @return The remaining cooldown time in milliseconds, or 0 if no cooldown is active
     */
    public long getCooldownTime(String key) {
        if (!cooldowns.containsKey(key)) {
            return 0;
        }
        long cooldownTime = cooldowns.get(key) - System.currentTimeMillis();
        return Math.max(cooldownTime, 0);
    }

    /**
     * Set a cooldown for the given key.
     *
     * @param key The cooldown key to set
     * @param seconds The duration of the cooldown in seconds
     */
    public void setCooldown(String key, int seconds) {
        long cooldownTime = System.currentTimeMillis() + (seconds * 1000L);
        cooldowns.put(key, cooldownTime);
    }
}
