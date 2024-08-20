package dev.gether.getcustomitem.item.manager;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class MagicTotemManager {

    private HashMap<UUID, HashMap<Integer, ItemStack>> playersData = new HashMap<>();

    public Optional<HashMap<Integer, ItemStack>> getSavedItemsForPlayer(UUID uuid) {
        return Optional.ofNullable(playersData.get(uuid));
    }
    public void saveItems(Player player, HashMap<Integer, ItemStack> items) {
        playersData.put(player.getUniqueId(), items);
    }

    public void clearCache(Player player) {
        playersData.remove(player.getUniqueId());
    }
}
