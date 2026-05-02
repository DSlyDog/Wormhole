package com.reedwellarts.wormhole.registrar;

import com.reedwellarts.wormhole.Wormhole;
import com.reedwellarts.wormhole.screen.WormholeChargerScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class ScreenHandlerRegistrar {

    public static ScreenHandlerType<WormholeChargerScreenHandler>  WORMHOLE_CHARGER_HANDLER = Registry.register(
            Registries.SCREEN_HANDLER,
            Identifier.of(Wormhole.MOD_ID, "wormhole_charger_handler"),
            new ExtendedScreenHandlerType<>(
                    (syncId, playerInventory, pos) ->
                        new WormholeChargerScreenHandler(syncId, playerInventory),
                    BlockPos.PACKET_CODEC
            )
    );

    public static void registerScreenHandlers() {
        // load trigger
    }
}
