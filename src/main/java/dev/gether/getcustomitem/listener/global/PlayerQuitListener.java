package dev.gether.getcustomitem.listener.global;

import dev.gether.getcustomitem.cooldown.CooldownManager;
import dev.gether.getcustomitem.item.manager.BearFurReducedManager;
import dev.gether.getcustomitem.item.manager.FrozenManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {


    private final BearFurReducedManager bearFurReducedManager;
    private final CooldownManager cooldownManager;
    private final FrozenManager frozenManager;

    public PlayerQuitListener(BearFurReducedManager bearFurReducedManager, CooldownManager cooldownManager, FrozenManager frozenManager) {
        this.bearFurReducedManager = bearFurReducedManager;
        this.cooldownManager = cooldownManager;
        this.frozenManager = frozenManager;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        // clear cache with cooldown
        cooldownManager.clearAllCache(player);
        // clear cache which is responsible for handling frozen player
        frozenManager.cleanCache(player);
        // clear cache responsible for give a reduced damage
        bearFurReducedManager.cleanCache(player);


    }
}
