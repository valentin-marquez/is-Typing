package com.nozz.it.client.render;

import com.nozz.it.client.TypingStateManager;
import com.nozz.it.client.config.ClientConfig;
import com.nozz.it.client.i18n.TypingMessages;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import java.util.List;

/**
 * Renders the typing indicator integrated with Minecraft's chat.
 * Designed to look natural, as part of the vanilla chat system.
 * Lives in :common so it can be shared by all platform implementations.
 */
public class TypingOverlay {
    private static long animationStart = 0;
    private static float fadeAlpha = 0.0f;

    /**
     * Renders the typing indicator above the chat.
     * Call this from your platform's HUD render event.
     */
    public static void render(DrawContext context, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) return;

        ClientConfig config = ClientConfig.getInstance();
        List<String> typingPlayers = TypingStateManager.getInstance().getTypingPlayers();

        if (typingPlayers.isEmpty()) {
            fadeAlpha = Math.max(0, fadeAlpha - config.getFadeSpeed());
            if (fadeAlpha <= 0) return;
        } else {
            fadeAlpha = Math.min(1.0f, fadeAlpha + config.getFadeSpeed());
            if (animationStart == 0) {
                animationStart = System.currentTimeMillis();
            }
        }

        if (fadeAlpha <= 0) return;

        // Calculate position: just above the chat
        int screenHeight = client.getWindow().getScaledHeight();

        // Vanilla chat position (bottom left corner)
        int chatX = 2;
        int chatY = screenHeight - config.getOverlayYOffset();

        // Build the text
        net.minecraft.text.MutableText message = buildTypingMessage(typingPlayers, config);
        if (message == null) return;

        Text text = message;
        int textWidth = client.textRenderer.getWidth(text);
        int textHeight = client.textRenderer.fontHeight;

        // Use configuration colors
        int bgAlpha = (int)(fadeAlpha * ((config.getBackgroundColor() >> 24) & 0xFF));
        int backgroundColor = (bgAlpha << 24) | (config.getBackgroundColor() & 0xFFFFFF);

        context.fill(
            chatX,
            chatY - 2,
            chatX + textWidth + 4,
            chatY + textHeight + 2,
            backgroundColor
        );

        // Draw text with fade
        int textAlpha = (int)(fadeAlpha * ((config.getTextColor() >> 24) & 0xFF));
        int color = (textAlpha << 24) | (config.getTextColor() & 0xFFFFFF);

        context.drawText(
            client.textRenderer,
            text,
            chatX + 2,
            chatY,
            color,
            false
        );
    }

    /**
     * Builds the typing message with dot animation
     */
    private static net.minecraft.text.MutableText buildTypingMessage(List<String> players, ClientConfig config) {
        if (players.isEmpty()) return null;

        int displayCount = Math.min(players.size(), config.getMaxDisplayedPlayers());
        String[] displayPlayers = players.subList(0, displayCount).toArray(new String[0]);

        net.minecraft.text.MutableText message = TypingMessages.getTypingMessage(displayPlayers);

        if (config.isShowAnimation()) {
            message.append(Text.literal(getAnimatedDots(config.getAnimationSpeedMs())));
        }

        // Easter Egg: Herobrine is typing...
        if (config.getEasterEggChance() > 0 && Math.random() < config.getEasterEggChance()) {
            net.minecraft.text.MutableText egg = Text.translatable("istyping.easter_egg.herobrine");
            if (config.isShowAnimation()) {
                egg.append(Text.literal(getAnimatedDots(config.getAnimationSpeedMs())));
            }
            return egg;
        }

        return message;
    }

    /**
     * Generates animated dots for the typing indicator
     */
    private static String getAnimatedDots(int speedMs) {
        long elapsed = System.currentTimeMillis() - animationStart;
        int cycle = (int)((elapsed / speedMs) % 3);

        switch (cycle) {
            case 0: return ".";
            case 1: return "..";
            case 2: return "...";
            default: return ".";
        }
    }

    /**
     * Resets the animation state
     */
    public static void reset() {
        animationStart = 0;
        fadeAlpha = 0.0f;
    }
}
