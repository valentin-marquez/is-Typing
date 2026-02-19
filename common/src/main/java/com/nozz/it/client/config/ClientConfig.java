package com.nozz.it.client.config;

import com.nozz.it.config.SimpleTomlConfig;
import java.io.File;

/**
 * Client configuration for IsTyping.
 * Controls visual and presentation aspects.
 * Lives in :common so it can be shared by all platform implementations.
 */
public class ClientConfig {
    private static ClientConfig INSTANCE;

    // Default values
    private int overlayYOffset = 28;
    private int maxDisplayedPlayers = 3;
    private int animationSpeedMs = 500;
    private int textColor = 0xFFAAAAAA;
    private int backgroundColor = 0x80000000;
    private boolean showAnimation = true;
    private float fadeSpeed = 0.1f;
    private double easterEggChance = 0.000001;

    private ClientConfig() {}

    public static ClientConfig getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ClientConfig();
        }
        return INSTANCE;
    }

    /**
     * Loads configuration from config/istyping.toml
     */
    public void load(File configDir) {
        File configFile = new File(configDir, "istyping.toml");
        SimpleTomlConfig toml = new SimpleTomlConfig();

        if (configFile.exists()) {
            toml.load(configFile);
            
            // Check if Client section exists, if not, we might want to add defaults if we are in a context where we should?
            // But load() is usually called to READ.
            // However, typical mod behavior is "create default if missing".
            // Since we share the file, "missing" might mean the FILE is missing OR the SECTION is missing.
            
            // If the file exists but no [Client] section, we should probably add it and save,
            // so the user sees the options.
            if (toml.get("Client", "text_color", null) == null) {
                // Section likely missing or empty, populate defaults
                updateToDefaults(toml);
                toml.save(configFile);
            }

            // Load values
            maxDisplayedPlayers = toml.getInt("Client", "max_displayed_players", 3);
            easterEggChance = toml.getDouble("Client", "easter_egg_chance", 0.000001);
            
            overlayYOffset = toml.getInt("Client", "overlay_y_offset", 28);
            animationSpeedMs = toml.getInt("Client", "animation_speed_ms", 500);
            
            String textColStr = toml.get("Client", "text_color", "FFAAAAAA");
            textColor = parseColor(textColStr);
            
            String bgColStr = toml.get("Client", "background_color", "80000000");
            backgroundColor = parseColor(bgColStr);
            
            showAnimation = toml.getBoolean("Client", "show_animation", true);
            fadeSpeed = (float) toml.getDouble("Client", "fade_speed", 0.1);
            
            com.nozz.it.IsTyping.LOGGER.info("[IsTyping] Client config loaded (TOML)");
        } else {
            createDefaultConfig(configFile);
        }
    }

    private void createDefaultConfig(File configFile) {
        try {
            configFile.getParentFile().mkdirs();
            SimpleTomlConfig toml = new SimpleTomlConfig();
            updateToDefaults(toml);
            toml.save(configFile);
            com.nozz.it.IsTyping.LOGGER.info("[IsTyping] Created default client config (TOML)");
        } catch (Exception e) {
            com.nozz.it.IsTyping.LOGGER.error("[IsTyping] Failed to create client config: " + e.getMessage());
        }
    }

    private void updateToDefaults(SimpleTomlConfig toml) {
        toml.set("Client", "max_displayed_players", 3);
        toml.set("Client", "easter_egg_chance", 0.000001);
        toml.set("Client", "overlay_y_offset", 28);
        toml.set("Client", "animation_speed_ms", 500);
        toml.set("Client", "text_color", "FFAAAAAA");
        toml.set("Client", "background_color", "80000000");
        toml.set("Client", "show_animation", true);
        toml.set("Client", "fade_speed", 0.1);
    }

    private int parseColor(String hex) {
        try {
            return (int) Long.parseLong(hex, 16);
        } catch (NumberFormatException e) {
            return 0xFFAAAAAA;
        }
    }
    
    // Getters detected language is now handled purely by MC, calls to this were removed or should be replaced by MC logic directly if needed.
    // Leaving this method out as it's unused.

    public int getOverlayYOffset() { return overlayYOffset; }
    public int getMaxDisplayedPlayers() { return maxDisplayedPlayers; }
    public int getAnimationSpeedMs() { return animationSpeedMs; }
    public int getTextColor() { return textColor; }
    public int getBackgroundColor() { return backgroundColor; }
    public boolean isShowAnimation() { return showAnimation; }
    public float getFadeSpeed() { return fadeSpeed; }
    public double getEasterEggChance() { return easterEggChance; }
}
