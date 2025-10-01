package com.nozz.it.network;

import com.nozz.it.server.TypingTracker;
import dev.architectury.networking.NetworkManager.PacketContext;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import io.netty.buffer.Unpooled;

/**
 * C2S packet sent when a player stops typing.
 */
public class StopTypingPacket {
    
    public StopTypingPacket() {}
    
    /**
     * Constructs the packet from a buffer
     */
    public StopTypingPacket(PacketByteBuf buf) {
        // This packet has no additional data
    }
    
    /**
     * Writes the packet to a buffer
     */
    public PacketByteBuf toBuffer() {
        return new PacketByteBuf(Unpooled.buffer());
    }
    
    /**
     * Handles packet reception on the server
     */
    public static void receive(PacketByteBuf buf, PacketContext context) {
        context.queue(() -> {
            if (context.getPlayer() instanceof ServerPlayerEntity player) {
                TypingTracker.getInstance().stopTyping(player);
            }
        });
    }
}
