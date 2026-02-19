package com.nozz.it.fabric.client;

import com.nozz.it.client.config.ClientConfig;
import com.nozz.it.client.render.TypingOverlay;
import dev.architectury.platform.Platform;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import com.nozz.it.client.ClientTypingHandler;

/**
 * Entry point of the mod for the Fabric client
 */
public class IsTypingFabricClient implements ClientModInitializer {
    
    @Override
    public void onInitializeClient() {
        // Load client configuration
        ClientConfig.getInstance().load(Platform.getConfigFolder().toFile());
        
        // Register client network packets (S2C)
        com.nozz.it.network.IsTypingNetworkManager.initClient();
        
        // Register the overlay renderer
        HudRenderCallback.EVENT.register((drawContext, tickCounter) -> {
            float tickDelta = tickCounter.getTickDelta(false);
            TypingOverlay.render(drawContext, tickDelta);
        });

        // Register client tick event for typing detection
        ClientTickEvents.END_CLIENT_TICK.register(ClientTypingHandler::tick);
        
        com.nozz.it.IsTyping.LOGGER.info("[IsTyping] Fabric Client initialized");
    }
}
