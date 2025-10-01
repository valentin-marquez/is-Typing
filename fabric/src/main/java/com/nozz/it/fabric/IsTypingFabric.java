package com.nozz.it.fabric;

import com.nozz.it.IsTyping;
import net.fabricmc.api.ModInitializer;

/**
 * Entry point of the mod for Fabric (common for client and server)
 */
public class IsTypingFabric implements ModInitializer {
    
    @Override
    public void onInitialize() {
        IsTyping.init();
    }
}
