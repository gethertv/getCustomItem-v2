package dev.gether.getcustomitem.file;

import dev.gether.getconfig.domain.Item;
import dev.gether.getconfig.domain.config.ItemInventory;
import dev.gether.getconfig.domain.config.TitleMessage;
import dev.gether.getconfig.domain.config.particles.DustOptions;
import dev.gether.getconfig.domain.config.particles.ParticleConfig;
import dev.gether.getconfig.domain.config.potion.PotionEffectConfig;
import dev.gether.getconfig.domain.config.sound.SoundConfig;
import dev.gether.getcustomitem.item.CustomItem;
import dev.gether.getcustomitem.item.ItemType;
import dev.gether.getcustomitem.item.customize.*;
import dev.gether.getcustomitem.item.customize.itemtier.ActionEvent;
import dev.gether.getcustomitem.item.customize.itemtier.ItemTier;
import dev.gether.getcustomitem.item.customize.itemtier.TierData;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;

import java.util.*;


public class DefaultItem {

    @Getter
    private Set<CustomItem> customItems = new HashSet<>(
            Set.of(
                    new HookItem(
                            "hook",
                            "hook_category",
                            -1,
                            Item.builder()
                                    .amount(1)
                                    .material(Material.FISHING_ROD)
                                    .displayname("#f2ff69Magic fishing rod!")
                                    .lore(new ArrayList<>(
                                            List.of(
                                                    "&7",
                                                    "#beff69× Use right click!",
                                                    "&7",
                                                    "&7• Usage: #beff69{usage}",
                                                    "&7"
                                            )
                                    ))
                                    .unbreakable(true)
                                    .glow(true)
                                    .build(),
                            ItemType.HOOK,
                            5,
                            "getcustomitem.hook.bypass",
                            SoundConfig.builder()
                                    .enable(true)
                                    .sound(Sound.UI_BUTTON_CLICK)
                                    .build(),
                            new ArrayList<>(
                                    List.of("&7",
                                            "#78ff69 × Example YOURSELF!",
                                            "&7")
                            ),
                            new ArrayList<>(
                                    List.of("&7",
                                            "#78ff69 × Example OPPONENTS!",
                                            "&7")
                            ),
                            new TitleMessage(false, "&aTitle Yourself", "&7Subtitle", 10, 20, 10),
                            new TitleMessage(false, "&aTitle opponents", "&7Subtitle", 10, 20, 10),
                            4,
                            1.7,
                            2.0
                    ),
                    new CrossBowItem(
                            "crossbow",
                            "crossbow_category",
                            -1,
                            Item.builder()
                                    .amount(1)
                                    .material(Material.CROSSBOW)
                                    .displayname("#40ffe9Teleporting crossbow")
                                    .lore(new ArrayList<>(
                                            List.of(
                                                    "&7",
                                                    "#85fff1× Hit a player and move him to you",
                                                    "#85fff1× Chance: #c2fff8{chance}%",
                                                    "&7",
                                                    "&7&7• Usage: #85fff1{usage}",
                                                    "&7"
                                            )
                                    ))
                                    .unbreakable(true)
                                    .glow(true)
                                    .build(),
                            ItemType.CROSSBOW,
                            10,
                            "getcustomitem.crossbow.bypass",
                            SoundConfig.builder()
                                    .enable(true)
                                    .sound(Sound.ENTITY_TNT_PRIMED)
                                    .build(),
                            new ArrayList<>(
                                    List.of("&7",
                                            "#78ff69 × Example YOURSELF!",
                                            "&7")
                            ),
                            new ArrayList<>(
                                    List.of("&7",
                                            "#78ff69 × Example OPPONENTS!",
                                            "&7")
                            ),
                            new TitleMessage(false, "&aTitle Yourself", "&7Subtitle", 10, 20, 10),
                            new TitleMessage(false, "&aTitle opponents", "&7Subtitle", 10, 20, 10),
                            ParticleConfig.builder()
                                    .enable(true)
                                    .particle(Particle.HEART)
                                    .build(),
                            "getcustomitem.crossbow.ignore",
                            50,
                            25
                    ),
                    new CobwebGrenade(
                            "cobweb_grenade",
                            "cobweb_category",
                            5,
                            Item.builder()
                                    .amount(1)
                                    .material(Material.SPLASH_POTION)
                                    .displayname("#ff004cCobweb grenade")
                                    .lore(new ArrayList<>(
                                            List.of(
                                                    "&7",
                                                    "#ff175c× Throw the grande to create",
                                                    "#ff175c  a trap with cobweb &7(&f{radius-x}&7x&f{radius-y}&8) ",
                                                    "&7",
                                                    "&7• Usage: #ff004c{usage}",
                                                    "&7"
                                            )
                                    ))
                                    .unbreakable(true)
                                    .glow(true)
                                    .build(),
                            ItemType.COBWEB_GRENADE,
                            10,
                            "getcustomitem.grenadecobweb.bypass",
                            SoundConfig.builder()
                                    .enable(true)
                                    .sound(Sound.ENTITY_WITHER_SHOOT)
                                    .build(),
                            new ArrayList<>(
                                    List.of("&7",
                                            "#78ff69 × Example YOURSELF!",
                                            "&7")
                            ),
                            new ArrayList<>(
                                    List.of("&7",
                                            "#78ff69 × Example OPPONENTS!",
                                            "&7")
                            ),
                            new TitleMessage(false, "&aTitle Yourself", "&7Subtitle", 10, 20, 10),
                            new TitleMessage(false, "&aTitle opponents", "&7Subtitle", 10, 20, 10),
                            ParticleConfig.builder()
                                    .enable(true)
                                    .dustOptions(new DustOptions(210, 255, 97, 5))
                                    .particle(Particle.REDSTONE)
                                    .build(),
                            2,
                            2,
                            0.95,
                            3
                    ),
                    new EffectRadiusItem(
                            "stick_levitation",
                            "levitation_category",
                            5,
                            Item.builder()
                                    .amount(1)
                                    .material(Material.BLAZE_ROD)
                                    .displayname("#ff9436Stick of levitation")
                                    .lore(new ArrayList<>(
                                            List.of(
                                                    "&7",
                                                    "#ffba61× Use this item to give",
                                                    "#ffba61  the levitation effect on X seconds",
                                                    "#ffba61  to near players &7(&f{radius}&7x&f{radius}&7)",
                                                    "&7",
                                                    "&7• Usage: #ffba61{usage}",
                                                    "&7"
                                            )
                                    ))
                                    .unbreakable(true)
                                    .glow(true)
                                    .build(),
                            ItemType.EFFECT_RADIUS,
                            10,
                            "getcustomitem.sticklevitation.bypass",
                            SoundConfig.builder()
                                    .enable(true)
                                    .sound(Sound.ENTITY_VILLAGER_TRADE)
                                    .build(),
                            new ArrayList<>(
                                    List.of("&7",
                                            "#78ff69 × Example YOURSELF!",
                                            "&7")
                            ),
                            new ArrayList<>(
                                    List.of("&7",
                                            "#78ff69 × Example OPPONENTS!",
                                            "&7")
                            ),
                            new TitleMessage(false, "&aTitle Yourself", "&7Subtitle", 10, 20, 10),
                            new TitleMessage(false, "&aTitle opponents", "&7Subtitle", 10, 20, 10),
                            true,
                            true,
                            5,
                            new ArrayList<>(
                                    List.of(
                                            new PotionEffectConfig(
                                                    "LEVITATION",
                                                    3,
                                                    1
                                            )
                                    )
                            ),
                            new ArrayList<>()
                    ),
                    new EffectRadiusItem(
                            "yeti_eye",
                            "yeti_eye_category",
                            5,
                            Item.builder()
                                    .amount(1)
                                    .material(Material.SPIDER_EYE)
                                    .displayname("#0015ffYeti eye")
                                    .lore(new ArrayList<>(
                                            List.of(
                                                    "&7",
                                                    "#2e3fff× Use this item to give",
                                                    "#2e3fff  the weakness effect on X seconds",
                                                    "#2e3fff  to near players &7(&f{radius}&7x&f{radius}&7)",
                                                    "&7",
                                                    "&7• Usage: #ffba61{usage}",
                                                    "&7"
                                            )
                                    ))
                                    .unbreakable(true)
                                    .glow(true)
                                    .build(),
                            ItemType.EFFECT_RADIUS,
                            10,
                            "getcustomitem.yetieye.bypass",
                            SoundConfig.builder()
                                    .enable(true)
                                    .sound(Sound.ENTITY_POLAR_BEAR_HURT)
                                    .build(),
                            new ArrayList<>(
                                    List.of("&7",
                                            "#78ff69 × Example YOURSELF!",
                                            "&7")
                            ),
                            new ArrayList<>(
                                    List.of("&7",
                                            "#78ff69 × Example OPPONENTS!",
                                            "&7")
                            ),
                            new TitleMessage(false, "&aTitle Yourself", "&7Subtitle", 10, 20, 10),
                            new TitleMessage(false, "&aTitle opponents", "&7Subtitle", 10, 20, 10),
                            true,
                            true,
                            5,
                            new ArrayList<>(
                                    List.of(
                                            new PotionEffectConfig(
                                                    "WEAKNESS",
                                                    3,
                                                    1
                                            )
                                    )
                            ),
                            new ArrayList<>()
                    ),
                    new EffectRadiusItem(
                            "air_filter",
                            "air_filter_category",
                            1,
                            Item.builder()
                                    .amount(1)
                                    .material(Material.FLINT)
                                    .displayname("#608a71Air filter")
                                    .lore(new ArrayList<>(
                                            List.of(
                                                    "&7",
                                                    "#96b0a0× Use this item to clean",
                                                    "#96b0a0  a negative effect from yourself",
                                                    "&7",
                                                    "&7• Usage: #96b0a0{usage}",
                                                    "&7"
                                            )
                                    ))
                                    .unbreakable(true)
                                    .glow(true)
                                    .build(),
                            ItemType.EFFECT_RADIUS,
                            10,
                            "getcustomitem.airfilter.bypass",
                            SoundConfig.builder()
                                    .enable(true)
                                    .sound(Sound.ITEM_TOTEM_USE)
                                    .build(),
                            new ArrayList<>(
                                    List.of("&7",
                                            "#78ff69 × Example YOURSELF!",
                                            "&7")
                            ),
                            new ArrayList<>(
                                    List.of("&7",
                                            "#78ff69 × Example OPPONENTS!",
                                            "&7")
                            ),
                            new TitleMessage(false, "&aTitle Yourself", "&7Subtitle", 10, 20, 10),
                            new TitleMessage(false, "&aTitle opponents", "&7Subtitle", 10, 20, 10),
                            true,
                            false,
                            2,
                            new ArrayList<>(),
                            new ArrayList<>(
                                    List.of(
                                            "WEAKNESS"
                                    )
                            )
                    ),
                    new EffectRadiusItem(
                            "ice_rod",
                            "ice_rod_category",
                            3,
                            Item.builder()
                                    .amount(1)
                                    .material(Material.TRIDENT)
                                    .displayname("#737d9cIce rod")
                                    .lore(new ArrayList<>(
                                            List.of(
                                                    "&7",
                                                    "#9aa1b8× Use this item to remove",
                                                    "#9aa1b8  all positive effects from",
                                                    "#9aa1b8  players within a &f{radius}&7x&f{radius}#9aa1b8 radius",
                                                    "&7",
                                                    "&7• Usage: #9aa1b8{usage}",
                                                    "&7"
                                            )
                                    ))
                                    .unbreakable(true)
                                    .glow(true)
                                    .build(),
                            ItemType.EFFECT_RADIUS,
                            10,
                            "getcustomitem.icerod.bypass",
                            SoundConfig.builder()
                                    .enable(true)
                                    .sound(Sound.BLOCK_SNOW_HIT)
                                    .build(),
                            new ArrayList<>(
                                    List.of("&7",
                                            "#78ff69 × Example YOURSELF!",
                                            "&7")
                            ),
                            new ArrayList<>(
                                    List.of("&7",
                                            "#78ff69 × Example OPPONENTS!",
                                            "&7")
                            ),
                            new TitleMessage(false, "&aTitle Yourself", "&7Subtitle", 10, 20, 10),
                            new TitleMessage(false, "&aTitle opponents", "&7Subtitle", 10, 20, 10),
                            false,
                            true,
                            5,
                            new ArrayList<>(),
                            new ArrayList<>(
                                    List.of(
                                            "SPEED",
                                            "INCREASE_DAMAGE",
                                            "JUMP"
                                    )
                            )
                    ),
                    new FrozenSword(
                            "frozen_sword",
                            "frozen_sword_category",
                            -1,
                            Item.builder()
                                    .amount(1)
                                    .material(Material.OXEYE_DAISY)
                                    .displayname("#3366ffFrozen sword")
                                    .lore(new ArrayList<>(
                                            List.of(
                                                    "&7",
                                                    "#527dff× Hit the player to have",
                                                    "#527dff  a {chance}% chance of freezing",
                                                    "#527dff  them for {seconds} seconds!",
                                                    "&7",
                                                    "&7• Usage: #527dff{usage}",
                                                    "&7"
                                            )
                                    ))
                                    .unbreakable(true)
                                    .glow(true)
                                    .build(),
                            ItemType.FROZEN_SWORD,
                            10,
                            "getcustomitem.frozensword.bypass",
                            SoundConfig.builder()
                                    .enable(true)
                                    .sound(Sound.BLOCK_SNOW_HIT)
                                    .build(),
                            new ArrayList<>(
                                    List.of("&7",
                                            "#78ff69 × Example YOURSELF!",
                                            "&7")
                            ),
                            new ArrayList<>(
                                    List.of("&7",
                                            "#78ff69 × Example OPPONENTS!",
                                            "&7")
                            ),
                            new TitleMessage(false, "&aTitle Yourself", "&7Subtitle", 10, 20, 10),
                            new TitleMessage(false, "&aTitle opponents", "&7Subtitle", 10, 20, 10),
                            2,
                            20
                    ),
                    new AntyCobweb(
                            "anty_cobweb",
                            "anty_cobweb_category",
                            1,
                            Item.builder()
                                    .amount(1)
                                    .material(Material.SOUL_LANTERN)
                                    .displayname("#1aff00Anty-cobweb")
                                    .lore(new ArrayList<>(
                                            List.of(
                                                    "&7",
                                                    "#78ff69× Use this item to",
                                                    "#78ff69  remove all cobweb",
                                                    "#78ff69  in radius &7(&f{radius-x}&7x&f{radius-y}&7)",
                                                    "&7",
                                                    "&7• Usage: #78ff69{usage}",
                                                    "&7"
                                            )
                                    ))
                                    .unbreakable(true)
                                    .glow(true)
                                    .build(),
                            ItemType.ANTY_COBWEB,
                            10,
                            "getcustomitem.antycobweb.bypass",
                            SoundConfig.builder()
                                    .enable(true)
                                    .sound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP)
                                    .build(),
                            new ArrayList<>(
                                    List.of("&7",
                                            "#78ff69 × Example YOURSELF!",
                                            "&7")
                            ),
                            new ArrayList<>(
                                    List.of("&7",
                                            "#78ff69 × Example OPPONENTS!",
                                            "&7")
                            ),
                            new TitleMessage(false, "&aTitle Yourself", "&7Subtitle", 10, 20, 10),
                            new TitleMessage(false, "&aTitle opponents", "&7Subtitle", 10, 20, 10),
                            2,
                            2
                    ),
                    new MagicTotemItem(
                            "magic_totem",
                            "magic_totem_category",
                            1,
                            Item.builder()
                                    .amount(1)
                                    .material(Material.TOTEM_OF_UNDYING)
                                    .displayname("#ff6a00Magic totem")
                                    .lore(new ArrayList<>(
                                            List.of(
                                                    "&7",
                                                    "#ffb03b× Use this item to",
                                                    "#ffb03b  preserve {chance}% of your",
                                                    "#ffb03b  inventory upon dying",
                                                    "&7",
                                                    "&7• Usage: #ffb03b{usage}",
                                                    "&7"
                                            )
                                    ))
                                    .unbreakable(true)
                                    .glow(true)
                                    .build(),
                            ItemType.MAGIC_TOTEM,
                            10,
                            "getcustomitem.magictotem.bypass",
                            SoundConfig.builder()
                                    .enable(true)
                                    .sound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP)
                                    .build(),
                            new ArrayList<>(
                                    List.of("&7",
                                            "#78ff69 × Example YOURSELF!",
                                            "&7")
                            ),
                            new ArrayList<>(
                                    List.of("&7",
                                            "#78ff69 × Example OPPONENTS!",
                                            "&7")
                            ),
                            new TitleMessage(false, "&aTitle Yourself", "&7Subtitle", 10, 20, 10),
                            new TitleMessage(false, "&aTitle opponents", "&7Subtitle", 10, 20, 10),
                            10
                    ),
                    new BearFurItem(
                            "bear_fur",
                            "bear_fur_category",
                            10,
                            Item.builder()
                                    .amount(1)
                                    .material(Material.PHANTOM_MEMBRANE)
                                    .displayname("#85f2ffBear fur")
                                    .lore(new ArrayList<>(
                                            List.of(
                                                    "&7",
                                                    "#c7f9ff× Use this item to reduced",
                                                    "#c7f9ff  your damage by {reduced-damage}%",
                                                    "#c7f9ff  for {seconds} seconds",
                                                    "&7",
                                                    "&7• Usage: #c7f9ff{usage}",
                                                    "&7"
                                            )
                                    ))
                                    .unbreakable(true)
                                    .glow(true)
                                    .build(),
                            ItemType.BEAR_FUR,
                            10,
                            "getcustomitem.bearfur.bypass",
                            SoundConfig.builder()
                                    .enable(true)
                                    .sound(Sound.BLOCK_ANVIL_USE)
                                    .build(),
                            new ArrayList<>(
                                    List.of("&7",
                                            "#78ff69 × Example YOURSELF!",
                                            "&7")
                            ),
                            new ArrayList<>(
                                    List.of("&7",
                                            "#78ff69 × Example OPPONENTS!",
                                            "&7")
                            ),
                            new TitleMessage(false, "&aTitle Yourself", "&7Subtitle", 10, 20, 10),
                            new TitleMessage(false, "&aTitle opponents", "&7Subtitle", 10, 20, 10),
                            50,
                            5
                    ),
                    new HitEffectItem(
                            "wizard_staff",
                            "wizard_staff_category",
                            -1,
                            Item.builder()
                                    .amount(1)
                                    .material(Material.REDSTONE_TORCH)
                                    .displayname("#8c19ffWizard's staff")
                                    .lore(new ArrayList<>(
                                            List.of(
                                                    "&7",
                                                    "#a74fff× Hit the player",
                                                    "#a74fff  and give him custom effect",
                                                    "#a74fff  Chance: &f{chance}%",
                                                    "&7",
                                                    "&7• Usage: #a74fff{usage}",
                                                    "&7"
                                            )
                                    ))
                                    .unbreakable(true)
                                    .glow(true)
                                    .build(),
                            ItemType.HIT_EFFECT,
                            10,
                            "getcustomitem.wizardstaff.bypass",
                            SoundConfig.builder()
                                    .enable(true)
                                    .sound(Sound.BLOCK_ENCHANTMENT_TABLE_USE)
                                    .build(),
                            new ArrayList<>(
                                    List.of("&7",
                                            "#78ff69 × Example YOURSELF!",
                                            "&7")
                            ),
                            new ArrayList<>(
                                    List.of("&7",
                                            "#78ff69 × Example OPPONENTS!",
                                            "&7")
                            ),
                            new TitleMessage(false, "&aTitle Yourself", "&7Subtitle", 10, 20, 10),
                            new TitleMessage(false, "&aTitle opponents", "&7Subtitle", 10, 20, 10),
                            new ArrayList<>(
                                    List.of(
                                            new PotionEffectConfig("SPEED", 5, 1)
                                    )
                            ),
                            50,
                            false,
                            true
                    ),
                    new SnowballTPItem(
                            "snowball_tp",
                            "snowball_tp_category",
                            -1,
                            Item.builder()
                                    .amount(1)
                                    .material(Material.SNOWBALL)
                                    .displayname("#8c19ffSnowball TP")
                                    .lore(new ArrayList<>(
                                            List.of(
                                                    "&7",
                                                    "#a74fff× Hit the player",
                                                    "#a74fff  and swap locations",
                                                    "#a74fff  with them",
                                                    "&7",
                                                    "&7• Usage: #a74fff{usage}",
                                                    "&7"
                                            )
                                    ))
                                    .unbreakable(true)
                                    .glow(true)
                                    .build(),
                            ItemType.SNOWBALL_TP,
                            10,
                            "getcustomitem.snowballtp.bypass",
                            SoundConfig.builder()
                                    .enable(true)
                                    .sound(Sound.BLOCK_ENCHANTMENT_TABLE_USE)
                                    .build(),
                            new ArrayList<>(
                                    List.of("&7",
                                            "#78ff69 × Example YOURSELF!",
                                            "&7")
                            ),
                            new ArrayList<>(
                                    List.of("&7",
                                            "#78ff69 × Example OPPONENTS!",
                                            "&7")
                            ),
                            new TitleMessage(false, "&aTitle Yourself", "&7Subtitle", 10, 20, 10),
                            new TitleMessage(false, "&aTitle opponents", "&7Subtitle", 10, 20, 10),
                            2.5f,
                            25,
                            Item.builder()
                                    .amount(1)
                                    .material(Material.SNOWBALL)
                                    .displayname("&7")
                                    .lore(new ArrayList<>())
                                    .build()
                    ),
                    new InstaHealItem(
                            "insta_heal",
                            "insta_heal_category",
                            -1,
                            Item.builder()
                                    .amount(1)
                                    .material(Material.BLUE_DYE)
                                    .displayname("#ff4040Vampire Blow")
                                    .lore(new ArrayList<>(
                                            List.of(
                                                    "&7",
                                                    "#ff7a7a× Use this item",
                                                    "#ff7a7a  to heal your self",
                                                    "#ff7a7a  chance: {chance}%",
                                                    "&7",
                                                    "&7• Usage: #ff7a7a{usage}",
                                                    "&7"
                                            )
                                    ))
                                    .unbreakable(true)
                                    .glow(true)
                                    .build(),
                            ItemType.INSTA_HEAL,
                            10,
                            "getcustomitem.instaheal.bypass",
                            SoundConfig.builder()
                                    .enable(true)
                                    .sound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP)
                                    .build(),
                            new ArrayList<>(
                                    List.of("&7",
                                            "#78ff69 × Example YOURSELF!",
                                            "&7")
                            ),
                            new TitleMessage(false, "&aTitle Yourself", "&7Subtitle", 10, 20, 10),
                            100f
                    ),
                    new PushItem(
                            "push_item",
                            "push_item_category",
                            -1,
                            Item.builder()
                                    .amount(1)
                                    .material(Material.BLAZE_ROD)
                                    .displayname("#9e9e9ePush stick")
                                    .lore(new ArrayList<>(
                                            List.of(
                                                    "&7",
                                                    "#666666× Use this item",
                                                    "#666666  to push opponents",
                                                    "#666666  chance: {chance}%",
                                                    "#666666  power: {power-push}",
                                                    "&7",
                                                    "&7• Usage: #666666{usage}",
                                                    "&7"
                                            )
                                    ))
                                    .unbreakable(true)
                                    .glow(true)
                                    .build(),
                            ItemType.PUSH_ITEM,
                            10,
                            "getcustomitem.pushitem.bypass",
                            SoundConfig.builder()
                                    .enable(true)
                                    .sound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP)
                                    .build(),
                            new ArrayList<>(
                                    List.of("&7",
                                            "#78ff69 × Example YOURSELF!",
                                            "&7")
                            ),
                            new ArrayList<>(
                                    List.of("&7",
                                            "#78ff69 × Example OPPONENTS!",
                                            "&7")
                            ),
                            new TitleMessage(false, "&aTitle Yourself", "&7Subtitle", 10, 20, 10),
                            new TitleMessage(false, "&aTitle Opponents", "&7Subtitle", 10, 20, 10),
                            100f,
                            1.5
                    ),
                    new ThrowUpItem(
                            "throw_up_upward",
                            "throw_up_category",
                            -1,
                            Item.builder()
                                    .amount(1)
                                    .material(Material.STICK)
                                    .displayname("#9e9e9ePush stick")
                                    .lore(new ArrayList<>(
                                            List.of(
                                                    "&7",
                                                    "#666666× Use this item",
                                                    "#666666  to throw up players",
                                                    "#666666  in radius: {radius}",
                                                    "#666666  power opponents: &f{push-opponents}",
                                                    "#666666  power yourself: &f{push-yourself}",
                                                    "&7",
                                                    "&7• Usage: #666666{usage}",
                                                    "&7"
                                            )
                                    ))
                                    .unbreakable(true)
                                    .glow(true)
                                    .build(),
                            ItemType.THROW_UP,
                            10,
                            "getcustomitem.pushitem.bypass",
                            SoundConfig.builder()
                                    .enable(true)
                                    .sound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP)
                                    .build(),
                            new ArrayList<>(
                                    List.of("&7",
                                            "#78ff69 × Example YOURSELF!",
                                            "&7")
                            ),
                            new ArrayList<>(
                                    List.of("&7",
                                            "#78ff69 × Example OPPONENTS!",
                                            "&7")
                            ),
                            new TitleMessage(false, "&aTitle Yourself", "&7Subtitle", 10, 20, 10),
                            new TitleMessage(false, "&aTitle Opponents", "&7Subtitle", 10, 20, 10),
                            true,
                            true,
                            1.5,
                            1.5,
                            7
                    ),
                    new LightningItem(
                            "lightning_item",
                            "lightning_item_category",
                            -1,
                            Item.builder()
                                    .amount(1)
                                    .material(Material.DIAMOND_AXE)
                                    .displayname("#9e9e9eLightning Axe")
                                    .lore(new ArrayList<>(
                                            List.of(
                                                    "&7",
                                                    "#666666× Hit player",
                                                    "#666666  and shoot lightning",
                                                    "#666666  taking damage X%",
                                                    "&7",
                                                    "&7• Usage: #666666{usage}",
                                                    "&7"
                                            )
                                    ))
                                    .unbreakable(true)
                                    .glow(true)
                                    .build(),
                            ItemType.LIGHTNING_ITEM,
                            10,
                            "getcustomitem.lightningitem.bypass",
                            SoundConfig.builder()
                                    .enable(true)
                                    .sound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP)
                                    .build(),
                            new ArrayList<>(
                                    List.of("&7",
                                            "#78ff69 × Example YOURSELF!",
                                            "&7")
                            ),
                            new ArrayList<>(
                                    List.of("&7",
                                            "#78ff69 × Example OPPONENTS!",
                                            "&7")
                            ),
                            new TitleMessage(false, "&aTitle Yourself", "&7Subtitle", 10, 20, 10),
                            new TitleMessage(false, "&aTitle Opponents", "&7Subtitle", 10, 20, 10),
                            100,
                            0.3
                    ),
                    new ShieldItem(
                            "shield_item",
                            "shield_item_category",
                            -1,
                            Item.builder()
                                    .amount(1)
                                    .material(Material.RED_DYE)
                                    .displayname("#9e9e9eShield")
                                    .lore(new ArrayList<>(
                                            List.of(
                                                    "&7",
                                                    "#666666× Hand this item",
                                                    "#666666  and block hit",
                                                    "#666666  block chance {chance}%",
                                                    "&7",
                                                    "&7• Usage: #666666{usage}",
                                                    "&7"
                                            )
                                    ))
                                    .unbreakable(true)
                                    .glow(true)
                                    .build(),
                            ItemType.SHIELD_ITEM,
                            10,
                            "getcustomitem.lightningitem.bypass",
                            SoundConfig.builder()
                                    .enable(true)
                                    .sound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP)
                                    .build(),
                            new ArrayList<>(
                                    List.of("&7",
                                            "#78ff69 × Example YOURSELF!",
                                            "&7")
                            ),
                            new ArrayList<>(
                                    List.of("&7",
                                            "#78ff69 × Example OPPONENTS!",
                                            "&7")
                            ),
                            new TitleMessage(false, "&aTitle Yourself", "&7Subtitle", 10, 20, 10),
                            new TitleMessage(false, "&aTitle Opponents", "&7Subtitle", 10, 20, 10),
                            30,
                            new HashSet<>(Set.of(
                                    EquipmentSlot.OFF_HAND
                            ))
                    ),
                    new EggThrowItItem(
                            "egg_throw_it",
                            "egg_throw_it_category",
                            -1,
                            Item.builder()
                                    .amount(1)
                                    .material(Material.EGG)
                                    .displayname("#9e9e9eJump egg")
                                    .lore(new ArrayList<>(
                                            List.of(
                                                    "&7",
                                                    "#666666× Throw egg thrue",
                                                    "#666666  the player hit him and",
                                                    "#666666  throw up it",
                                                    "#666666  chance: &f{chance}",
                                                    "#666666  power: &f{power-push}",
                                                    "&7",
                                                    "&7• Usage: #666666{usage}",
                                                    "&7"
                                            )
                                    ))
                                    .unbreakable(true)
                                    .glow(true)
                                    .build(),
                            ItemType.EGG_THROW_UP,
                            10,
                            "getcustomitem.eggthrowup.bypass",
                            SoundConfig.builder()
                                    .enable(true)
                                    .sound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP)
                                    .build(),
                            new ArrayList<>(
                                    List.of("&7",
                                            "#78ff69 × Example YOURSELF!",
                                            "&7")
                            ),
                            new ArrayList<>(
                                    List.of("&7",
                                            "#78ff69 × Example OPPONENTS!",
                                            "&7")
                            ),
                            new TitleMessage(false, "&aTitle Yourself", "&7Subtitle", 10, 20, 10),
                            new TitleMessage(false, "&aTitle Opponents", "&7Subtitle", 10, 20, 10),
                            1.5,
                            1.5,
                            100,
                            25.0,
                            Item.builder()
                                    .amount(1)
                                    .material(Material.EGG)
                                    .displayname("Egg")
                                    .lore(new ArrayList<>())
                                    .unbreakable(true)
                                    .glow(true)
                                    .build()
                    ),
                    new ThrowingEnderPearlsItem(
                            "throwing_ender_pearls",
                            "throwing_ender_pearls_category",
                            -1,
                            Item.builder()
                                    .amount(1)
                                    .material(Material.DIAMOND_SWORD)
                                    .displayname("#9e9e9eDragon sword")
                                    .lore(new ArrayList<>(
                                            List.of(
                                                    "&7",
                                                    "#666666× Right click to throw",
                                                    "#666666× ender pearls",
                                                    "&7",
                                                    "&7• Usage: #666666{usage}",
                                                    "&7"
                                            )
                                    ))
                                    .enchantments(new HashMap<>(Map.of(
                                            Enchantment.FIRE_ASPECT, 2,
                                            Enchantment.DAMAGE_ALL, 6
                                    )))
                                    .unbreakable(true)
                                    .glow(false)
                                    .build(),
                            ItemType.THROWING_ENDER_PEARLS,
                            10,
                            "getcustomitem.throwingenderpearls.bypass",
                            SoundConfig.builder()
                                    .enable(true)
                                    .sound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP)
                                    .build(),
                            new ArrayList<>(
                                    List.of("&7",
                                            "#78ff69 × Example YOURSELF!",
                                            "&7")
                            ),
                            new ArrayList<>(
                                    List.of("&7",
                                            "#78ff69 × Example OPPONENTS!",
                                            "&7")
                            ),
                            new TitleMessage(false, "&aTitle Yourself", "&7Subtitle", 10, 20, 10),
                            new TitleMessage(false, "&aTitle Opponents", "&7Subtitle", 10, 20, 10),
                            1.5f,
                            25,
                            Item.builder()
                                    .amount(1)
                                    .material(Material.ENDER_PEARL)
                                    .displayname("&7")
                                    .lore(new ArrayList<>())
                                    .enchantments(new HashMap<>())
                                    .build()
                    ),
                    new ItemEffect(
                            "lollypop",
                            "lollypop_category",
                            -1,
                            Item.builder()
                                    .amount(1)
                                    .material(Material.LIME_DYE)
                                    .displayname("#9e9e9eLollypop")
                                    .lore(new ArrayList<>(
                                            List.of(
                                                    "&7",
                                                    "#666666× Hand this item",
                                                    "#666666× to get effects",
                                                    "&7",
                                                    "&7• Usage: #666666{usage}",
                                                    "&7"
                                            )
                                    ))
                                    .enchantments(new HashMap<>(Map.of(
                                            Enchantment.FIRE_ASPECT, 2,
                                            Enchantment.DAMAGE_ALL, 6
                                    )))
                                    .unbreakable(true)
                                    .glow(false)
                                    .build(),
                            ItemType.ITEM_EFFECT,
                            10,
                            "getcustomitem.lollypop.bypass",
                            SoundConfig.builder()
                                    .enable(true)
                                    .sound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP)
                                    .build(),
                            new ArrayList<>(
                                    List.of("&7",
                                            "#78ff69 × Example YOURSELF!",
                                            "&7")
                            ),
                            new ArrayList<>(
                                    List.of("&7",
                                            "#78ff69 × Example OPPONENTS!",
                                            "&7")
                            ),
                            new TitleMessage(false, "&aTitle Yourself", "&7Subtitle", 10, 20, 10),
                            new TitleMessage(false, "&aTitle Opponents", "&7Subtitle", 10, 20, 10),
                            new ArrayList<>(List.of(
                                    EquipmentSlot.HAND
                            )),
                            new ArrayList<>(
                                    List.of(
                                            new PotionEffectConfig(
                                                    "SPEED",
                                                    3,
                                                    1
                                            )
                                    )
                            )
                    ),
                    new ItemEffect(
                            "crown",
                            "crown_category",
                            -1,
                            Item.builder()
                                    .amount(1)
                                    .material(Material.PLAYER_HEAD)
                                    .base64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGYzMzNjNzNjODQ4OWE5Y2EzNWQ1NjAzMTMwOTE4Yjg3NjA5ODRlYjlkMzAyOGUzMGU3NDI0N2RmZjg3M2JmZSJ9fX0=")
                                    .displayname("#9e9e9eCrown")
                                    .lore(new ArrayList<>(
                                            List.of(
                                                    "&7",
                                                    "#666666× Wear this item",
                                                    "#666666× to get effects",
                                                    "&7",
                                                    "&7• Usage: #666666{usage}",
                                                    "&7"
                                            )
                                    ))
                                    .unbreakable(true)
                                    .glow(false)
                                    .build(),
                            ItemType.ITEM_EFFECT,
                            10,
                            "getcustomitem.crown.bypass",
                            SoundConfig.builder()
                                    .enable(true)
                                    .sound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP)
                                    .build(),
                            new ArrayList<>(
                                    List.of("&7",
                                            "#78ff69 × Example YOURSELF!",
                                            "&7")
                            ),
                            new ArrayList<>(
                                    List.of("&7",
                                            "#78ff69 × Example OPPONENTS!",
                                            "&7")
                            ),
                            new TitleMessage(false, "&aTitle Yourself", "&7Subtitle", 10, 20, 10),
                            new TitleMessage(false, "&aTitle Opponents", "&7Subtitle", 10, 20, 10),
                            new ArrayList<>(List.of(
                                    EquipmentSlot.HEAD
                            )),
                            new ArrayList<>(
                                    List.of(
                                            new PotionEffectConfig(
                                                    "SPEED",
                                                    3,
                                                    1
                                            )
                                    )
                            )
                    ),
                    new ItemEffect(
                            "cupids_stick",
                            "cupids_stick_category",
                            -1,
                            Item.builder()
                                    .amount(1)
                                    .material(Material.STICK)
                                    .displayname("#9e9e9eCupid's Stick")
                                    .lore(new ArrayList<>(
                                            List.of(
                                                    "&7",
                                                    "#666666× Hand this item",
                                                    "#666666× to get effects",
                                                    "&7",
                                                    "&7• Usage: #666666{usage}",
                                                    "&7"
                                            )
                                    ))
                                    .unbreakable(true)
                                    .glow(true)
                                    .build(),
                            ItemType.ITEM_EFFECT,
                            10,
                            "getcustomitem.crown.bypass",
                            SoundConfig.builder()
                                    .enable(true)
                                    .sound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP)
                                    .build(),
                            new ArrayList<>(
                                    List.of("&7",
                                            "#78ff69 × Example YOURSELF!",
                                            "&7")
                            ),
                            new ArrayList<>(
                                    List.of("&7",
                                            "#78ff69 × Example OPPONENTS!",
                                            "&7")
                            ),
                            new TitleMessage(false, "&aTitle Yourself", "&7Subtitle", 10, 20, 10),
                            new TitleMessage(false, "&aTitle Opponents", "&7Subtitle", 10, 20, 10),
                            new ArrayList<>(List.of(
                                    EquipmentSlot.HAND
                            )),
                            new ArrayList<>(
                                    List.of(
                                            new PotionEffectConfig(
                                                    "SPEED",
                                                    3,
                                                    1
                                            )
                                    )
                            )
                    ),
                    new ItemEffect(
                            "cupids_stick",
                            "cupids_stick_category",
                            -1,
                            Item.builder()
                                    .amount(1)
                                    .material(Material.STICK)
                                    .displayname("#9e9e9eCupid's Stick")
                                    .lore(new ArrayList<>(
                                            List.of(
                                                    "&7",
                                                    "#666666× Hand this item",
                                                    "#666666× to get effects",
                                                    "&7",
                                                    "&7• Usage: #666666{usage}",
                                                    "&7"
                                            )
                                    ))
                                    .unbreakable(true)
                                    .glow(true)
                                    .build(),
                            ItemType.ITEM_EFFECT,
                            10,
                            "getcustomitem.crown.bypass",
                            SoundConfig.builder()
                                    .enable(true)
                                    .sound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP)
                                    .build(),
                            new ArrayList<>(
                                    List.of("&7",
                                            "#78ff69 × Example YOURSELF!",
                                            "&7")
                            ),
                            new ArrayList<>(
                                    List.of("&7",
                                            "#78ff69 × Example OPPONENTS!",
                                            "&7")
                            ),
                            new TitleMessage(false, "&aTitle Yourself", "&7Subtitle", 10, 20, 10),
                            new TitleMessage(false, "&aTitle Opponents", "&7Subtitle", 10, 20, 10),
                            new ArrayList<>(List.of(
                                    EquipmentSlot.HAND
                            )),
                            new ArrayList<>(
                                    List.of(
                                            new PotionEffectConfig(
                                                    "SPEED",
                                                    3,
                                                    1
                                            )
                                    )
                            )
                    ),
                    new ShuffleInventoryItem(
                            "shuffle_inv",
                            "shuffle_inv_category",
                            -1,
                            Item.builder()
                                    .amount(1)
                                    .material(Material.PLAYER_HEAD)
                                    .displayname("#9e9e9eRubik's cube")
                                    .base64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGYzMzNjNzNjODQ4OWE5Y2EzNWQ1NjAzMTMwOTE4Yjg3NjA5ODRlYjlkMzAyOGUzMGU3NDI0N2RmZjg3M2JmZSJ9fX0=")
                                    .lore(new ArrayList<>(
                                            List.of(
                                                    "&7",
                                                    "#666666× Hit player to",
                                                    "#666666× shuffle his inventory",
                                                    "&7",
                                                    "&7• Usage: #666666{usage}",
                                                    "&7"
                                            )
                                    ))
                                    .unbreakable(true)
                                    .glow(true)
                                    .build(),
                            ItemType.SHUFFLE_ITEM,
                            10,
                            "getcustomitem.shuffleinv.bypass",
                            SoundConfig.builder()
                                    .enable(true)
                                    .sound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP)
                                    .build(),
                            new ArrayList<>(
                                    List.of("&7",
                                            "#78ff69 × Example YOURSELF!",
                                            "&7")
                            ),
                            new ArrayList<>(
                                    List.of("&7",
                                            "#78ff69 × Example OPPONENTS!",
                                            "&7")
                            ),
                            new TitleMessage(false, "&aTitle Yourself", "&7Subtitle", 10, 20, 10),
                            new TitleMessage(false, "&aTitle Opponents", "&7Subtitle", 10, 20, 10),
                            100.0
                    ),
                    new CupidBowItem(
                            "cupid_bow",
                            "cupid_bow_category",
                            -1,
                            Item.builder()
                                    .amount(1)
                                    .material(Material.CROSSBOW)
                                    .displayname("#40ffe9Cupid's Bow")
                                    .lore(new ArrayList<>(
                                            List.of(
                                                    "&7",
                                                    "#85fff1× Hit player and",
                                                    "#85fff1× give victim blindness",
                                                    "#85fff1× Chance: #c2fff8{chance}%",
                                                    "&7",
                                                    "&7&7• Usage: #85fff1{usage}",
                                                    "&7"
                                            )
                                    ))
                                    .enchantments(new HashMap<>(Map.of(
                                            Enchantment.ARROW_FIRE, 1,
                                            Enchantment.ARROW_INFINITE, 1,
                                            Enchantment.ARROW_DAMAGE, 6,
                                            Enchantment.DURABILITY, 3
                                    )))
                                    .unbreakable(true)
                                    .glow(false)
                                    .build(),
                            ItemType.CUPIDS_BOW,
                            10,
                            "getcustomitem.cupidbow.bypass",
                            SoundConfig.builder()
                                    .enable(true)
                                    .sound(Sound.ENTITY_TNT_PRIMED)
                                    .build(),
                            new ArrayList<>(
                                    List.of("&7",
                                            "#78ff69 × Example YOURSELF!",
                                            "&7")
                            ),
                            new ArrayList<>(
                                    List.of("&7",
                                            "#78ff69 × Example OPPONENTS!",
                                            "&7")
                            ),
                            new TitleMessage(false, "&aTitle Yourself", "&7Subtitle", 10, 20, 10),
                            new TitleMessage(false, "&aTitle opponents", "&7Subtitle", 10, 20, 10),
                            ParticleConfig.builder()
                                    .enable(true)
                                    .particle(Particle.HEART)
                                    .build(),
                            "getcustomitem.cupidbow.ignore",
                            50,
                            25,
                            new ArrayList<>(List.of(
                                    new PotionEffectConfig("BLINDNESS", 5, 1)
                            ))
                    ),
                    new StopFlyingItem(
                            "stop_flying",
                            "stop_flying_category",
                            -1,
                            Item.builder()
                                    .amount(1)
                                    .material(Material.FISHING_ROD)
                                    .displayname("#f2ff69Anti-Elytra Rod")
                                    .lore(new ArrayList<>(
                                            List.of(
                                                    "&7",
                                                    "#beff69Prevents Elytra use on hit!",
                                                    "&7",
                                                    "&7• Usage: #beff69{usage}",
                                                    "&7"
                                            )
                                    ))
                                    .unbreakable(true)
                                    .glow(true)
                                    .build(),
                            ItemType.STOP_FLYING,
                            5,
                            "getcustomitem.stopflying.bypass",
                            SoundConfig.builder()
                                    .enable(true)
                                    .sound(Sound.UI_BUTTON_CLICK)
                                    .build(),
                            new ArrayList<>(
                                    List.of(
                                            "&7",
                                            "#78ff69 × Example YOURSELF!",
                                            "&7"
                                    )
                            ),
                            new ArrayList<>(
                                    List.of(
                                            "&7",
                                            "#78ff69 × Example OPPONENTS!",
                                            "&7"
                                    )
                            ),
                            new TitleMessage(
                                    false,
                                    "&aTitle Yourself",
                                    "&7Subtitle",
                                    10, 20, 10
                            ),
                            new TitleMessage(
                                    false,
                                    "&aTitle Opponents",
                                    "&7Subtitle",
                                    10, 20, 10
                            ),
                            4,
                            1.7,
                            2.0,
                            15,
                            new ArrayList<>(
                                    List.of(Material.FIREWORK_ROCKET)
                            )
                    ),
                    new ItemsBag(
                            "item_bag",
                            "item_bag_category",
                            -1,
                            Item.builder()
                                    .amount(1)
                                    .material(Material.PLAYER_HEAD)
                                    .base64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTM3YTM1NTIyZjY3YjJhZjkyMzQ1NTkyODQ2YjcwMmI5YWZiOWQ3YzhkYmFkNWVhMTUwNjczYzllNDRkZTMifX19")
                                    .displayname("#f2ff69Loot Bag")
                                    .lore(new ArrayList<>(
                                            List.of(
                                                    "&7",
                                                    "#beff69Stores items from defeated players!",
                                                    "&7",
                                                    "&7• Usage: #beff69{usage}",
                                                    "&7"
                                            )
                                    ))
                                    .unbreakable(true)
                                    .glow(true)
                                    .build(),
                            ItemType.ITEMS_BAG,
                            5,
                            "getcustomitem.itembag.bypass",
                            SoundConfig.builder()
                                    .enable(true)
                                    .sound(Sound.UI_BUTTON_CLICK)
                                    .build(),
                            54,
                            "&0Items Bag",
                            ItemInventory.builder()
                                    .item(Item.builder()
                                            .amount(1)
                                            .material(Material.LIME_DYE)
                                            .displayname("#beff69Withdraw Items")
                                            .lore(new ArrayList<>(
                                                    List.of(
                                                            "",
                                                            "&7Click to withdraw all items",
                                                            ""
                                                    )
                                            ))
                                            .build())
                                    .slot(53)
                                    .build()
                    ),
                    new ExplosionBallItem(
                            "explosion_durability",
                            "explosion_durability_category",
                            -1,
                            Item.builder()
                                    .amount(1)
                                    .material(Material.FIRE_CHARGE)
                                    .displayname("#f2ff69Explosion Ball of Durability")
                                    .lore(new ArrayList<>(
                                            List.of(
                                                    "&7",
                                                    "#beff69Explosion Power: {explosion-power}",
                                                    "&7",
                                                    "&7• Usage: #beff69{usage}",
                                                    "&7"
                                            )
                                    ))
                                    .unbreakable(true)
                                    .glow(true)
                                    .build(),
                            ItemType.EXPLOSION_BALL,
                            5,
                            "getcustomitem.explosionitemdurability.bypass",
                            SoundConfig.builder()
                                    .enable(true)
                                    .sound(Sound.UI_BUTTON_CLICK)
                                    .build(),
                            new ArrayList<>(
                                    List.of(
                                            "&7",
                                            "#78ff69 × Example YOURSELF!",
                                            "&7"
                                    )
                            ),
                            new ArrayList<>(
                                    List.of(
                                            "&7",
                                            "#78ff69 × Example OPPONENTS!",
                                            "&7"
                                    )
                            ),
                            new TitleMessage(
                                    false,
                                    "&aTitle Yourself",
                                    "&7Subtitle",
                                    10, 20, 10
                            ),
                            new TitleMessage(
                                    false,
                                    "&aTitle Opponents",
                                    "&7Subtitle",
                                    10, 20, 10
                            ),
                            1.5f,
                            25,
                            new HashSet<>(
                                    Set.of(Material.BEDROCK)
                            ),
                            5f,
                            false,
                            true,
                            true,
                            0
                    ),
                    new ExplosionBallItem(
                            "explosion_durability",
                            "explosion_durability_category",
                            -1,
                            Item.builder()
                                    .amount(1)
                                    .material(Material.FIRE_CHARGE)
                                    .displayname("#f2ff69Explosion Ball of Durability")
                                    .lore(new ArrayList<>(
                                            List.of(
                                                    "&7",
                                                    "#beff69Explosion Power: {explosion-power}",
                                                    "#beff69Durability Cost: {destroy-durability}",
                                                    "&7",
                                                    "&7• Usage: #beff69{usage}",
                                                    "&7"
                                            )
                                    ))
                                    .unbreakable(true)
                                    .glow(true)
                                    .build(),
                            ItemType.EXPLOSION_BALL,
                            5,
                            "getcustomitem.explosionitemdurability.bypass",
                            SoundConfig.builder()
                                    .enable(true)
                                    .sound(Sound.UI_BUTTON_CLICK)
                                    .build(),
                            new ArrayList<>(
                                    List.of(
                                            "&7",
                                            "#78ff69 × Example YOURSELF!",
                                            "&7"
                                    )
                            ),
                            new ArrayList<>(
                                    List.of(
                                            "&7",
                                            "#78ff69 × Example OPPONENTS!",
                                            "&7"
                                    )
                            ),
                            new TitleMessage(
                                    false,
                                    "&aTitle Yourself",
                                    "&7Subtitle",
                                    10, 20, 10
                            ),
                            new TitleMessage(
                                    false,
                                    "&aTitle Opponents",
                                    "&7Subtitle",
                                    10, 20, 10
                            ),
                            1.5f,
                            25,
                            new HashSet<>(
                                    Set.of(Material.BEDROCK)
                            ),
                            5f,
                            false,
                            false,
                            false,
                            10
                    ),
                    new DropToInventoryItem(
                            "drop_to_inv",
                            "drop_to_inv_category",
                            -1,
                            Item.builder()
                                    .amount(1)
                                    .material(Material.PLAYER_HEAD)
                                    .base64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGYzMzNjNzNjODQ4OWE5Y2EzNWQ1NjAzMTMwOTE4Yjg3NjA5ODRlYjlkMzAyOGUzMGU3NDI0N2RmZjg3M2JmZSJ9fX0=")
                                    .displayname("#9e9e9eCrown of Looting")
                                    .lore(new ArrayList<>(
                                            List.of(
                                                    "&7",
                                                    "&7Automatically sends dropped items",
                                                    "&7from killed players directly",
                                                    "&7to your inventory.",
                                                    "&7",
                                                    "&7• Usage: #666666{usage}",
                                                    "&7"
                                            )
                                    ))
                                    .unbreakable(true)
                                    .glow(true)
                                    .build(),
                            ItemType.DROP_TO_INV,
                            5,
                            "getcustomitem.droptoinv.bypass",
                            SoundConfig.builder()
                                    .enable(true)
                                    .sound(Sound.UI_BUTTON_CLICK)
                                    .build(),
                            new ArrayList<>(
                                    List.of(
                                            "&7",
                                            "#78ff69 × Example YOURSELF!",
                                            "&7"
                                    )
                            ),
                            new TitleMessage(
                                    false,
                                    "&aTitle Yourself",
                                    "&7Subtitle",
                                    10, 20, 10
                            )
                    ),
                    new ReflectionEffectItem(
                            "reflection_effect",
                            "reflection_effect_category",
                            -1,
                            Item.builder()
                                    .amount(1)
                                    .material(Material.NETHERITE_HELMET)
                                    .displayname("#8c19ffReflection Effect")
                                    .lore(new ArrayList<>(
                                            List.of(
                                                    "&7",
                                                    "#a74fff× Get damage and give effect",
                                                    "#a74fff  Chance: &f{chance}%",
                                                    "&7",
                                                    "&7• Usage: #a74fff{usage}",
                                                    "&7"
                                            )
                                    ))
                                    .unbreakable(true)
                                    .glow(true)
                                    .build(),
                            ItemType.REFLECTION_EFFECT,
                            10,
                            "getcustomitem.reflectioneffect.bypass",
                            SoundConfig.builder()
                                    .enable(true)
                                    .sound(Sound.BLOCK_ENCHANTMENT_TABLE_USE)
                                    .build(),
                            new ArrayList<>(
                                    List.of(
                                            "&7",
                                            "#78ff69 × Example YOURSELF!",
                                            "&7"
                                    )
                            ),
                            new ArrayList<>(
                                    List.of(
                                            "&7",
                                            "#78ff69 × Example OPPONENTS!",
                                            "&7"
                                    )
                            ),
                            new TitleMessage(
                                    false,
                                    "&aTitle Yourself",
                                    "&7Subtitle",
                                    10, 20, 10
                            ),
                            new TitleMessage(
                                    false,
                                    "&aTitle Opponents",
                                    "&7Subtitle",
                                    10, 20, 10
                            ),
                            new ArrayList<>(
                                    List.of(
                                            new PotionEffectConfig("SPEED", 5, 1)
                                    )
                            ),
                            50,
                            false,
                            true,
                            new HashSet<>(
                                    Set.of(EquipmentSlot.HEAD)
                            )
                    ),
                    new PokeballItem(
                            "poke_ball",
                            "poke_ball",
                            -1,
                            Item.builder()
                                    .amount(1)
                                    .material(Material.HEART_OF_THE_SEA)
                                    .displayname("#8c19ffPokeball")
                                    .lore(new ArrayList<>(
                                            List.of(
                                                    "&7",
                                                    "#a74fff× Hit the player and get",
                                                    "#a74fff  a chance to teleport them to you",
                                                    "#a74fff  Chance: &f{chance}",
                                                    "&7",
                                                    "&7• Usage: #a74fff{usage}",
                                                    "&7"
                                            )
                                    ))
                                    .unbreakable(true)
                                    .glow(true)
                                    .build(),
                            ItemType.POKE_BALL,
                            10,
                            "getcustomitem.pokeball.bypass",
                            SoundConfig.builder()
                                    .enable(true)
                                    .sound(Sound.BLOCK_ENCHANTMENT_TABLE_USE)
                                    .build(),
                            new ArrayList<>(
                                    List.of(
                                            "&7",
                                            "#78ff69 × Example YOURSELF!",
                                            "&7"
                                    )
                            ),
                            new ArrayList<>(
                                    List.of(
                                            "&7",
                                            "#78ff69 × Example OPPONENTS!",
                                            "&7"
                                    )
                            ),
                            new TitleMessage(
                                    false,
                                    "&aTitle Yourself",
                                    "&7Subtitle",
                                    10, 20, 10
                            ),
                            new TitleMessage(
                                    false,
                                    "&aTitle Opponents",
                                    "&7Subtitle",
                                    10, 20, 10
                            ),
                            3,
                            50,
                            25
                    ),
                    new ItemTier(
                            "excalibur_tier",
                            "excalibur_tier_category",
                            -1,
                            Item.builder()
                                    .amount(1)
                                    .material(Material.DIAMOND_SWORD)
                                    .displayname("#f2ff69Excalibur")
                                    .lore(new ArrayList<>(
                                            List.of(
                                                    "&7",
                                                    "&7need-int: {need-int}",
                                                    "&7need-double: {need-double}",
                                                    "&7requirement-int: {requirement-int}",
                                                    "&7requirement-double: {requirement-double}",
                                                    "&7percent: {percent}%",
                                                    "&7Level: {level}",
                                                    "&7Next-level: {level-increase}",
                                                    "&7Previous: {level-decrease}",
                                                    "&7Roman level: {level-roman}",
                                                    "&7Roman next-level: {level-roman-increase}",
                                                    "&7Roman previous-level: {level-roman-decrease}",
                                                    "&7",
                                                    "&7{progress}",
                                                    "&7"
                                            )
                                    ))
                                    .unbreakable(true)
                                    .glow(false)
                                    .build(),
                            ItemType.ITEM_TIER,
                            5,
                            "getcustomitem.excaliburtier.bypass",
                            SoundConfig.builder()
                                    .enable(true)
                                    .sound(Sound.UI_BUTTON_CLICK)
                                    .build(),
                            new HashSet<>(Set.of(
                                    EquipmentSlot.HAND
                            )),
                            new HashMap<>(Map.of(
                                    1, TierData.builder()
                                            .item(Item.builder()
                                                    .amount(1)
                                                    .material(Material.NETHERITE_SWORD)
                                                    .displayname("#f2ff69Excalibur")
                                                    .lore(new ArrayList<>(
                                                                    List.of(
                                                                            "&7",
                                                                            "&7need-int: {need-int}",
                                                                            "&7need-double: {need-double}",
                                                                            "&7requirement-int: {requirement-int}",
                                                                            "&7requirement-double: {requirement-double}",
                                                                            "&7percent: {percent}",
                                                                            "&7Level: {level}",
                                                                            "&7Next-level: {level-increase}",
                                                                            "&7Previous: {level-decrease}",
                                                                            "&7Roman level: {level-roman}",
                                                                            "&7Roman next-level: {level-roman-increase}",
                                                                            "&7Roman previous-level: {level-roman-decrease}",
                                                                            "&7",
                                                                            "&7{progress}",
                                                                            "&7"
                                                                    )
                                                            )
                                                    )
                                                    .enchantments(Map.of(
                                                            Enchantment.KNOCKBACK, 1
                                                    ))
                                                    .attributeModifiers(new HashMap<>(Map.of(
                                                            Attribute.GENERIC_LUCK, new ArrayList<>(List.of(
                                                                    new AttributeModifier(UUID.randomUUID(), "excalibur", 2, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND)
                                                            ))
                                                    )))
                                                    .unbreakable(true)
                                                    .glow(false)
                                                    .build())
                                            .requirementValue(10)
                                            .actionEvents(new HashMap<>(
                                                    Map.of(ActionEvent.KILL_ENTITY, new HashMap<>(
                                                            Map.of(EntityType.ZOMBIE, 1.0)
                                                    ))
                                            ))
                                            .build()
                            )),
                            "-",
                            10,
                            "#7cff3b",
                            "#636363"

                    )

            )
    );

}
