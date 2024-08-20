package dev.gether.getcustomitem;

import dev.gether.getcustomitem.bstats.Metrics;
import dev.gether.getcustomitem.cmd.CustomItemCommand;
import dev.gether.getcustomitem.cmd.arg.CustomItemArg;
import dev.gether.getcustomitem.cmd.handler.NoPermissionHandler;
import dev.gether.getcustomitem.cmd.handler.UsageCmdHandler;
import dev.gether.getcustomitem.file.FileManager;
import dev.gether.getcustomitem.cooldown.CooldownManager;
import dev.gether.getcustomitem.item.CustomItem;
import dev.gether.getcustomitem.item.ItemType;
import dev.gether.getcustomitem.item.manager.BearFurReducedManager;
import dev.gether.getcustomitem.item.manager.FrozenManager;
import dev.gether.getcustomitem.item.ItemManager;
import dev.gether.getcustomitem.item.manager.MagicTotemManager;
import dev.gether.getcustomitem.item.manager.itembag.ItemBagManager;
import dev.gether.getcustomitem.item.manager.itembag.ItemBagService;
import dev.gether.getcustomitem.listener.*;
import dev.gether.getcustomitem.listener.global.PlayerQuitListener;
import dev.gether.getcustomitem.listener.global.PrepareAnvilListener;
import dev.gether.getcustomitem.storage.MySQL;
import dev.gether.getcustomitem.task.EffectTask;
import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.stream.Stream;

public final class GetCustomItem extends JavaPlugin {

    @Getter
    private static GetCustomItem instance;
    private LiteCommands<CommandSender> liteCommands;

    @Getter
    private FileManager fileManager;

    @Getter
    private ItemManager itemManager;
    private MySQL mySQL;
    private ItemBagManager itemBagManager;

    @Override
    public void onEnable() {
        instance = this;
        // config
        fileManager = new FileManager();
        fileManager.loadItems();

        // managers
        itemManager = new ItemManager(fileManager);

        List<CustomItem> allCustomItemByType = itemManager.findAllCustomItemByType(ItemType.ITEMS_BAG);
        if(!allCustomItemByType.isEmpty()) {
            mySQL = new MySQL(this, fileManager);
            if (!mySQL.isConnected()) {
                getLogger().severe("Cannot connect to the database!");
                getServer().getPluginManager().disablePlugin(this);
                return;
            }

            // services
            ItemBagService itemBagService = new ItemBagService(mySQL, fileManager);
            itemBagManager = new ItemBagManager(this, fileManager, itemBagService, itemManager);

        }
        itemManager.initItems();

        CooldownManager cooldownManager = new CooldownManager(fileManager);
        FrozenManager frozenManager = new FrozenManager();
        BearFurReducedManager bearFurReducedManager = new BearFurReducedManager();
        MagicTotemManager magicTotemManager = new MagicTotemManager();

        // listeners
        Stream.of(
                new CobwebGrenadeListener(itemManager, cooldownManager, fileManager),
                new FishRodListener(itemManager, cooldownManager, fileManager),
                new CrossbowListener(this, itemManager, cooldownManager, fileManager),
                new EffectRadiusListener(itemManager, cooldownManager, fileManager),
                new FrozenSwordListener(itemManager, cooldownManager, fileManager, frozenManager),
                new AntyCobwebListener(itemManager, cooldownManager, fileManager),
                new MagicTotemListener(itemManager, cooldownManager, fileManager, magicTotemManager),
                new BearFurListener(itemManager, cooldownManager, bearFurReducedManager, fileManager),
                new HitEffectListener(itemManager, cooldownManager, fileManager),
                new PlayerQuitListener(bearFurReducedManager, cooldownManager, frozenManager),
                new SnowballTeleport(itemManager, cooldownManager, fileManager),
                new EggThrowItListener(itemManager, cooldownManager, fileManager),
                new InstaHealListener(itemManager, cooldownManager, fileManager),
                new LightningItemListener(itemManager, cooldownManager, fileManager),
                new PushItemListener(itemManager, cooldownManager, fileManager), //
                new ShieldItemListener(itemManager, cooldownManager, fileManager),
                new ShuffleInvListener(itemManager, cooldownManager, fileManager),
                new StopFlyingListener(itemManager, cooldownManager, fileManager, this),
                new ThrowingEnderPearlsListener(itemManager, cooldownManager, fileManager),
                new ThrowUpListener(itemManager, cooldownManager, fileManager),
                new CubidBowListener(this, itemManager, cooldownManager, fileManager),
                new ItemBagListener(itemManager, cooldownManager, fileManager, itemBagManager),
                new ItemTierListener(itemManager, cooldownManager, fileManager),
                new ExplosionBallListener(itemManager, cooldownManager, fileManager),
                new DropToInventoryItemListener(itemManager, cooldownManager, fileManager),
                new ReflectionEffectListener(itemManager, cooldownManager, fileManager),
                new PrepareAnvilListener(itemManager)
        ).forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));

        // register command
        registerCommand(itemManager);

        new EffectTask(itemManager).runTaskTimer(this, 20L, 20L);

        // register bstats
        new Metrics(this, 21420);
    }

    private void registerCommand(ItemManager itemManager) {
        this.liteCommands = LiteBukkitFactory.builder("getCustomItem", this)
                .commands(
                        new CustomItemCommand(this)
                )
                .invalidUsage(new UsageCmdHandler())
                .missingPermission(new NoPermissionHandler())
                .argument(CustomItem.class, new CustomItemArg(itemManager))
                .build();
    }

    @Override
    public void onDisable() {

        Bukkit.getScheduler().cancelTasks(this);

        // unregister cmd
        if(this.liteCommands != null) {
            this.liteCommands.unregister();
        }

        if(mySQL != null) {
            itemBagManager.saveAllBackpacks();
            mySQL.disconnect();
        }

        HandlerList.unregisterAll(this);

    }


}
