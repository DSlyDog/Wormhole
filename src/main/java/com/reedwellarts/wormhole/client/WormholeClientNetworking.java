package com.reedwellarts.wormhole.client;

import com.reedwellarts.wormhole.Wormhole;
import com.reedwellarts.wormhole.network.*;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.math.BlockPos;

public class WormholeClientNetworking {

    public static void register() {
        Wormhole.LOGGER.info("Registering wormhole client");

        ClientPlayNetworking.registerGlobalReceiver(OpenControllerScreenPayload.ID, (payload, context) ->

                context.client().execute(() ->
                        context.client().setScreen(
                                new ControllerScreen(payload.controllerPos(), payload.destinationNames())
                        )
                )
        );

        ClientPlayNetworking.registerGlobalReceiver(OpenNameScreenPayload.ID, (payload, context) ->
                context.client().execute(() ->
                        context.client().setScreen(new NamePortalScreen(payload.basePos()))
                ));

        ClientPlayNetworking.registerGlobalReceiver(SyncPortalNamesPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                PortalNames.updateNames(payload.names());
            });
        });
    }

    public static void sendSelectedDestination(BlockPos controllerPos, String destinationName){
        ClientPlayNetworking.send(new SelectDestinationPayload(controllerPos, destinationName));
    }

    public static void registerPortalName(BlockPos basePos, String name) {
        ClientPlayNetworking.send(new RegisterPortalNamePayload(basePos, name));
    }
}
