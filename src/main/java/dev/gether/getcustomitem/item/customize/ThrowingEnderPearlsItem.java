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
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeName("throwing_ender_pearls")
public class ThrowingEnderPearlsItem extends CustomItem {
    private float speed;
    private double maxRange;

    private Item throwingItem;

    @JsonIgnore
    private transient ItemStack throwingItemStack;


    public ThrowingEnderPearlsItem(String key,
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
    protected Map<String, String> replacementValues() {
        return Map.of(
                "{usage}", String.valueOf(usage)
        );
    }


    public void init() {
        super.init();
        throwingItemStack = throwingItem.getItemStack();
    }

    public void throwEnderPearls(Player player) {
        final Vector direction = player.getEyeLocation().getDirection().multiply(speed);
        EnderPearl enderPearl = player.getWorld().spawn(player.getEyeLocation().add(direction.getX(), direction.getY(), direction.getZ()), EnderPearl.class);
        enderPearl.setItem(throwingItemStack);
        enderPearl.setShooter(player);
        enderPearl.setVelocity(direction);
        enderPearl.setInvulnerable(true);
        maxRangeTask(enderPearl, maxRange);
    }
}
