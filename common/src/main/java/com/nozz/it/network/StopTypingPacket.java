package com.nozz.it.network;

import com.nozz.it.server.TypingTracker;
import dev.architectury.networking.NetworkManager.PacketContext;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public record StopTypingPacket() implements CustomPayload {

    public static final CustomPayload.Id<StopTypingPacket> ID = new CustomPayload.Id<>(Identifier.of("istyping", "stop_typing"));
    public static final PacketCodec<RegistryByteBuf, StopTypingPacket> CODEC = PacketCodec.unit(new StopTypingPacket());

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receive(StopTypingPacket payload, PacketContext context) {
        context.queue(() -> {
            if (context.getPlayer() instanceof ServerPlayerEntity player) {
                TypingTracker.getInstance().stopTyping(player);
            }
        });
    }
}
