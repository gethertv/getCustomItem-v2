package dev.gether.getcustomitem.listener;

import dev.gether.getconfig.utils.MessageUtil;
import dev.gether.getcustomitem.cooldown.CooldownManager;
import dev.gether.getcustomitem.file.FileManager;
import dev.gether.getcustomitem.item.CustomItem;
import dev.gether.getcustomitem.item.ItemManager;
import dev.gether.getcustomitem.item.ItemType;
import dev.gether.getcustomitem.item.customize.BearFurItem;
import dev.gether.getcustomitem.item.manager.BearFurReducedManager;
import dev.gether.getcustomitem.item.model.UseItemData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class BearFurListener implements Listener {

    private final ItemManager itemManager;
    private final CooldownManager cooldownManager;
    private final BearFurReducedManager bearFurReducedManager;
    private final FileManager fileManager;

    public BearFurListener(ItemManager itemManager,
                           CooldownManager cooldownManager,
                           BearFurReducedManager bearFurReducedManager,
                           FileManager fileManager) {
        this.itemManager = itemManager;
        this.cooldownManager = cooldownManager;
        this.bearFurReducedManager = bearFurReducedManager;
        this.fileManager = fileManager;
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

        Optional<CustomItem> customItemByType = itemManager.findCustomItemByType(ItemType.BEAR_FUR, itemStack);
        if (customItemByType.isEmpty() || !(customItemByType.get() instanceof BearFurItem bearFurItem))
            return;

        event.setCancelled(true);

        // check the item is enabled / if not then cancel
        if(!bearFurItem.isEnabled())
            return;


        Action action = event.getAction();
        if(action != Action.RIGHT_CLICK_BLOCK && action != Action.RIGHT_CLICK_AIR) {
            return;
        }

        double cooldownSeconds = cooldownManager.getCooldownSecond(player, bearFurItem);
        if(cooldownSeconds <= 0 || player.hasPermission(bearFurItem.getPermissionBypass())) {
            // set cooldown
            cooldownManager.setCooldown(player, bearFurItem);

            // play sound
            bearFurItem.playSound(player.getLocation()); // play sound

            // set the effect of reduced damage
            bearFurReducedManager.reducedDamage(player, bearFurItem);

            // verify a value to usage of item
            bearFurItem.takeUsage(player, itemStack, event.getHand());

            // alert yourself
            bearFurItem.notifyYourself(player);

        } else {
            MessageUtil.sendMessage(player, fileManager.getLangConfig().getHasCooldown().replace("{time}", String.valueOf(cooldownSeconds)));
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        if(event.isCancelled()) return;

        // check the entity is the player, if not then return/ignore
        if(!(event.getEntity() instanceof Player victim)) {
            return;
        }

        // find the user are using the item
        Optional<UseItemData> useItemDataByUUID = bearFurReducedManager.findUseItemDataByUUID(victim.getUniqueId());
        if(useItemDataByUUID.isEmpty())
            return;

        UseItemData useItemData = useItemDataByUUID.get();
        BearFurItem bearFurItem = (BearFurItem) useItemData.getCustomItem();

        // if he used the item, check if the time has passed
        double reducedTime = bearFurReducedManager.getReducedTime(victim);
        if(reducedTime > 0) {
            double damage = event.getDamage();
            double latestDamage = damage * (bearFurItem.getReducedDamage() / 100);
            event.setDamage(latestDamage);
        }

    }



}