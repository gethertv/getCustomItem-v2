package dev.gether.getcustomitem.task;

import dev.gether.getconfig.utils.ItemUtil;
import dev.gether.getconfig.utils.MessageUtil;
import dev.gether.getconfig.utils.PotionConverUtil;
import dev.gether.getcustomitem.item.CustomItem;
import dev.gether.getcustomitem.item.ItemManager;
import dev.gether.getcustomitem.item.ItemType;
import dev.gether.getcustomitem.item.customize.ItemEffect;
import org.bukkit.Bukkit;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class EffectTask extends BukkitRunnable {

    private final ItemManager itemManager;

    public EffectTask(ItemManager itemManager) {
        this.itemManager = itemManager;
    }

    @Override
    public void run() {

        List<CustomItem> allCustomItemByType = itemManager.findAllCustomItemByType(ItemType.ITEM_EFFECT);
        allCustomItemByType.forEach( item -> {
            if(item instanceof ItemEffect itemEffect) {
                Bukkit.getOnlinePlayers().forEach(player -> {
                    itemEffect.getEquipmentSlots().forEach(equipmentSlot -> {
                        ItemStack itemStack = player.getInventory().getItem(equipmentSlot);
                        if(itemStack == null) return;


                        if(ItemUtil.sameItem(itemStack, itemEffect.getItemStack())) {
                            // verify a value to usage of item
                            itemEffect.takeUsage(player, itemStack, EquipmentSlot.HAND);

                            List<PotionEffect> activePotionEffect = PotionConverUtil.getPotionEffectFromConfig(itemEffect.getPotionEffectConfigs());
                            activePotionEffect.forEach(player::addPotionEffect);
                        }
                    });

                });
            }
        });
    }
}
