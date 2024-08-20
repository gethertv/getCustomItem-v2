package dev.gether.getcustomitem.item.customize;

import com.fasterxml.jackson.annotation.JsonTypeName;
import dev.gether.getconfig.domain.Item;
import dev.gether.getconfig.domain.config.TitleMessage;
import dev.gether.getconfig.domain.config.particles.ParticleConfig;
import dev.gether.getconfig.domain.config.potion.PotionEffectConfig;
import dev.gether.getconfig.domain.config.sound.SoundConfig;
import dev.gether.getconfig.utils.ParticlesUtil;
import dev.gether.getcustomitem.GetCustomItem;
import dev.gether.getcustomitem.item.CustomItem;
import dev.gether.getcustomitem.item.ItemType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Entity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeName("crossbow")
public class CrossBowItem extends CustomItem {
    private ParticleConfig particleConfig;
    private String permissionIgnoreEffect = "getcustomitem.crossbow.effect.bypass";
    private double chance = 30;
    private double maxRange;

    public CrossBowItem(String key,
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
                        ParticleConfig particleConfig,
                        String permissionIgnoreEffect,
                        double chance,
                        double maxRange) {

        super(key, categoryName, usage, item, itemType, cooldown,
                permissionBypass, soundConfig, notifyYourself,
                notifyOpponents, titleYourself, titleOpponents);

        this.particleConfig = particleConfig;
        this.permissionIgnoreEffect = permissionIgnoreEffect;
        this.chance = chance;
        this.maxRange = maxRange;
    }

    public void runParticles(Entity arrow) {

        // check the particles is enabled
        if(!particleConfig.isEnable())
            return;

        new BukkitRunnable() {
            @Override
            public void run() {
                // check if the arrow has landed or is removed
                if (arrow.isOnGround() || !arrow.isValid()) {
                    this.cancel();
                    return;
                }

                ParticlesUtil.spawnParticles(arrow, particleConfig);
            }
        }.runTaskTimerAsynchronously(GetCustomItem.getInstance(), 0L, 0L);
    }
    @Override
    protected Map<String, String> replacementValues() {
        return Map.of(
                "{chance}", String.valueOf(chance)
        );
    }
}
