package com.nozz.it;

import com.nozz.it.config.ServerConfig;
import com.nozz.it.network.IsTypingNetworkManager;
import dev.architectury.platform.Platform;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;

import java.io.File;

/**
 * Main class for the IsTyping mod
 */
public class IsTyping {
    public static final String MOD_ID = "istyping";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    
    public static void init() {
        LOGGER.info("Initializing IsTyping mod...");
        IsTypingNetworkManager.init();
        dev.architectury.event.events.common.LifecycleEvent.SERVER_STARTING.register(server -> {
            File configDir = Platform.getConfigFolder().toFile();
            ServerConfig.getInstance().load(configDir);
        });

        dev.architectury.event.events.common.TickEvent.SERVER_POST.register(server -> {
            com.nozz.it.server.TypingTracker.getInstance().tick(server);
        });
        CommandRegistrationEvent.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("istyping")
                .requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.literal("simulate")
                    .then(CommandManager.argument("count", IntegerArgumentType.integer(1, 1000))
                        .executes(context -> {
                            int count = IntegerArgumentType.getInteger(context, "count");
                            com.nozz.it.server.TypingTracker.getInstance().simulateTypingLoad(context.getSource().getServer(), count);
                            context.getSource().sendFeedback(() -> Text.literal("Simulating " + count + " typing players for testing purposes!"), false);
                            return count;
                        })
                    )
                )
            );
        });
        
        LOGGER.info("IsTyping mod initialized successfully!");
    }
}
