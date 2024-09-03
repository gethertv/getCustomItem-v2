package dev.gether.getcustomitem.cmd;

import dev.gether.getconfig.utils.MessageUtil;
import dev.gether.getcustomitem.GetCustomItem;
import dev.gether.getcustomitem.item.CustomItem;
import dev.gether.getcustomitem.item.ItemType;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Command(name = "getcustomitem", aliases = {"getitem", "citem"})
@Permission("getcustomitem.admin")
public class CustomItemCommand {

    private final GetCustomItem plugin;


    public CustomItemCommand(GetCustomItem plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param commandSender SENDER
     * @param target player name who get the item
     * @param customItem name of item (ID from CONFIG)
     * @param amount amount of items to give
     *
     * give target(player) a custom item with specified amount
     */
    @Execute(name = "give")
    public void giveItem(@Context CommandSender commandSender, @Arg("nickname") Player target, @Arg("name_item") CustomItem customItem, @Arg("amount") int amount) {

        ItemStack itemStack = customItem.getItemStack().clone(); // clone item to change amount
        itemStack.setAmount(amount); // set new amount

        target.getInventory().addItem(itemStack); // add item to the player (target)
        MessageUtil.sendMessage(commandSender, "&aSuccessful give the item to player!");

    }

    @Execute(name = "setspawn")
    public void spawnLocation(@Context Player player) {
        plugin.getFileManager().getConfig().setSpawnLocation(player.getLocation());
        plugin.getFileManager().getConfig().save();

        MessageUtil.sendMessage(player, "&aSuccessful spawn location set!");
    }

    @Execute(name = "debug")
    public void debugItem(@Context CommandSender commandSender) {
        // check commandSender is player
        if(commandSender instanceof Player player) {
            ItemStack mainHand = player.getInventory().getItemInMainHand();
            MessageUtil.sendMessage(player, mainHand.toString());
        } else {
            MessageUtil.sendMessage(commandSender, "&cYou cannot use this command by console");
        }
    }

    @Execute(name = "reload")
    public void reloadConfig(@Context CommandSender commandSender) {
        //  reload config
        MessageUtil.broadcast("1size: "+plugin.getItemManager().findAllCustomItemByType(ItemType.ITEM_TIER).size());
        plugin.getFileManager().reload();
        MessageUtil.broadcast("2size: "+plugin.getItemManager().findAllCustomItemByType(ItemType.ITEM_TIER).size());
        MessageUtil.broadcast("3size: "+plugin.getItemManager().findAllCustomItemByType(ItemType.ITEM_TIER).size());
        MessageUtil.sendMessage(commandSender, "#40ff76Successfully reloaded config!");

    }




}
