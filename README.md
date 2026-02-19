![is Typing Preview](https://raw.githubusercontent.com/valentin-marquez/is-Typing/refs/heads/1.20.1/docs/example.webp)

[![Architectury API](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/requires/architectury-api_vector.svg)](https://github.com/architectury/architectury-api)
[![Fabric](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/supported/fabric_vector.svg)](https://fabricmc.net/) [![NeoForge](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/supported/neoforge_vector.svg)](https://neoforged.net/) [![Forge](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/supported/forge_vector.svg)](https://files.minecraftforge.net/)
[![Modrinth](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/modrinth_vector.svg)](https://modrinth.com/mod/is-typing)

**is Typing** is a lightweight multiplayer utility that displays a visual indicator when other players are typing in chat. It helps bridge the gap between voice and text, allowing for more natural conversation flow without interrupting players who are composing a message.

We currently support Minecraft **1.20.1** (Forge/Fabric) and **1.21.x** (NeoForge/Fabric).

## Features

**Real-time Feedback**
See exactly who is typing as it happens. The indicator appears unobtrusively on your screen, keeping you informed without cluttering your view.

**Privacy & Stealth**
Designed with server administration in mind:
* **Command Privacy:** The mod detects when a message starts with a command slash (`/`) and will **not** show the typing indicator. Your commands and teleports remain secret.
* **Vanish Support:** If a player is vanished or not listed in the tab list, their typing status is hidden to prevent accidental detection.

**Customization**
Adjust the position, colors, and animation speed to match your preference. The mod includes built-in support for **9 languages** (English, Spanish, Portuguese, French, German, Russian, Japanese, Korean, Chinese).

## Installation

**Note: This mod must be installed on BOTH the Client and the Server.**

1.  Download the `.jar` file for your specific version.
2.  Install **Architectury API** (Required).
3.  Place both files into your `mods` folder.

| Version | Supported Loaders |
| :--- | :--- |
| **1.21.4** | NeoForge, Fabric (Quilt compatible) |
| **1.21.1** | NeoForge, Fabric (Quilt compatible) |
| **1.20.1** | Forge, Fabric |

## Configuration

The mod uses a single configuration file: `config/istyping.toml`. The content of this file depends on where the mod is installed.

### Client (`config/istyping.toml`)
When installed on a Client, the file will contain the `[Client]` section.

```toml
[Client]
max_displayed_players = 3   # Max typing notifications shown at once
easter_egg_chance = 1.0E-6  # Probability of showing hidden easter egg
overlay_y_offset = 28       # Distance from bottom of screen
animation_speed_ms = 500    # Speed of the "..." animation
text_color = "FFAAAAAA"     # ARGB Hex color
background_color = "80000000" # Background transparency
show_animation = true       # Enable animated dots
fade_speed = 0.1            # Speed of fade in/out effect
```

### Server (`config/istyping.toml`)
When installed on a Server, the file will contain the `[Server]` section.

```toml
[Server]
enable_typing_indicator = true
max_tracked_players = 50
typing_timeout_ms = 4000      # Milliseconds until a player is considered to have stopped typing
heartbeat_interval_ms = 2000  # Expected interval between client heartbeats
cooldown_between_typing_ms = 500 # Anti-spam cooldown
```

**Note:** In Singleplayer, the file will contain **both** sections.