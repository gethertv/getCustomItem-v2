package dev.gether.getcustomitem.item.customize;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.protection.flags.Flags;
import dev.gether.getconfig.domain.Item;
import dev.gether.getconfig.domain.config.TitleMessage;
import dev.gether.getconfig.domain.config.sound.SoundConfig;
import dev.gether.getcustomitem.item.CustomItem;
import dev.gether.getcustomitem.item.ItemType;
import dev.gether.getcustomitem.utils.WorldGuardUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeName("anty_cobweb")
public class AntyCobweb extends CustomItem {

    private int radiusX;
    private int radiusY;


    public AntyCobweb(String key,
                      String categoryName,
                      int usage,
                      Item item,
                      ItemType itemType,
                      int cooldown,
                      String permissionBypass,
                      SoundConfig soundConfig,
                      List<String> notifyYourself,
                      List<String> notifyOpponents,
                      TitleMessage titleYourself,
                      TitleMessage titleOpponents,
                      int radiusX,
                      int radiusY) {

        super(key, categoryName, usage, item, itemType, cooldown,
                permissionBypass, soundConfig, notifyYourself,
                notifyOpponents, titleYourself, titleOpponents);

        this.radiusX = radiusX;
        this.radiusY = radiusY;
    }
    public void cleanCobweb(Location location) {
        for (int x = -radiusX + 1; x < radiusX; x++) {
            for (int y = -radiusY + 1; y < radiusY; y++) {
                for (int z = -radiusX + 1; z < radiusX; z++) {
                    Location tempLoc = location.clone().add(x, y, z);
                    if (WorldGuardUtil.isDeniedFlag(tempLoc, null, Flags.BLOCK_BREAK)) {
                        continue;
                    }
                    Block block = tempLoc.getBlock();
                    if (block.getType() == Material.COBWEB) {
                        block.setType(Material.AIR);
                    }
                }
            }
        }
    }
    @Override
    protected Map<String, String> replacementValues() {
        return Map.of(
                "{radius-x}", String.valueOf(radiusX),
                "{radius-y}", String.valueOf(radiusY)
                    );
    }

}
