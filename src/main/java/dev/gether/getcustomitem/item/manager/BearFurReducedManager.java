package dev.gether.getcustomitem.item.manager;

import dev.gether.getcustomitem.item.customize.BearFurItem;
import dev.gether.getcustomitem.item.model.UseItemData;
import org.bukkit.entity.Player;

import java.util.*;

public class BearFurReducedManager {

    private Map<UUID, UseItemData> playersData = new HashMap<>();

    public double getReducedTime(Player player) {
        // check the player has active effect //  default == 0 - no effect
        UseItemData useItemData = playersData.get(player.getUniqueId());
        long currentTimeMillis = System.currentTimeMillis(); // current time

        // not found effect time for player or effect time has expired
        if(useItemData == null || useItemData.getTimeMS() <= currentTimeMillis) {
            return 0;
        }

        // calc how many second you get reduced damage
        long diffTime = useItemData.getTimeMS() - currentTimeMillis; // there is a difference in MS like: 2500 - 2.5 sec
        double secondsLeft = (double) diffTime / 1000; // getting effect of reduced damage (seconds) but with wrong format - 1.44443423 sec

        return Double.parseDouble(String.format(Locale.US, "%.2f", secondsLeft)); // change double to 2 decimal places
    }

    public Optional<UseItemData> findUseItemDataByUUID(UUID uuid) {
        return Optional.ofNullable(playersData.get(uuid));
    }

    public void reducedDamage(Player player, BearFurItem bearFurItem) {
        if(bearFurItem.getSeconds() == 0)
            return;

        long reducedEndMS = System.currentTimeMillis() + 1000L * bearFurItem.getSeconds();

        // put to map
        playersData.put(player.getUniqueId(), new UseItemData(bearFurItem, reducedEndMS));
    }

    public void cleanCache(Player player) {
        playersData.remove(player.getUniqueId());
    }


}
