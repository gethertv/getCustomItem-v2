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

import java.util.List;
import java.util.Map;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeName("magic_totem")
public class MagicTotemItem extends CustomItem {
    private double chanceLostItem;

    public MagicTotemItem(String key,
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
                          double chanceLostItem) {

        super(key, categoryName, usage, item, itemType, cooldown,
                permissionBypass, soundConfig, notifyYourself,
                notifyOpponents, titleYourself, titleOpponents);

        this.chanceLostItem = chanceLostItem;
    }

    @Override
    protected Map<String, String> replacementValues() {
        return Map.of(
                "{chance}", String.valueOf(chanceLostItem)
        );
    }

}
