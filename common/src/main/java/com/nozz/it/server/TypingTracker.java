package com.nozz.it.server;

import com.nozz.it.config.ServerConfig;
import com.nozz.it.network.IsTypingNetworkManager;
import com.nozz.it.network.PlayerTypingPacket;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracks the typing state of players on the server.
 * Handles timeouts and broadcasts to all clients.
 */
public class TypingTracker {
    private static final TypingTracker INSTANCE = new TypingTracker();
    
    private final Map<UUID, PlayerTypingState> typingPlayers = new ConcurrentHashMap<>();
    private final Map<UUID, Long> lastTypingEvent = new ConcurrentHashMap<>(); // Anti-spam
    
    private int tickCounter = 0;
    
    
    public static TypingTracker getInstance() {
        return INSTANCE;
    }
    
    /**
     * Marks a player as typing and broadcasts it
     */
    public void startTyping(ServerPlayerEntity player) {
        ServerConfig config = ServerConfig.getInstance();
        if (!config.isEnabled()) return;
        
        UUID uuid = player.getUuid();
        String name = player.getName().getString();
        long currentTime = System.currentTimeMillis();
        
        // Anti-spam: check cooldown
        Long lastEvent = lastTypingEvent.get(uuid);
        if (lastEvent != null && (currentTime - lastEvent) < config.getCooldownBetweenTypingMs()) {
            return; // Too soon, ignore
        }
        
        // Check tracked players limit
        if (typingPlayers.size() >= config.getMaxTrackedPlayers() && !typingPlayers.containsKey(uuid)) {
            return; // Limit reached
        }
        
        boolean wasTyping = typingPlayers.containsKey(uuid);
        typingPlayers.put(uuid, new PlayerTypingState(name, currentTime));
        lastTypingEvent.put(uuid, currentTime);
        
        // Only broadcast if it's the first time or if there was a timeout
        if (!wasTyping) {
            broadcastTypingState(player.getServer(), uuid, name, true);
        }
    }

    public void tick(MinecraftServer server) {
        if (++tickCounter >= 20) {
            tickCounter = 0;
            checkTimeouts(server);   
        }
    }
    
    /**
     * Marks a player as stopped typing
     */
    public void stopTyping(ServerPlayerEntity player) {
        UUID uuid = player.getUuid();
        PlayerTypingState state = typingPlayers.remove(uuid);
        
        if (state != null) {
            broadcastTypingState(player.getServer(), uuid, player.getName().getString(), false);
        }
    }
    
    /**
     * Gets all players that are currently typing
     */
    public Set<UUID> getTypingPlayers() {
        return new HashSet<>(typingPlayers.keySet());
    }
    
    /**
     * Clears a player's state (used when they disconnect)
     */
    public void removePlayer(UUID playerUuid) {
        typingPlayers.remove(playerUuid);
    }
    
    /**
     * Broadcasts typing state to all clients
     */
    private void broadcastTypingState(net.minecraft.server.MinecraftServer server, UUID uuid, String name, boolean typing) {
        if (server != null) {
            PlayerTypingPacket packet = new PlayerTypingPacket(uuid, name, typing);
            IsTypingNetworkManager.sendToAllClients(server, packet);
        }
    }
    
    /**
     * Checks and removes players with timeout
     */
    private void checkTimeouts(MinecraftServer server) {
        ServerConfig config = ServerConfig.getInstance();
        long currentTime = System.currentTimeMillis();
        long timeout = config.getTypingTimeoutMs();
        List<UUID> toRemove = new ArrayList<>();
        
        for (Map.Entry<UUID, PlayerTypingState> entry : typingPlayers.entrySet()) {
            if (currentTime - entry.getValue().lastUpdate > timeout) {
                toRemove.add(entry.getKey());
            }
        }
        
        // Remove and broadcast
        for (UUID uuid : toRemove) {
            PlayerTypingState state = typingPlayers.remove(uuid);
            if (state != null) {
                broadcastTypingState(
                    server, 
                    uuid, 
                    state.playerName, 
                    false
                );
            }
        }
    }
    
    /**
     * Simulates typing load by injecting fake players
     */
    public void simulateTypingLoad(MinecraftServer server, int count) {
        long currentTime = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            String botName = "Bot" + i;
            UUID botUuid = UUID.nameUUIDFromBytes(botName.getBytes());
            
            typingPlayers.put(botUuid, new PlayerTypingState(botName, currentTime));
            broadcastTypingState(server, botUuid, botName, true);
        }
    }
    
    /**
     * Stops the timeout checker
     */
    public void shutdown() {

        typingPlayers.clear();
    }
    
    private static class PlayerTypingState {
        final String playerName;
        final long lastUpdate;
        
        PlayerTypingState(String playerName, long lastUpdate) {
            this.playerName = playerName;
            this.lastUpdate = lastUpdate;
        }
    }
}
