package com.nozz.it.fabric.mixin;

import com.nozz.it.network.IsTypingNetworkManager;
import com.nozz.it.network.StartTypingPacket;
import com.nozz.it.network.StopTypingPacket;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin for ChatScreen that detects when the player is typing.
 * Similar to how Discord detects typing activity.
 */
@Mixin(ChatScreen.class)
public class ChatScreenMixin {
    
    @Shadow
    protected TextFieldWidget chatField;
    
    @Unique
    private boolean istyping$isTyping = false;
    
    @Unique
    private long istyping$lastHeartbeat = 0;
    
    @Unique
    private long istyping$lastActivity = 0; // Last time the user typed something
    
    @Unique
    private String istyping$lastText = "";
    
    @Unique
    private static final long DEFAULT_HEARTBEAT_INTERVAL = 2000; // Fallback in case of error
    
    @Unique
    private static final long ACTIVITY_TIMEOUT = 3000; // 3 seconds without typing before stopping
    
    /**
     * Detects when the chat screen is opened
     */
    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        istyping$isTyping = false;
        istyping$lastHeartbeat = 0;
        istyping$lastActivity = 0;
        istyping$lastText = "";
    }
    
    /**
     * Detects changes in chat text and sends typing status
     */
    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        if (chatField == null) return;
        
        String currentText = chatField.getText();
        long currentTime = System.currentTimeMillis();
        
        // Detect if text changed (user is actively typing)
        boolean textChanged = !currentText.equals(istyping$lastText);
        boolean isCommand = currentText.startsWith("/");
        boolean hasContent = !currentText.trim().isEmpty();
        
        // Update last activity if text changed and is not a command
        if (textChanged && !isCommand) {
            istyping$lastActivity = currentTime;
        }
        
        // Determine if should be "typing"
        boolean shouldBeTyping = false;
        
        if (!isCommand) { // Only if not a command
            if (hasContent) {
                // If there's content, check if there has been recent activity
                long timeSinceActivity = currentTime - istyping$lastActivity;
                shouldBeTyping = timeSinceActivity <= ACTIVITY_TIMEOUT;
            }
        }
        
        // Handle the typing state
        if (shouldBeTyping) {
            // Send initial heartbeat or renew
            long heartbeatInterval = DEFAULT_HEARTBEAT_INTERVAL; // Use default value on client
            if (!istyping$isTyping || currentTime - istyping$lastHeartbeat >= heartbeatInterval) {
                IsTypingNetworkManager.sendToServer(new StartTypingPacket());
                istyping$lastHeartbeat = currentTime;
                istyping$isTyping = true;
            }
        } else if (istyping$isTyping) {
            // If stopped typing or is a command, stop
            istyping$stopTyping();
        }
        
        istyping$lastText = currentText;
    }
    
    /**
     * Detects when a message is sent or chat is closed
     */
    @Inject(method = "removed", at = @At("HEAD"))
    private void onRemoved(CallbackInfo ci) {
        istyping$stopTyping();
    }
    
    /**
     * Detects when a message is sent (when pressing Enter)
     */
    @Inject(method = "sendMessage", at = @At("HEAD"))
    private void onSendMessage(String message, boolean addToHistory, CallbackInfoReturnable<Boolean> cir) {
        if (!message.startsWith("/")) {
            istyping$stopTyping();
        }
    }
    
    @Unique
    private void istyping$stopTyping() {
        if (istyping$isTyping) {
            IsTypingNetworkManager.sendToServer(new StopTypingPacket());
            istyping$isTyping = false;
            istyping$lastHeartbeat = 0;
        }
    }
}
