package dev.gether.getcustomitem.item.customize;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;
import dev.gether.getconfig.domain.Item;
import dev.gether.getconfig.domain.config.TitleMessage;
import dev.gether.getconfig.domain.config.sound.SoundConfig;
import dev.gether.getcustomitem.item.CustomItem;
import dev.gether.getcustomitem.item.ItemType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeName("snowball_tp")
public class SnowballTPItem extends CustomItem {
    private float speed;
    private double maxRange;
    @JsonIgnore
    private transient ItemStack throwingItemStack;
    private Item throwingItem;

    public SnowballTPItem(String key,
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
                          float speed,
                          double maxRange,
                          Item throwingItem) {

        super(key, categoryName, usage, item, itemType, cooldown,
                permissionBypass, soundConfig, notifyYourself,
                notifyOpponents, titleYourself, titleOpponents);


        this.speed = speed;
        this.maxRange = maxRange;
        this.throwingItem = throwingItem;
        this.throwingItemStack = throwingItem.getItemStack();
    }

    @Override
    public void init() {
        super.init();
        this.throwingItemStack = throwingItem.getItemStack();
    }

    @Override
    protected Map<String, String> replacementValues() {
        return Map.of(
                "{usage}", String.valueOf(usage)
        );
    }

    public void throwSnowball(Player player) {
        final Vector direction = player.getEyeLocation().getDirection().multiply(speed);
        Snowball snowball = player.getWorld().spawn(player.getEyeLocation().add(direction.getX(), direction.getY(), direction.getZ()), Snowball.class);
        snowball.setItem(throwingItemStack);
        snowball.setShooter(player);
        snowball.setVelocity(direction);
        snowball.setInvulnerable(true);
        maxRangeTask(snowball, maxRange);
    }
}
