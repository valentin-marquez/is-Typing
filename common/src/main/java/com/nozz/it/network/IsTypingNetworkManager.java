package com.nozz.it.network;

import dev.architectury.networking.NetworkManager;
import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Handles registration and sending of packets between client and server.
 */
public class IsTypingNetworkManager {
    
    public static void init() {
        NetworkManager.registerReceiver(
            NetworkManager.Side.C2S,
            StartTypingPacket.ID,
            StartTypingPacket.CODEC,
            StartTypingPacket::receive
        );
        NetworkManager.registerReceiver(
            NetworkManager.Side.C2S,
            StopTypingPacket.ID,
            StopTypingPacket.CODEC,
            StopTypingPacket::receive
        );
        // Register S2C payload type so the server has the PacketCodec available to *encode* S2C packets.
        // We only do this on the physical SERVER. The client registers its receiver in initClient().
        EnvExecutor.runInEnv(Env.SERVER, () -> () ->
            NetworkManager.registerS2CPayloadType(
                PlayerTypingPacket.ID,
                PlayerTypingPacket.CODEC
            )
        );
    }
    
    public static void initClient() {
        // Register PlayerTypingPacket (S2C) on client side only
        NetworkManager.registerReceiver(
            NetworkManager.Side.S2C,
            PlayerTypingPacket.ID,
            PlayerTypingPacket.CODEC,
            PlayerTypingPacket::receive
        );
    }
    
    public static void sendToServer(CustomPayload packet) {
        NetworkManager.sendToServer(packet);
    }
    
    public static void sendToPlayer(ServerPlayerEntity player, CustomPayload packet) {
        NetworkManager.sendToPlayer(player, packet);
    }
    
    public static void sendToAllClients(MinecraftServer server, CustomPayload packet) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            sendToPlayer(player, packet);
        }
    }
}