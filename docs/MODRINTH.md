![Example of typing indicator](https://raw.githubusercontent.com/valentin-marquez/is-Typing/refs/heads/1.20.1/docs/example.webp)

A lightweight typing indicator mod for Minecraft multiplayer that shows when other players are composing chat messages.

## Features

- **Real-time typing indicators** - See who is typing as it happens
- **Customizable display** - Adjust colors, position, and animations to your liking
- **Multi-language support** - Available in 9 languages with automatic detection
- **Performance optimized** - Minimal impact on your game

**Must be installed on BOTH client and server**

## Configuration

### Client Settings (`config/istyping-client.properties`)

Customize how typing indicators appear on your screen:

```properties
language=auto                    # UI language (auto/en/es/pt/fr/de/ru/ja/ko/zh)
overlay_y_offset=28             # Distance from bottom (10-100)
max_displayed_players=3         # Max players shown at once (1-10)
animation_speed_ms=500          # Dot animation speed (100-2000)
text_color=FFAAAAAA            # Text color (ARGB hex)
background_color=80000000       # Background color (ARGB hex)
show_animation=true             # Enable animated dots
fade_speed=0.1                  # Fade in/out speed (0.01-1.0)
```

### Server Settings (`config/istyping.properties`)

Control typing behavior and prevent spam:

```properties
typing_timeout_ms=4000              # Time until typing stops (1000-10000)
heartbeat_interval_ms=2000          # Update interval (1000-5000)
max_tracked_players=50              # Max players tracked (10-500)
cooldown_between_typing_ms=500     # Anti-spam cooldown (100-2000)
enable_typing_indicator=true        # Enable/disable mod
```

## Supported Languages

English • Spanish • Portuguese • French • German • Russian • Japanese • Korean • Chinese

Language is automatically detected from your Minecraft settings.

## Support

[![Ko-fi](https://img.shields.io/badge/Ko--fi-Support-FF5E5B?logo=ko-fi&logoColor=white)](https://ko-fi.com/nozzdev)