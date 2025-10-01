package com.nozz.it.client;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages the typing state of players on the client.
 * Similar to Discord's "X is typing..." system.
 */
public class TypingStateManager {
    private static final TypingStateManager INSTANCE = new TypingStateManager();
    
    private final Map<UUID, TypingState> typingPlayers = new ConcurrentHashMap<>();
    private static final long TIMEOUT = 5000; // 5 seconds timeout
    
    private TypingStateManager() {}
    
    public static TypingStateManager getInstance() {
        return INSTANCE;
    }
    
    public void setTyping(UUID playerUuid, String playerName, boolean typing) {
        if (typing) {
            typingPlayers.put(playerUuid, new TypingState(playerName, System.currentTimeMillis()));
        } else {
            typingPlayers.remove(playerUuid);
        }
    }
    
    public List<String> getTypingPlayers() {
        long currentTime = System.currentTimeMillis();
        List<String> players = new ArrayList<>();
        
        // Remove players with timeout
        typingPlayers.entrySet().removeIf(entry -> {
            if (currentTime - entry.getValue().timestamp > TIMEOUT) {
                return true;
            }
            return false;
        });
        
        for (TypingState state : typingPlayers.values()) {
            players.add(state.playerName);
        }
        
        return players;
    }
    
    /**
     * Clears all states (useful when changing servers)
     */
    public void clear() {
        typingPlayers.clear();
    }
    
    public boolean hasTypingPlayers() {
        return !getTypingPlayers().isEmpty();
    }
    
    private static class TypingState {
        final String playerName;
        final long timestamp;
        
        TypingState(String playerName, long timestamp) {
            this.playerName = playerName;
            this.timestamp = timestamp;
        }
    }
}
