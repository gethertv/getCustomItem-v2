package dev.gether.getcustomitem.item.customize;

import com.fasterxml.jackson.annotation.JsonTypeName;
import dev.gether.getconfig.domain.Item;
import dev.gether.getconfig.domain.config.TitleMessage;
import dev.gether.getconfig.domain.config.sound.SoundConfig;
import dev.gether.getcustomitem.GetCustomItem;
import dev.gether.getcustomitem.item.CustomItem;
import dev.gether.getcustomitem.item.ItemType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Map;

@JsonTypeName("poke_ball")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PokeballItem extends CustomItem {
    private double speedPokeball;
    private double maxRange;
    private double chance;
    private String permissionByPass = "pokeball.bypass";


    public PokeballItem(String key, String categoryName, int usage, Item item, ItemType itemType, int cooldown, String permissionBypass, SoundConfig soundConfig, List<String> notifyYourself, List<String> notifyOpponents, TitleMessage titleYourself, TitleMessage titleOpponents, double speedPokeball, double chance, int maxRange) {
        super(key, categoryName, usage, item, itemType, cooldown, permissionBypass, soundConfig, notifyYourself, notifyOpponents, titleYourself, titleOpponents);
        this.speedPokeball = speedPokeball;
        this.chance = chance;
        this.maxRange = maxRange;
    }


    protected Map<String, String> replacementValues() {
        return Map.of("{chance}",
                String.valueOf(this.chance));
    }

    public void throwEntity(Player player) {
        Vector direction = player.getEyeLocation().getDirection().multiply(this.speedPokeball);
        Arrow arrow = player.getWorld().spawn(player.getEyeLocation().add(direction.getX(), direction.getY(), direction.getZ()), Arrow.class);
        arrow.setShooter(player);
        arrow.setMetadata(getKey(), new FixedMetadataValue(GetCustomItem.getInstance(), "true"));
        arrow.setVelocity(direction);
        arrow.setInvulnerable(true);
        maxRangeTask(arrow, this.maxRange);
    }

}
