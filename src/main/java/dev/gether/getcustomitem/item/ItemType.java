package dev.gether.getcustomitem.item;


import dev.gether.getcustomitem.item.customize.*;
import dev.gether.getcustomitem.item.customize.itemtier.ItemTier;

public enum ItemType {
    HOOK(HookItem.class),
    CROSSBOW(CrossBowItem.class),
    COBWEB_GRENADE(CobwebGrenade.class),
    EFFECT_RADIUS(EffectRadiusItem.class),
    FROZEN_SWORD(FrozenSword.class),
    ANTY_COBWEB(AntyCobweb.class),
    BEAR_FUR(BearFurItem.class),
    MAGIC_TOTEM(MagicTotemItem.class),
    HIT_EFFECT(HitEffectItem.class),
    SNOWBALL_TP(SnowballTPItem.class),
    INSTA_HEAL(InstaHealItem.class),
    PUSH_ITEM(PushItem.class),
    THROW_UP(ThrowUpItem.class),
    LIGHTNING_ITEM(LightningItem.class),
    SHIELD_ITEM(ShieldItem.class),
    EGG_THROW_UP(EggThrowItItem.class),
    THROWING_ENDER_PEARLS(ThrowingEnderPearlsItem.class),
    ITEM_EFFECT(ItemEffect.class),
    SHUFFLE_ITEM(ShuffleInventoryItem.class),
    CUPIDS_BOW(CupidBowItem.class),
    STOP_FLYING(StopFlyingItem.class),
    ITEMS_BAG(ItemsBag.class),
    ITEM_TIER(ItemTier.class),
    EXPLOSION_BALL(ExplosionBallItem.class),
    DROP_TO_INV(DropToInventoryItem.class),
    POKE_BALL(PokeballItem.class),
    REFLECTION_EFFECT(ReflectionEffectItem.class);

    private final Class<? extends CustomItem> itemClass;

    ItemType(Class<? extends CustomItem> itemClass) {
        this.itemClass = itemClass;
    }

    public Class<? extends CustomItem> getItemClass() {
        return itemClass;
    }
}
