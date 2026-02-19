package com.nozz.it.client;

import com.nozz.it.mixin.ChatScreenAccessor;
import com.nozz.it.network.IsTypingNetworkManager;
import com.nozz.it.network.StartTypingPacket;
import com.nozz.it.network.StopTypingPacket;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;

public class ClientTypingHandler {
    private static boolean isTyping = false;
    private static long lastHeartbeat = 0;
    private static long lastActivity = 0;
    private static String lastText = "";
    
    private static final long HEARTBEAT_INTERVAL = 1000;
    private static final long ACTIVITY_TIMEOUT = 1500;

    public static void tick(MinecraftClient client) {
        if (client.player == null) return;

        if (client.currentScreen instanceof ChatScreen chatScreen) {
            handleChatScreen(chatScreen);
        } else if (isTyping) {
            stopTyping();
        }
    }

    private static void handleChatScreen(ChatScreen chatScreen) {
        TextFieldWidget chatField = ((ChatScreenAccessor) chatScreen).istyping$getChatField();
        if (chatField == null) return;

        String currentText = chatField.getText();
        long currentTime = System.currentTimeMillis();
        
        // Detect activity
        boolean textChanged = !currentText.equals(lastText);
        boolean isCommand = currentText.startsWith("/");
        boolean hasContent = !currentText.trim().isEmpty();

        if (textChanged && !isCommand) {
            lastActivity = currentTime;
        }
        
        boolean shouldBeTyping = !isCommand && hasContent && (currentTime - lastActivity <= ACTIVITY_TIMEOUT);

        if (shouldBeTyping) {
            if (!isTyping || currentTime - lastHeartbeat >= HEARTBEAT_INTERVAL) {
                IsTypingNetworkManager.sendToServer(new StartTypingPacket());
                lastHeartbeat = currentTime;
                isTyping = true;
            }
        } else if (isTyping) {
             // We are in chat screen but criteria for "typing" not met (e.g. cleared text or timeout)
             stopTyping();
        }

        lastText = currentText;
    }

    public static void stopTyping() {
        if (isTyping) {
            IsTypingNetworkManager.sendToServer(new StopTypingPacket());
            isTyping = false;
            lastHeartbeat = 0;
        }
    }

    public static void handlePacket(com.nozz.it.network.PlayerTypingPacket payload) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        // Ignore our own typing state
        if (payload.playerUuid().equals(client.player.getUuid())) return;

        // Anti-Vanish filter: only process if the player is visible in the tab list
        net.minecraft.client.network.ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();
        if (networkHandler != null) {
            net.minecraft.client.network.PlayerListEntry entry = networkHandler.getPlayerListEntry(payload.playerUuid());
            if (entry == null && !payload.playerUuid().equals(java.util.UUID.nameUUIDFromBytes(payload.playerName().getBytes()))) {
                // Player is not in the visible player list (likely in vanish) and not a bot — ignore
                return;
            }
        }

        TypingStateManager.getInstance().setTyping(payload.playerUuid(), payload.playerName(), payload.isTyping());
    }
}
