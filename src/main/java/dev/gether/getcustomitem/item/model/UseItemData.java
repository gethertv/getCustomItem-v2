package dev.gether.getcustomitem.item.model;

import dev.gether.getcustomitem.item.CustomItem;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UseItemData {
    private CustomItem customItem;
    private Long timeMS;

    public UseItemData(CustomItem customItem, Long timeMS) {
        this.customItem = customItem;
        this.timeMS = timeMS;
    }
}
