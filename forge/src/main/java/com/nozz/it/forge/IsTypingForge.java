package com.nozz.it.forge;

import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import com.nozz.it.network.IsTypingNetworkManager;
import com.nozz.it.IsTyping;

@Mod(IsTyping.MOD_ID)
public final class IsTypingForge {
    public IsTypingForge() {
        // Submit our event bus to let Architectury API register our content on the right time.
        EventBuses.registerModEventBus(IsTyping.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());

        // Run our common setup.
        IsTyping.init();
        
        // Register client setup
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerOverlays);
    }
    
    private void onClientSetup(final FMLClientSetupEvent event) {
        com.nozz.it.client.config.ClientConfig.getInstance().load(dev.architectury.platform.Platform.getConfigFolder().toFile());
        IsTypingNetworkManager.initClient();
    }
    
    private void registerOverlays(final net.minecraftforge.client.event.RegisterGuiOverlaysEvent event) {
        event.registerAboveAll("istyping", (gui, guiGraphics, partialTick, width, height) -> {
            com.nozz.it.client.render.TypingOverlay.render(guiGraphics, partialTick);
        });
    }
}
