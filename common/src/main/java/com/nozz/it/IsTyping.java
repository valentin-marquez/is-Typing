package com.nozz.it;

import com.nozz.it.config.ServerConfig;
import com.nozz.it.network.IsTypingNetworkManager;
import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Main class for the IsTyping mod
 * Mod that displays typing indicators in chat, similar to Discord
 */
public class IsTyping {
    public static final String MOD_ID = "istyping";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    
    /**
     * Common mod initialization (client and server)
     */
    public static void init() {
        LOGGER.info("Initializing IsTyping mod...");
        
        // Register network packets
        IsTypingNetworkManager.init();
        
        // Load server configuration (server only)
        if (Platform.getEnvironment() == Env.SERVER) {
            File configDir = Platform.getConfigFolder().toFile();
            ServerConfig.getInstance().load(configDir);
            LOGGER.info("Server configuration loaded");
        }
        
        LOGGER.info("IsTyping mod initialized successfully!");
    }
}
