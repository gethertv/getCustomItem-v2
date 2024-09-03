package dev.gether.getcustomitem.file.config;

import dev.gether.getconfig.GetConfig;
import dev.gether.getconfig.annotation.Comment;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class Config extends GetConfig {

    @Comment({
            "",
            "author - https://www.spigotmc.org/resources/authors/gethertv.571046/",
            "discord: https://dc.gether.dev",
            "",
            "https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/potion/PotionEffectType.html",
            "https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html",
            "https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Particle.html",
            ""
    })
    private String discord = "https://dc.gether.dev";
    private boolean defaultItems = true;

    private Location spawnLocation = null;

    private String cooldownMessage = "&cMusisz odczekac {time}";
    private Map<Object, Integer> cooldown = new HashMap<>(Map.of(
            EntityType.PLAYER, 15
    ));

}
