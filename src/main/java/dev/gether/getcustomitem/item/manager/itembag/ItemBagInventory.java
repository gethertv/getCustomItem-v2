package dev.gether.getcustomitem.item.manager.itembag;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.Inventory;

@Getter
@Setter
public class ItemBagInventory {
    private Inventory inventory;
    private String key;

    public ItemBagInventory(Inventory inventory, String key) {
        this.inventory = inventory;
        this.key = key;
    }
}
