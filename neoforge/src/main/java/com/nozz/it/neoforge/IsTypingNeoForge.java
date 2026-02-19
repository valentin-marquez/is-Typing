package com.nozz.it.neoforge;

import com.nozz.it.IsTyping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.minecraft.util.Identifier;

@Mod(IsTyping.MOD_ID)
public final class IsTypingNeoForge {
    public IsTypingNeoForge(IEventBus modBus) {
        IsTyping.init();

        if (FMLEnvironment.dist == Dist.CLIENT) {
            modBus.addListener(this::onClientSetup);
            modBus.addListener(this::registerLayers);
            net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(this::onClientTick);
        }
    }

    private void onClientSetup(final net.neoforged.fml.event.lifecycle.FMLClientSetupEvent event) {
        com.nozz.it.network.IsTypingNetworkManager.initClient();
    }

    private void registerLayers(final RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.CHAT, Identifier.of(IsTyping.MOD_ID, "typing_indicator"), (guiGraphics, partialTick) -> {
            float delta = partialTick.getTickDelta(false); 
            com.nozz.it.client.render.TypingOverlay.render(guiGraphics, delta);
        });
    }

    private void onClientTick(net.neoforged.neoforge.client.event.ClientTickEvent.Post event) {
        com.nozz.it.client.ClientTypingHandler.tick(net.minecraft.client.MinecraftClient.getInstance());
    }
}
