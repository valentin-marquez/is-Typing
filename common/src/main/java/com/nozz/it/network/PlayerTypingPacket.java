package com.nozz.it.network;

import com.nozz.it.client.TypingStateManager;
import dev.architectury.networking.NetworkManager.PacketContext;
import net.minecraft.network.PacketByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.MinecraftClient;

import java.util.UUID;

/**
 * S2C packet that broadcasts a player's typing status.
 */
public class PlayerTypingPacket {
    private final UUID playerUuid;
    private final String playerName;
    private final boolean isTyping;
    
    public PlayerTypingPacket(UUID playerUuid, String playerName, boolean isTyping) {
        this.playerUuid = playerUuid;
        this.playerName = playerName;
        this.isTyping = isTyping;
    }
    
    /**
     * Constructs the packet from a buffer
     */
    public PlayerTypingPacket(PacketByteBuf buf) {
        this.playerUuid = buf.readUuid();
        this.playerName = buf.readString();
        this.isTyping = buf.readBoolean();
    }
    
    /**
     * Writes the packet to a buffer
     */
    public PacketByteBuf toBuffer() {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeUuid(playerUuid);
        buf.writeString(playerName);
        buf.writeBoolean(isTyping);
        return buf;
    }
    
    /**
     * Handles packet reception on the client
     */
    public static void receive(PacketByteBuf buf, PacketContext context) {
        UUID uuid = buf.readUuid();
        String name = buf.readString();
        boolean typing = buf.readBoolean();
        
        context.queue(() -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != null && !uuid.equals(client.player.getUuid())) {
                TypingStateManager.getInstance().setTyping(uuid, name, typing);
            }
        });
    }
    
    public UUID getPlayerUuid() {
        return playerUuid;
    }
    
    public String getPlayerName() {
        return playerName;
    }
    
    public boolean isTyping() {
        return isTyping;
    }
}
