package dev.gether.getcustomitem.listener;

import dev.gether.getconfig.utils.MessageUtil;
import dev.gether.getcustomitem.cooldown.CooldownManager;
import dev.gether.getcustomitem.file.FileManager;
import dev.gether.getcustomitem.item.CustomItem;
import dev.gether.getcustomitem.item.ItemManager;
import dev.gether.getcustomitem.item.ItemType;
import dev.gether.getcustomitem.item.customize.ItemsBag;
import dev.gether.getcustomitem.item.manager.itembag.ItemBagInventoryHolder;
import dev.gether.getcustomitem.item.manager.itembag.ItemBagManager;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ItemBagListener implements Listener {

    private final ItemManager itemManager;
    private final CooldownManager cooldownManager;
    private final FileManager fileManager;
    private final ItemBagManager itemBagManager;
    public ItemBagListener(ItemManager itemManager,
                           CooldownManager cooldownManager,
                           FileManager fileManager,
                           ItemBagManager itemBagManager) {
        this.itemManager = itemManager;
        this.cooldownManager = cooldownManager;
        this.fileManager = fileManager;
        this.itemBagManager = itemBagManager;
    }

    /**
     * cancel place/use custom item
     * @param event
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {

        Player player = event.getPlayer();
        ItemStack itemStack = event.getItem(); // using item

        if(itemStack == null)
            return;

        List<CustomItem> customItemByType = itemManager.findAllCustomItemByType(ItemType.ITEMS_BAG);
        if (customItemByType.isEmpty())
            return;


        for (CustomItem customItem : customItemByType) {
            if (!(customItem instanceof ItemsBag itemsBag))
                continue;

            // check the item is enabled / if not then cancel
            if(!itemsBag.isEnabled())
                continue;

            if(!itemsBag.isBackpack(itemStack))
                continue;

            event.setCancelled(true);

            Action action = event.getAction();
            if(action != Action.RIGHT_CLICK_BLOCK && action != Action.RIGHT_CLICK_AIR) {
                return;
            }

            double cooldownSeconds = cooldownManager.getCooldownSecond(player, itemsBag);
            if(cooldownSeconds <= 0 || player.hasPermission(itemsBag.getPermissionBypass())) {
                // set cooldown
                cooldownManager.setCooldown(player, itemsBag);

                // play sound
                itemsBag.playSound(player.getLocation()); // play sound
                itemBagManager.openBackpack(player, itemStack, itemsBag);


            } else {
                MessageUtil.sendMessage(player, fileManager.getLangConfig().getHasCooldown().replace("{time}", String.valueOf(cooldownSeconds)));
            }
            return;
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        InventoryView openInventory = player.getOpenInventory();
        if (openInventory.getTopInventory().getHolder() instanceof ItemBagInventoryHolder itemBagInventoryHolder) {
            Item itemDrop = event.getItemDrop();
            ItemStack itemStack = itemDrop.getItemStack();
            List<CustomItem> customItemByType = itemManager.findAllCustomItemByType(ItemType.ITEMS_BAG);
            if (customItemByType.isEmpty())
                return;


            for (CustomItem customItem : customItemByType) {
                if (!(customItem instanceof ItemsBag itemsBag))
                    continue;

                if(!itemsBag.isBackpack(itemStack))
                    continue;

                event.setCancelled(true);
                return;
            }

        }
    }


    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        Inventory inventory = event.getInventory();
        if (inventory.getHolder() instanceof ItemBagInventoryHolder itemBagInventoryHolder) {
            for (Integer rawSlot : event.getRawSlots()) {
                if(rawSlot >= 0 && rawSlot < inventory.getSize()) {
                    event.setCancelled(true);
                }
            }
        }
    }


    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null) return;

        if (inventory.getHolder() instanceof ItemBagInventoryHolder itemBagInventoryHolder) {
            Player player = (Player) event.getWhoClicked();
            if(event.getClick().isShiftClick()) {
                event.setCancelled(true);
            }

            int clickedSlot = event.getRawSlot();
            ItemStack clickedItem = event.getCurrentItem();

            if(clickedSlot >= 0 && clickedSlot < clickedInventory.getSize()) {
                event.setCancelled(true);
                itemBagInventoryHolder.action(player, clickedSlot, clickedItem);
            }
            itemBagManager.updateTakeAllButton(clickedInventory, itemBagInventoryHolder.getItemsBag());
        }
    }



    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        if (killer == null) {
            return;
        }

        List<ItemStack> backpacks = findBackpacksInInventory(killer.getInventory());
        if (backpacks.isEmpty()) {
            return;
        }

        List<ItemStack> itemsToAdd = new ArrayList<>(event.getDrops());
        List<ItemStack> itemsNotAdded = new ArrayList<>(itemsToAdd);

        for (ItemStack backpackItem : backpacks) {
            Optional<CustomItem> customItemByType = itemManager.findCustomItemByType(ItemType.ITEMS_BAG, backpackItem);
            if (customItemByType.isEmpty() || !(customItemByType.get() instanceof ItemsBag itemsBag))
                continue;

            UUID backpackUUID = itemsBag.getBackpackUUID(backpackItem);
            if (backpackUUID == null) continue;

            itemsNotAdded = itemBagManager.addItemsToBackpack(backpackUUID, itemsNotAdded);

            if (itemsNotAdded.isEmpty()) {
                break;
            }
        }

        event.getDrops().clear();
        event.getDrops().addAll(itemsNotAdded);
    }

    private List<ItemStack> findBackpacksInInventory(PlayerInventory inventory) {
        List<ItemStack> backpacks = new ArrayList<>();
        List<CustomItem> customItemByType = itemManager.findAllCustomItemByType(ItemType.ITEMS_BAG);
        if(customItemByType.isEmpty())
            return new ArrayList<>();


        for (CustomItem customItem : customItemByType) {
            if (!(customItem instanceof ItemsBag itemsBag))
                continue;

            for (ItemStack item : inventory.getContents()) {
                if(!itemsBag.isBackpack(item))
                    continue;

                backpacks.add(item);
            }
        }
        return backpacks;
    }
}
