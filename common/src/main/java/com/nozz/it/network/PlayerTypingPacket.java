package com.nozz.it.network;

import com.nozz.it.client.TypingStateManager;
import dev.architectury.networking.NetworkManager.PacketContext;
import net.minecraft.network.PacketByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;

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
     * Handles packet reception on the client.
     * Anti-Vanish filter: if the sender's UUID is not in the visible player list
     * (e.g. an admin in vanish), the packet is silently ignored.
     */
    public static void receive(PacketByteBuf buf, PacketContext context) {
        UUID uuid = buf.readUuid();
        String name = buf.readString();
        boolean typing = buf.readBoolean();

        context.queue(() -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null) return;

            // Ignore our own typing state
            if (uuid.equals(client.player.getUuid())) return;

            // Anti-Vanish filter: only process if the player is visible in the tab list
            ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();
            if (networkHandler != null) {
                PlayerListEntry entry = networkHandler.getPlayerListEntry(uuid);
                if (entry == null) {
                    // Player is not in the visible player list (likely in vanish) — ignore
                    return;
                }
            }

            TypingStateManager.getInstance().setTyping(uuid, name, typing);
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
