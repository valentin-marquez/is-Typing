package com.nozz.it.fabric.client;

import com.nozz.it.client.config.ClientConfig;
import com.nozz.it.client.render.TypingOverlay;
import com.nozz.it.network.IsTypingNetworkManager;
import dev.architectury.platform.Platform;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

/**
 * Entry point of the mod for the Fabric client
 */
public class IsTypingFabricClient implements ClientModInitializer {
    
    @Override
    public void onInitializeClient() {
        // Load client configuration
        ClientConfig.getInstance().load(Platform.getConfigFolder().toFile());
        
        // Register client network packets (S2C)
        IsTypingNetworkManager.initClient();
        
        // Register the overlay renderer
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            TypingOverlay.render(drawContext, tickDelta);
        });
        
        System.out.println("[IsTyping] Fabric Client initialized");
    }
}