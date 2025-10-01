package com.nozz.it.network;

import dev.architectury.networking.NetworkManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

/**
 * Network manager for the IsTyping mod.
 * Handles registration and sending of packets between client and server.
 */
public class IsTypingNetworkManager {
    
    // Packet identifiers
    public static final Identifier START_TYPING = new Identifier("istyping", "start_typing");
    public static final Identifier STOP_TYPING = new Identifier("istyping", "stop_typing");
    public static final Identifier PLAYER_TYPING = new Identifier("istyping", "player_typing");
    
    /**
     * Registers C2S packets (Client to Server)
     * This method should be called on both client and server.
     * In Architectury 9.2.14, C2S receivers are registered on both sides.
     */
    public static void init() {
        // Register C2S packets (Client to Server)
        // These receivers will run on the server when they receive packets from clients
        NetworkManager.registerReceiver(
            NetworkManager.c2s(), 
            START_TYPING, 
            (buf, context) -> StartTypingPacket.receive(buf, context)
        );
        
        NetworkManager.registerReceiver(
            NetworkManager.c2s(), 
            STOP_TYPING, 
            (buf, context) -> StopTypingPacket.receive(buf, context)
        );
    }
    
    /**
     * Registers S2C packets (client only)
     */
    public static void initClient() {
        NetworkManager.registerReceiver(
            NetworkManager.s2c(), 
            PLAYER_TYPING, 
            (buf, context) -> PlayerTypingPacket.receive(buf, context)
        );
    }
    
    /**
     * Sends a packet from client to server
     */
    public static void sendToServer(Object packet) {
        if (packet instanceof StartTypingPacket) {
            NetworkManager.sendToServer(START_TYPING, ((StartTypingPacket) packet).toBuffer());
        } else if (packet instanceof StopTypingPacket) {
            NetworkManager.sendToServer(STOP_TYPING, ((StopTypingPacket) packet).toBuffer());
        }
    }
    
    /**
     * Sends a packet from server to a specific client
     */
    public static void sendToPlayer(ServerPlayerEntity player, Object packet) {
        if (packet instanceof PlayerTypingPacket) {
            NetworkManager.sendToPlayer(player, PLAYER_TYPING, ((PlayerTypingPacket) packet).toBuffer());
        }
    }
    
    /**
     * Sends a packet from server to all clients
     */
    public static void sendToAllClients(MinecraftServer server, Object packet) {
        if (packet instanceof PlayerTypingPacket) {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                sendToPlayer(player, packet);
            }
        }
    }
}