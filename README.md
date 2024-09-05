Certainly! Here's a styled version of the documentation suitable for GitHub:

# getCustomItem

getCustomItem is an advanced Spigot plugin that allows you to create and manage custom items with unique properties and effects on your Minecraft server.

## 📋 Table of Contents

- [Requirements](#-requirements)
- [Installation](#-installation)
- [Commands](#-commands)
- [Creating Custom Items](#️-creating-custom-items)
- [Pre-defined Custom Items](#-pre-defined-custom-items)
- [Configuration](#️-configuration)
- [WorldGuard Integration](#-worldguard-integration)
- [Support](#-support)

## 🔧 Requirements

* WorldGuard (Note: WorldGuard requires WorldEdit or FAWE to function)

## 🚀 Installation

1. Download the getCustomItem plugin.
2. Place the downloaded JAR file into your server's `/plugins` folder.
3. Restart your server or reload plugins.
4. Configure the plugin as needed.

## 🎮 Commands

All commands require the permission `getcustomitem.admin`

| Command | Description |
|---------|-------------|
| `/getcustomitem give <player> <item_key> [amount]` | Give a custom item to a player |
| `/getcustomitem reload` | Reload the plugin configuration |
| `/getcustomitem debug` | Display information about the item in your hand |
| `/getcustomitem setspawn` | Set spawn location for certain item effects |

## 🛠️ Creating Custom Items

Creating new custom items is simple:

1. Choose an existing item file with the type of effect you want (e.g., one that applies potion effects).
2. Copy this file to the `plugins/GetCustomItem/items/` folder.
3. Rename the copied .yml file to a unique name.
4. Open the file and change the `key` value (preferably to match the file name).
5. Modify other properties as desired.

## 🎁 Pre-defined Custom Items

getCustomItem includes 32 pre-defined custom items:

1. **Magic Fishing Rod** - Hook and pull yourself towards a location
2. **Teleporting Crossbow** - Chance to teleport hit players to the shooter
3. **Cobweb Grenade** - Creates a temporary cobweb trap
4. **Stick of Levitation** - Gives levitation effect to nearby players
5. **Yeti Eye** - Applies weakness effect to nearby players
6. **Air Filter** - Removes negative effects from the user
7. **Ice Rod** - Removes positive effects from nearby players
8. **Frozen Sword** - Chance to freeze hit players
9. **Anty-Cobweb** - Removes cobwebs in a specified radius
10. **Magic Totem** - Preserves inventory percentage upon death
11. **Bear Fur** - Temporarily reduces damage taken
12. **Wizard's Staff** - Applies custom effects to hit players
13. **Snowball TP** - Swaps locations with hit players
14. **Vampire Blow** - Chance to instantly heal the user
15. **Push Stick** - Pushes hit players away
16. **Throw Up Stick** - Throws nearby players upward
17. **Lightning Axe** - Strikes hit players with lightning
18. **Shield** - Chance to block incoming attacks
19. **Jump Egg** - Throws players upward when hit
20. **Dragon Sword** - Throws ender pearls on right-click
21. **Lollipop** - Gives effects when held
22. **Crown** - Gives effects when worn
23. **Cupid's Stick** - Applies effects when held
24. **Rubik's Cube** - Shuffles inventory of hit players
25. **Cupid's Bow** - Applies blindness to hit players
26. **Anti-Elytra Rod** - Prevents Elytra use for hit players
27. **Loot Bag** - Stores items from defeated players
28. **Explosion Ball of Durability** - Damages item durability
29. **Crown of Looting** - Auto-collects drops from killed players
30. **Reflection Effect Helmet** - Reflects effects to attackers
31. **Pokeball** - Chance to teleport hit players to thrower
32. **Excalibur** - Upgradeable sword with increasing power

## ⚙️ Configuration

Main configuration files are in the `plugins/GetCustomItem/` directory:

- `config.yml`: General plugin settings
- `database.yml`: Database connection settings (if applicable)
- `lang.yml`: Language and message settings

Custom item configurations are in the `plugins/GetCustomItem/items/` directory.

## 🌐 WorldGuard Integration

getCustomItem integrates with WorldGuard for region-based item usage restrictions.

## 🆘 Support

For support, bug reports, or feature requests:
- [Open an issue](https://github.com/yourusername/getCustomItem-v2/issues)
- [Join our Discord](https://dc.gether.dev/)

---

<p align="center">
  Made with ❤️ by gether.dev
</p>
