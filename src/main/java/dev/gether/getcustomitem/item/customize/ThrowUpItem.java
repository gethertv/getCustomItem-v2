package dev.gether.getcustomitem.item.customize;

import com.fasterxml.jackson.annotation.JsonTypeName;
import dev.gether.getconfig.annotation.Comment;
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
@JsonTypeName("throw_up")
public class ThrowUpItem extends CustomItem {

    private boolean includingYou;
    private double pushYourself;

    private boolean otherPlayers;
    private double pushOpponents;

    private int radius;

    public ThrowUpItem(String key,
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
                       boolean includingYou,
                       boolean otherPlayers,
                       double pushYourself,
                       double pushOpponents,
                       int radius
    ) {

        super(key, categoryName, usage, item, itemType, cooldown,
                permissionBypass, soundConfig, notifyYourself,
                notifyOpponents, titleYourself, titleOpponents);

        this.includingYou = includingYou;
        this.otherPlayers = otherPlayers;
        this.pushYourself = pushYourself;
        this.pushOpponents = pushOpponents;
        this.radius = radius;
    }

    @Override
    protected Map<String, String> replacementValues() {
        return Map.of(
                "{radius}", String.valueOf(radius),
                "{push-opponents}", String.valueOf(pushOpponents),
                "{push-yourself}", String.valueOf(pushYourself)
        );
    }
}
