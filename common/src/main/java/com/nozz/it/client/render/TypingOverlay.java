package com.nozz.it.client.render;

import com.nozz.it.client.TypingStateManager;
import com.nozz.it.client.config.ClientConfig;
import com.nozz.it.client.i18n.TypingMessages;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.OrderedText;
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

        // Gather chat width limitation
        int rawChatWidth = client.inGameHud.getChatHud().getWidth();
        // Calculate max safe width to strictly prevent Hotbar overlap (Half screen minus padding)
        int maxSafeWidth = (client.getWindow().getScaledWidth() / 2) - 30;
        
        int chatWidth = Math.min(rawChatWidth, maxSafeWidth);
        // Fallback or padding
        if (chatWidth < 100) chatWidth = 320;

        Text text = message;
        List<OrderedText> lines = client.textRenderer.wrapLines(text, chatWidth);

        int textHeight = client.textRenderer.fontHeight;
        int totalHeight = textHeight * lines.size();
        
        // Push the starting Y higher depending on how many lines we have so the bottom line stays at the original chatY
        chatY = chatY - (totalHeight - textHeight);

        // Find the maximum width among the wrapped lines to draw a properly sized background box
        int maxLineWidth = 0;
        for (OrderedText line : lines) {
            int lineWidth = client.textRenderer.getWidth(line);
            if (lineWidth > maxLineWidth) {
                maxLineWidth = lineWidth;
            }
        }

        // Use configuration colors
        int bgAlpha = (int)(fadeAlpha * ((config.getBackgroundColor() >> 24) & 0xFF));
        int backgroundColor = (bgAlpha << 24) | (config.getBackgroundColor() & 0xFFFFFF);

        context.fill(
            chatX,
            chatY - 2,
            chatX + maxLineWidth + 4,
            chatY + totalHeight + 2,
            backgroundColor
        );

        // Draw text with fade
        int textAlpha = (int)(fadeAlpha * ((config.getTextColor() >> 24) & 0xFF));
        int color = (textAlpha << 24) | (config.getTextColor() & 0xFFFFFF);

        int currentY = chatY;
        for (OrderedText line : lines) {
            context.drawText(
                client.textRenderer,
                line,
                chatX + 2,
                currentY,
                color,
                false
            );
            currentY += textHeight;
        }
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
