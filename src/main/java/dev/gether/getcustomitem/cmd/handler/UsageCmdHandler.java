package dev.gether.getcustomitem.cmd.handler;

import dev.gether.getconfig.utils.MessageUtil;
import dev.rollczi.litecommands.handler.result.ResultHandlerChain;
import dev.rollczi.litecommands.invalidusage.InvalidUsage;
import dev.rollczi.litecommands.invalidusage.InvalidUsageHandler;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.schematic.Schematic;
import org.bukkit.command.CommandSender;

public class UsageCmdHandler implements InvalidUsageHandler<CommandSender> {

    @Override
    public void handle(Invocation<CommandSender> invocation, InvalidUsage<CommandSender> result, ResultHandlerChain<CommandSender> chain) {
        CommandSender sender = invocation.sender();
        Schematic schematic = result.getSchematic();

        if (schematic.isOnlyFirst()) {
            MessageUtil.sendMessage(sender, "&cInvalid usage of command! &7(" + schematic.first() + ")");
            return;
        }

        MessageUtil.sendMessage(sender, "&cInvalid usage of command!");
        for (String scheme : schematic.all()) {
            MessageUtil.sendMessage(sender, "&8 - &7" + scheme);
        }
    }
}
