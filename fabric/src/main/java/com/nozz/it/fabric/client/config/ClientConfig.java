package com.nozz.it.fabric.client.config;

import net.minecraft.client.MinecraftClient;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Client configuration for IsTyping.
 * Controls visual and presentation aspects.
 */
public class ClientConfig {
    private static ClientConfig INSTANCE;
    
    // Default values
    private String language = "auto";              // "auto", "en", "es", "pt", "fr", etc.
    private int overlayYOffset = 28;               // Offset from bottom of screen
    private int maxDisplayedPlayers = 3;           // Maximum players shown in UI
    private int animationSpeedMs = 500;            // Dot animation speed
    private int textColor = 0xFFAAAAAA;            // Text color (ARGB)
    private int backgroundColor = 0x80000000;      // Background color (ARGB)
    private boolean showAnimation = true;          // Show dot animation
    private float fadeSpeed = 0.1f;                // Fade in/out speed
    
    private ClientConfig() {}
    
    public static ClientConfig getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ClientConfig();
        }
        return INSTANCE;
    }
    
    /**
     * Loads configuration from config/istyping-client.properties
     */
    public void load(File configDir) {
        File configFile = new File(configDir, "istyping-client.properties");
        
        if (!configFile.exists()) {
            createDefaultConfig(configFile);
            return;
        }
        
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(configFile)) {
            props.load(fis);
            
            // Load values
            language = props.getProperty("language", "auto");
            overlayYOffset = getValidatedInt(props, "overlay_y_offset", 28, 10, 100);
            maxDisplayedPlayers = getValidatedInt(props, "max_displayed_players", 3, 1, 10);
            animationSpeedMs = getValidatedInt(props, "animation_speed_ms", 500, 100, 2000);
            textColor = parseColor(props.getProperty("text_color", "FFAAAAAA"));
            backgroundColor = parseColor(props.getProperty("background_color", "80000000"));
            showAnimation = Boolean.parseBoolean(props.getProperty("show_animation", "true"));
            fadeSpeed = Float.parseFloat(props.getProperty("fade_speed", "0.1"));
            
            System.out.println("[IsTyping] Client config loaded");
        } catch (Exception e) {
            System.err.println("[IsTyping] Failed to load client config: " + e.getMessage());
        }
    }
    
    private void createDefaultConfig(File configFile) {
        Properties props = new Properties();
        
        try {
            configFile.getParentFile().mkdirs();
            
            props.setProperty("language", "auto");
            props.setProperty("overlay_y_offset", "28");
            props.setProperty("max_displayed_players", "3");
            props.setProperty("animation_speed_ms", "500");
            props.setProperty("text_color", "FFAAAAAA");
            props.setProperty("background_color", "80000000");
            props.setProperty("show_animation", "true");
            props.setProperty("fade_speed", "0.1");
            
            try (FileOutputStream fos = new FileOutputStream(configFile)) {
                props.store(fos,
                    "IsTyping Client Configuration\n" +
                    "# language: UI language (auto, en, es, pt, fr, de, ru, ja, ko, zh) - 'auto' detects from Minecraft\n" +
                    "# overlay_y_offset: Distance from bottom of screen (10-100)\n" +
                    "# max_displayed_players: Max players shown in typing indicator (1-10)\n" +
                    "# animation_speed_ms: Speed of dot animation in milliseconds (100-2000)\n" +
                    "# text_color: Text color in ARGB hex format (AARRGGBB)\n" +
                    "# background_color: Background color in ARGB hex format (AARRGGBB)\n" +
                    "# show_animation: Enable animated dots (true/false)\n" +
                    "# fade_speed: Speed of fade in/out effect (0.01-1.0)"
                );
            }
            
            System.out.println("[IsTyping] Created default client config");
        } catch (IOException e) {
            System.err.println("[IsTyping] Failed to create client config: " + e.getMessage());
        }
    }
    
    private int getValidatedInt(Properties props, String key, int defaultValue, int min, int max) {
        try {
            int value = Integer.parseInt(props.getProperty(key, String.valueOf(defaultValue)));
            return Math.max(min, Math.min(max, value));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    private int parseColor(String hex) {
        try {
            return (int) Long.parseLong(hex, 16);
        } catch (NumberFormatException e) {
            return 0xFFAAAAAA;
        }
    }
    
    /**
     * Detects the Minecraft client language
     * Returns ISO 639-1 2-letter code (en, es, pt, etc.)
     */
    public String getDetectedLanguage() {
        if (!"auto".equals(language)) {
            return language;
        }
        
        try {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client != null && client.getLanguageManager() != null) {
                String mcLang = client.getLanguageManager().getLanguage();
                // Minecraft usa formato "es_es", "en_us", etc.
                // Extraemos solo la primera parte
                if (mcLang != null && mcLang.length() >= 2) {
                    return mcLang.substring(0, 2).toLowerCase();
                }
            }
        } catch (Exception e) {
            System.err.println("[IsTyping] Failed to detect language: " + e.getMessage());
        }
        
        return "en"; // Fallback to English
    }
    
    // Getters
    public String getLanguage() { return language; }
    public int getOverlayYOffset() { return overlayYOffset; }
    public int getMaxDisplayedPlayers() { return maxDisplayedPlayers; }
    public int getAnimationSpeedMs() { return animationSpeedMs; }
    public int getTextColor() { return textColor; }
    public int getBackgroundColor() { return backgroundColor; }
    public boolean isShowAnimation() { return showAnimation; }
    public float getFadeSpeed() { return fadeSpeed; }
}