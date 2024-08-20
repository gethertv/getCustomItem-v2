package dev.gether.getcustomitem.item.customize;

import com.fasterxml.jackson.annotation.JsonTypeName;
import dev.gether.getconfig.domain.Item;
import dev.gether.getconfig.domain.config.TitleMessage;
import dev.gether.getconfig.domain.config.sound.SoundConfig;
import dev.gether.getcustomitem.item.CustomItem;
import dev.gether.getcustomitem.item.ItemType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@JsonTypeName("drop_to_inv")
public class DropToInventoryItem extends CustomItem {

    public DropToInventoryItem(String key,
                               String categoryName,
                               int usage,
                               Item item,
                               ItemType itemType,
                               int cooldown,
                               String permissionBypass,
                               SoundConfig soundConfig,
                               List<String> notifyYourself,
                               TitleMessage titleYourself
    ) {

        super(key, categoryName, usage, item, itemType, cooldown,
                permissionBypass, soundConfig, notifyYourself,
                null, titleYourself, null);

    }

    @Override
    protected Map<String, String> replacementValues() {
        return Map.of();
    }


}
