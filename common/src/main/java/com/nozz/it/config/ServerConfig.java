package com.nozz.it.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Server configuration for IsTyping.
 * Controls authoritative and performance aspects.
 */
public class ServerConfig {
    private static ServerConfig INSTANCE;
    
    private int typingTimeoutMs = 4000;
    private int heartbeatIntervalMs = 2000;
    private int maxTrackedPlayers = 50;
    private int cooldownBetweenTypingMs = 500;
    private boolean enableTypingIndicator = true;
    
    private ServerConfig() {}
    
    public static ServerConfig getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ServerConfig();
        }
        return INSTANCE;
    }
    
    /**
     * Loads configuration from config/istyping.properties
     */
    public void load(File configDir) {
        File configFile = new File(configDir, "istyping.properties");
        
        if (!configFile.exists()) {
            createDefaultConfig(configFile);
            return;
        }
        
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(configFile)) {
            props.load(fis);
            
            typingTimeoutMs = getValidatedInt(props, "typing_timeout_ms", 4000, 1000, 10000);
            heartbeatIntervalMs = getValidatedInt(props, "heartbeat_interval_ms", 2000, 1000, 5000);
            maxTrackedPlayers = getValidatedInt(props, "max_tracked_players", 50, 10, 500);
            cooldownBetweenTypingMs = getValidatedInt(props, "cooldown_between_typing_ms", 500, 100, 2000);
            enableTypingIndicator = Boolean.parseBoolean(props.getProperty("enable_typing_indicator", "true"));
            
            System.out.println("[IsTyping] Server config loaded successfully");
        } catch (IOException e) {
            System.err.println("[IsTyping] Failed to load config, using defaults: " + e.getMessage());
        }
    }
    
    private void createDefaultConfig(File configFile) {
        Properties props = new Properties();
        
        try {
            configFile.getParentFile().mkdirs();
            
            props.setProperty("typing_timeout_ms", "4000");
            props.setProperty("heartbeat_interval_ms", "2000");
            props.setProperty("max_tracked_players", "50");
            props.setProperty("cooldown_between_typing_ms", "500");
            props.setProperty("enable_typing_indicator", "true");
            
            try (FileOutputStream fos = new FileOutputStream(configFile)) {
                props.store(fos, 
                    "IsTyping Server Configuration\n" +
                    "# typing_timeout_ms: Milliseconds until a player is considered to have stopped typing (1000-10000)\n" +
                    "# heartbeat_interval_ms: Expected interval between client heartbeats (1000-5000)\n" +
                    "# max_tracked_players: Maximum number of players tracked simultaneously (10-500)\n" +
                    "# cooldown_between_typing_ms: Anti-spam cooldown between typing events (100-2000)\n" +
                    "# enable_typing_indicator: Master switch to enable/disable the mod (true/false)"
                );
            }
            
            System.out.println("[IsTyping] Created default server config at: " + configFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("[IsTyping] Failed to create default config: " + e.getMessage());
        }
    }
    
    /**
     * Validates integer value from properties within specified range
     */
    private int getValidatedInt(Properties props, String key, int defaultValue, int min, int max) {
        try {
            int value = Integer.parseInt(props.getProperty(key, String.valueOf(defaultValue)));
            if (value < min || value > max) {
                System.err.println("[IsTyping] Config value '" + key + "' out of range [" + min + "-" + max + "], using default: " + defaultValue);
                return defaultValue;
            }
            return value;
        } catch (NumberFormatException e) {
            System.err.println("[IsTyping] Invalid config value for '" + key + "', using default: " + defaultValue);
            return defaultValue;
        }
    }
    
    public int getTypingTimeoutMs() { return typingTimeoutMs; }
    public int getHeartbeatIntervalMs() { return heartbeatIntervalMs; }
    public int getMaxTrackedPlayers() { return maxTrackedPlayers; }
    public int getCooldownBetweenTypingMs() { return cooldownBetweenTypingMs; }
    public boolean isEnabled() { return enableTypingIndicator; }
}
