package dev.gether.getcustomitem.item.customize;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;
import dev.gether.getconfig.domain.Item;
import dev.gether.getconfig.domain.config.TitleMessage;
import dev.gether.getconfig.domain.config.sound.SoundConfig;
import dev.gether.getcustomitem.GetCustomItem;
import dev.gether.getcustomitem.item.CustomItem;
import dev.gether.getcustomitem.item.ItemType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeName("egg_throw_it")
public class EggThrowItItem extends CustomItem {

    private double pushPower;
    private double speedEgg;
    private double chance;
    private String permissionByPass = "egg.throw.bypass";
    private Item throwingItem;

    @JsonIgnore
    private transient ItemStack throwingItemStack;

    private double maxRange;


    public EggThrowItItem(String key,
                       String categoryName,
                       int usage,
                       Item item,
                       ItemType itemType,
                       int cooldown,
                       String permissionBypass,
                       SoundConfig soundConfig,
                       List<String> notifyYourself,
                       List<String> notifyOpponents,
                       TitleMessage titleYourself,
                       TitleMessage titleOpponents,
                       double pushPower,
                       double speedEgg,
                       double chance,
                       double maxRange,
                       Item throwingItem
    ) {

        super(key, categoryName, usage, item, itemType, cooldown,
                permissionBypass, soundConfig, notifyYourself,
                notifyOpponents, titleYourself, titleOpponents);

        this.pushPower = pushPower;
        this.chance = chance;
        this.speedEgg = speedEgg;
        this.maxRange = maxRange;
        this.throwingItem = throwingItem;
        this.throwingItemStack = throwingItem.getItemStack();

    }

    public void init() {
        super.init();
        throwingItemStack = throwingItem.getItemStack();
    }

    @Override
    protected Map<String, String> replacementValues() {
        return Map.of(
                "{power-push}", String.valueOf(pushPower),
                "{chance}", String.valueOf(chance)
        );
    }

    public void throwEgg(Player player) {
        final Vector direction = player.getEyeLocation().getDirection().multiply(speedEgg);
        Egg egg = player.getWorld().spawn(player.getEyeLocation().add(direction.getX(), direction.getY(), direction.getZ()), Egg.class);
        egg.setItem(throwingItemStack);
        egg.setShooter(player);
        egg.setVelocity(direction);
        egg.setInvulnerable(true);
        egg.getPersistentDataContainer().set(new NamespacedKey(GetCustomItem.getInstance(), getKey()), PersistentDataType.STRING, "true");
        maxRangeTask(egg, maxRange);
    }
}
