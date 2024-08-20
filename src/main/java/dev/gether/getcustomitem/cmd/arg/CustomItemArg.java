package dev.gether.getcustomitem.cmd.arg;

import dev.gether.getcustomitem.item.CustomItem;
import dev.gether.getcustomitem.item.ItemManager;
import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.argument.resolver.ArgumentResolver;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.suggestion.SuggestionContext;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import org.bukkit.command.CommandSender;

import java.util.Optional;

public class CustomItemArg extends ArgumentResolver<CommandSender, CustomItem> {

    private final ItemManager itemManager;

    public CustomItemArg(ItemManager itemManager) {
        this.itemManager = itemManager;
    }

    @Override
    protected ParseResult<CustomItem> parse(Invocation<CommandSender> invocation, Argument<CustomItem> context, String argument) {
        Optional<CustomItem> customItemByKey = this.itemManager.findCustomItemByKey(argument);

        return customItemByKey.map(ParseResult::success).orElseGet(() -> ParseResult.failure("Item not found!"));

    }
    @Override
    public SuggestionResult suggest(Invocation<CommandSender> invocation, Argument<CustomItem> argument, SuggestionContext context) {
        return this.itemManager.getAllItemKey();
    }
}
