package com.reedwellarts.wormhole.network;

import com.reedwellarts.wormhole.util.TeleportUtil;
import com.reedwellarts.wormhole.util.WormholeLinkState;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class WormholeServerNetworking {

    public static void register(){
        PayloadTypeRegistry.playS2C().register(OpenControllerScreenPayload.ID, OpenControllerScreenPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(OpenNameScreenPayload.ID, OpenNameScreenPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(OpenRemoteScreenPayload.ID, OpenRemoteScreenPayload.CODEC);

        PayloadTypeRegistry.playC2S().register(SelectDestinationPayload.ID, SelectDestinationPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(SelectDestinationRemotePayload.ID, SelectDestinationRemotePayload.CODEC);
        PayloadTypeRegistry.playC2S().register(RegisterPortalNamePayload.ID, RegisterPortalNamePayload.CODEC);

        PayloadTypeRegistry.playS2C().register(SyncPortalNamesPayload.ID, SyncPortalNamesPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(SelectDestinationPayload.ID, (payload, context) ->
            TeleportUtil.wirePortal(context.player(), payload.controllerPos(), payload.destinationName())
        );

        ServerPlayNetworking.registerGlobalReceiver(SelectDestinationRemotePayload.ID, (payload, context) ->
                TeleportUtil.wireRemoteConnection(context.player(), payload.destinationName())
        );

        ServerPlayNetworking.registerGlobalReceiver(RegisterPortalNamePayload.ID, (payload, context) -> {
            String name = payload.name().trim();
            if (name.isEmpty()){
                return;
            }
            ServerWorld world = context.player().getEntityWorld().toServerWorld();
            WormholeLinkState state = WormholeLinkState.get(world);
            state.registerPortal(name, payload.basePos());

            sendSyncPortalNamesPayload(world);

            context.player().sendMessage(
                    Text.literal("Portal registered as '" + name + "'").formatted(Formatting.GREEN),
                    false
            );
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerWorld world = handler.getPlayer().getEntityWorld().toServerWorld();
            WormholeLinkState state = WormholeLinkState.get(world);
            SyncPortalNamesPayload payload = new SyncPortalNamesPayload(
                    List.copyOf(state.allPortals().keySet())
            );

            sender.sendPacket(payload);
        });
    }

    public static void sendSyncPortalNamesPayload(ServerWorld world){
        SyncPortalNamesPayload syncPayload = new SyncPortalNamesPayload(
                List.copyOf(WormholeLinkState.get(world).allPortals().keySet())
        );

        for (ServerPlayerEntity player : world.getPlayers()) {
            ServerPlayNetworking.send(player, syncPayload);
        }
    }
}
