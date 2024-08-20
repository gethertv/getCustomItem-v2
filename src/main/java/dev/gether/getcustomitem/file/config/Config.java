package dev.gether.getcustomitem.file.config;

import dev.gether.getconfig.GetConfig;
import dev.gether.getconfig.annotation.Comment;
import lombok.Getter;
import lombok.Setter;

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

}
