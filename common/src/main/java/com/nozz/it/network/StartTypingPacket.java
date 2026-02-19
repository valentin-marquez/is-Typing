package com.nozz.it.network;

import com.nozz.it.server.TypingTracker;
import dev.architectury.networking.NetworkManager.PacketContext;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public record StartTypingPacket() implements CustomPayload {

    public static final CustomPayload.Id<StartTypingPacket> ID = new CustomPayload.Id<>(Identifier.of("istyping", "start_typing"));
    public static final PacketCodec<RegistryByteBuf, StartTypingPacket> CODEC = PacketCodec.unit(new StartTypingPacket());

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receive(StartTypingPacket payload, PacketContext context) {
        context.queue(() -> {
            if (context.getPlayer() instanceof ServerPlayerEntity player) {
                TypingTracker.getInstance().startTyping(player);
            }
        });
    }
}
