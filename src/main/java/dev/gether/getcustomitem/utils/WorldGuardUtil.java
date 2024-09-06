package dev.gether.getcustomitem.utils;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import dev.gether.getconfig.utils.MessageUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;


public class WorldGuardUtil {

    public static boolean isDeniedFlag(Location location, Player player, StateFlag stateFlag) {
        LocalPlayer localPlayer = player != null ? WorldGuardPlugin.inst().wrapPlayer(player) : null;
        RegionContainer regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = regionContainer.createQuery();

        com.sk89q.worldedit.util.Location loc = BukkitAdapter.adapt(location);

        if(isInRegion(loc)) {
            boolean status = query.testState(loc, localPlayer, stateFlag);
            return !status;
        } else {
            boolean status = canUseInGlobal(BukkitAdapter.adapt(location.getWorld()), stateFlag);
            boolean finalStatus = location.getWorld() != null && status;
            return !finalStatus;
        }
    }

    public static boolean isInRegion(Player player) {
        return isInRegion(BukkitAdapter.adapt(player.getLocation()));
    }

    public static boolean isInRegion(com.sk89q.worldedit.util.Location location) {
        RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
        ApplicableRegionSet applicableRegions = query.getApplicableRegions(location);
        return !applicableRegions.getRegions().isEmpty();
    }

    private static boolean canUseInGlobal(World world, StateFlag stateFlag) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(world);
        if (regions != null) {
            ProtectedRegion globalRegion = regions.getRegion("__global__");
            if(globalRegion == null) return true;
            return globalRegion.getFlag(stateFlag) != StateFlag.State.DENY;
        }
        return true;
    }
}
