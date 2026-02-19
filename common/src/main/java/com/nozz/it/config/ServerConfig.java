package com.nozz.it.config;

import java.io.File;

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
     * Loads configuration from config/istyping.toml
     */
    public void load(File configDir) {
        File configFile = new File(configDir, "istyping.toml");
        SimpleTomlConfig toml = new SimpleTomlConfig();
        
        if (configFile.exists()) {
            toml.load(configFile);
            
            // Check if Server section exists
            if (toml.get("Server", "enable_typing_indicator", null) == null) {
                updateToDefaults(toml);
                toml.save(configFile);
            }
            
            // Server
            enableTypingIndicator = toml.getBoolean("Server", "enable_typing_indicator", true);
            maxTrackedPlayers = toml.getInt("Server", "max_tracked_players", 50);
            typingTimeoutMs = toml.getInt("Server", "typing_timeout_ms", 4000);
            heartbeatIntervalMs = toml.getInt("Server", "heartbeat_interval_ms", 2000);
            cooldownBetweenTypingMs = toml.getInt("Server", "cooldown_between_typing_ms", 500);
            
            System.out.println("[IsTyping] Server config loaded (TOML)");
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
            System.out.println("[IsTyping] Created default server config (TOML) at: " + configFile.getAbsolutePath());
        } catch (Exception e) {
            System.err.println("[IsTyping] Failed to create default config: " + e.getMessage());
        }
    }

    private void updateToDefaults(SimpleTomlConfig toml) {
        toml.set("Server", "enable_typing_indicator", true);
        toml.set("Server", "max_tracked_players", 50);
        toml.set("Server", "typing_timeout_ms", 4000);
        toml.set("Server", "heartbeat_interval_ms", 2000);
        toml.set("Server", "cooldown_between_typing_ms", 500);
    }
    
    public int getTypingTimeoutMs() { return typingTimeoutMs; }
    public int getHeartbeatIntervalMs() { return heartbeatIntervalMs; }
    public int getMaxTrackedPlayers() { return maxTrackedPlayers; }
    public int getCooldownBetweenTypingMs() { return cooldownBetweenTypingMs; }
    public boolean isEnabled() { return enableTypingIndicator; }
}
