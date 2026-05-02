package com.reedwellarts.wormhole;

import com.reedwellarts.wormhole.command.WormholeCommands;
import com.reedwellarts.wormhole.components.ModComponents;
import com.reedwellarts.wormhole.registrar.BlockRegistrar;
import com.reedwellarts.wormhole.registrar.ItemRegistrar;
import com.reedwellarts.wormhole.network.WormholeServerNetworking;
import com.reedwellarts.wormhole.util.WormholeLinkState;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Wormhole implements ModInitializer {

    public static final String MOD_ID = "wormhole";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Wormhole mod is initializing");

        ItemRegistrar.registerModItems();
        BlockRegistrar.registerModBlocks();
        ModComponents.registerModComponents();
        WormholeCommands.registerCommands();
        WormholeServerNetworking.register();

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            for (ServerWorld world : server.getWorlds()){
                LOGGER.info("Removing portals in {}", world.toString());
                WormholeLinkState state = WormholeLinkState.get(world);
                for (BlockPos base : state.allPortals().values()){
                    WormholeLinkState.removePortalAbove(world, base);
                    LOGGER.info("Removing portal {}", base.toString());
                }
            }
        });

        LOGGER.info("Wormhole mod loaded");
    }
}
