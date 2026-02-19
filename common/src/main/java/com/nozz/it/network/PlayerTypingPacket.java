package com.nozz.it.network;

import dev.architectury.networking.NetworkManager.PacketContext;
import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import java.util.UUID;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;

public record PlayerTypingPacket(UUID playerUuid, String playerName, boolean isTyping) implements CustomPayload {

    public static final CustomPayload.Id<PlayerTypingPacket> ID = new CustomPayload.Id<>(Identifier.of("istyping", "player_typing"));

    public static final PacketCodec<RegistryByteBuf, PlayerTypingPacket> CODEC = PacketCodec.tuple(
        Uuids.PACKET_CODEC, PlayerTypingPacket::playerUuid,
        PacketCodecs.STRING, PlayerTypingPacket::playerName,
        PacketCodecs.BOOL, PlayerTypingPacket::isTyping,
        PlayerTypingPacket::new
    );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receive(PlayerTypingPacket payload, PacketContext context) {
        context.queue(() -> {
            EnvExecutor.runInEnv(Env.CLIENT, () -> () -> 
                com.nozz.it.client.ClientTypingHandler.handlePacket(payload)
            );
        });
    }
}
