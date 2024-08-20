package dev.gether.getcustomitem.item.customize;


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
import org.bukkit.Material;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeName("explosion_ball")
public class ExplosionBallItem extends CustomItem {
    private float speed;
    private double maxRange;
    private Set<Material> whitelistMaterial;
    private float explosionPower;
    private boolean setFire;
    private boolean breakBlocks;
    private boolean igniteFireball;
    private int destroyDurability;


    public ExplosionBallItem(String key,
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
                             Set<Material> whitelistMaterial,
                             float explosionPower,
                             boolean setFire,
                             boolean breakBlocks,
                             boolean igniteFireball,
                             int destroyDurability) {

        super(key, categoryName, usage, item, itemType, cooldown,
                permissionBypass, soundConfig, notifyYourself,
                notifyOpponents, titleYourself, titleOpponents);


        this.speed = speed;
        this.maxRange = maxRange;
        this.whitelistMaterial = whitelistMaterial;
        this.explosionPower = explosionPower;
        this.setFire = setFire;
        this.breakBlocks = breakBlocks;
        this.igniteFireball = igniteFireball;
        this.destroyDurability = destroyDurability;
    }

    @Override
    protected Map<String, String> replacementValues() {
        return Map.of(
                "{usage}", String.valueOf(usage),
                "{explosion-power}", String.format(Locale.US, "%.2f", explosionPower),
                "{destroy-durability}", String.format(Locale.US, "%d", destroyDurability)
        );
    }

    public void throwExplosionBall(Player player) {
        final Vector direction = player.getEyeLocation().getDirection().multiply(speed);
        Fireball fireball = player.getWorld().spawn(
                player.getEyeLocation()
                        .add(
                                direction.getX(),
                                direction.getY(),
                                direction.getZ()
                        ),
                Fireball.class
        );
        fireball.setMetadata(getKey(), new FixedMetadataValue(GetCustomItem.getInstance(), true));
        fireball.setShooter(player);
        fireball.setVelocity(direction);
        fireball.setInvulnerable(true);
        fireball.setInvulnerable(true);
        fireball.setIsIncendiary(false);
        if(!igniteFireball)
            fireball.setFireTicks(0);

        maxRangeTask(fireball, maxRange);
    }
}
