package com.nozz.it.mixin;

import com.nozz.it.client.ClientTypingHandler;
import net.minecraft.client.gui.screen.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin for ChatScreen to handle immediate stop events.
 * Continuous typing detection is handled by ClientTypingHandler.
 */
@Mixin(ChatScreen.class)
public class ChatScreenMixin {
    
    /**
     * Detects when a message is sent or chat is closed
     */
    @Inject(method = "removed", at = @At("HEAD"))
    private void onRemoved(CallbackInfo ci) {
        ClientTypingHandler.stopTyping();
    }
    
    /**
     * Detects when a message is sent (when pressing Enter)
     */
    @Inject(method = "sendMessage", at = @At("HEAD"))
    private void onSendMessage(String message, boolean addToHistory, CallbackInfo ci) {
        if (!message.startsWith("/")) {
            ClientTypingHandler.stopTyping();
        }
    }
}
