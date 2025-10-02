# IsTyping

A lightweight typing indicator mod for Minecraft multiplayer that shows when other players are composing chat messages.

## What does it do?

IsTyping adds real-time typing indicators to your multiplayer experience. When a player starts typing in chat, other players will see a subtle notification at the bottom of their screen showing who is currently typing. The indicator automatically disappears when the player stops typing or sends their message.

## Features

- **Real-time indicators** - See who is typing as it happens
- **Multi-language support** - Automatic language detection based on your Minecraft settings
- **Customizable appearance** - Change colors, position, and animation settings
- **Server-side control** - Server administrators can configure timeout and anti-spam settings
- **Low performance impact** - Optimized network communication and rendering
- **Command filtering** - Commands (starting with /) don't trigger typing indicators

## Requirements

**Both client and server need:**
- Minecraft 1.20.1
- Fabric Loader 0.17.2+
- Fabric API
- Architectury API 9.2.14+

**Note:** This mod must be installed on both the client and server to work.

## Installation

1. Download the mod file
2. Place it in your `mods` folder (both client and server)
3. Launch Minecraft
4. Configuration files will be generated automatically on first run

## Configuration

### Client Settings

Configure your personal display preferences in `config/istyping-client.properties`:

| Setting | Description | Default |
|---------|-------------|---------|
| `language` | UI language (auto/en/es/pt/fr/de/ru/ja/ko/zh) | auto |
| `overlay_y_offset` | Distance from bottom of screen (10-100) | 28 |
| `max_displayed_players` | Maximum players shown at once (1-10) | 3 |
| `animation_speed_ms` | Dot animation speed (100-2000) | 500 |
| `text_color` | Text color in ARGB hex format | FFAAAAAA |
| `background_color` | Background color in ARGB hex format | 80000000 |
| `show_animation` | Enable animated dots (true/false) | true |
| `fade_speed` | Fade in/out speed (0.01-1.0) | 0.1 |

**Example configuration:**
```properties
language=auto
overlay_y_offset=28
max_displayed_players=3
animation_speed_ms=500
text_color=FFAAAAAA
background_color=80000000
show_animation=true
fade_speed=0.1
```

### Server Settings

Server administrators can configure behavior in `config/istyping.properties`:

| Setting | Description | Default |
|---------|-------------|---------|
| `typing_timeout_ms` | Time until player stops typing (1000-10000) | 4000 |
| `heartbeat_interval_ms` | Client heartbeat interval (1000-5000) | 2000 |
| `max_tracked_players` | Maximum players tracked (10-500) | 50 |
| `cooldown_between_typing_ms` | Anti-spam cooldown (100-2000) | 500 |
| `enable_typing_indicator` | Enable/disable mod (true/false) | true |

**Example configuration:**
```properties
typing_timeout_ms=4000
heartbeat_interval_ms=2000
max_tracked_players=50
cooldown_between_typing_ms=500
enable_typing_indicator=true
```

## Why separate client and server configurations?

The mod uses a **client-server architecture** to ensure proper synchronization:

- **Server configuration** controls the behavior and validation of typing events. This prevents abuse and ensures consistent timing across all players.
- **Client configuration** only affects visual presentation on your screen. Each player can customize how they see typing indicators without affecting others.

This design ensures that even if clients have different visual settings, the typing detection and synchronization remain accurate and fair for everyone.

## Supported Languages

The mod automatically detects your Minecraft language and displays messages accordingly:

English, Spanish, Portuguese, French, German, Russian, Japanese, Korean, and Chinese.

## How it works

1. When you open the chat screen and start typing, your client notifies the server
2. The server validates the event and broadcasts it to other players
3. Other players see your typing indicator on their screen
4. When you stop typing or send the message, the indicator disappears
5. Commands (starting with /) are filtered and won't trigger indicators

## Performance

IsTyping is designed to be lightweight:
- Minimal network traffic using a heartbeat system
- Efficient client-side rendering with fade animations
- Server-side timeout management to prevent memory leaks
- Optimized state tracking

## Support

If you enjoy this mod, consider supporting the developer:

[![Ko-fi](https://img.shields.io/badge/Ko--fi-Support-FF5E5B?logo=ko-fi&logoColor=white)](https://ko-fi.com/nozzdev)
