package dev.gether.getcustomitem.item.customize;

import com.fasterxml.jackson.annotation.JsonTypeName;
import dev.gether.getconfig.domain.Item;
import dev.gether.getconfig.domain.config.TitleMessage;
import dev.gether.getconfig.domain.config.sound.SoundConfig;
import dev.gether.getcustomitem.item.CustomItem;
import dev.gether.getcustomitem.item.ItemType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Material;

import java.util.List;
import java.util.Map;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeName("stop_flying")
public class StopFlyingItem extends CustomItem {
    private double multiply;
    private double divideHeight;
    private double divideGliding;
    private int stopFlyingTime;
    private List<Material> cannotUseMaterial;


    public StopFlyingItem(String key,
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
                          double multiply,
                          double divideHeight,
                          double divideGliding,
                          int stopFlyingTime,
                          List<Material> cannotUseMaterial) {

        super(key, categoryName, usage, item, itemType, cooldown,
                permissionBypass, soundConfig, notifyYourself,
                notifyOpponents, titleYourself, titleOpponents);

        this.multiply = multiply;
        this.divideHeight = divideHeight;
        this.divideGliding = divideGliding;
        this.stopFlyingTime = stopFlyingTime;
        this.cannotUseMaterial = cannotUseMaterial;
    }

    @Override
    protected Map<String, String> replacementValues() {
        return Map.of();
    }




}
