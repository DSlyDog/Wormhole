package com.reedwellarts.wormhole.network;

import com.reedwellarts.wormhole.items.blocks.WormholeControllerBlockEntity;
import com.reedwellarts.wormhole.portal.WormholeLinkState;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.ServerNetworkIo;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.Optional;

public class WormholeServerNetworking {

    public static void register(){
        PayloadTypeRegistry.playS2C().register(OpenControllerScreenPayload.ID, OpenControllerScreenPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(OpenNameScreenPayload.ID, OpenNameScreenPayload.CODEC);

        PayloadTypeRegistry.playC2S().register(SelectDestinationPayload.ID, SelectDestinationPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(RegisterPortalNamePayload.ID, RegisterPortalNamePayload.CODEC);

        PayloadTypeRegistry.playS2C().register(SyncPortalNamesPayload.ID, SyncPortalNamesPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(SelectDestinationPayload.ID, (payload, context) -> {
            ServerPlayerEntity player = context.player();
            ServerWorld world = player.getEntityWorld().toServerWorld();

            BlockEntity blockEntity = world.getBlockEntity(payload.controllerPos());
            if (!(blockEntity instanceof WormholeControllerBlockEntity controllerBE)) {
                return;
            }

            BlockPos sourceBase = controllerBE.getLinkedBase();
            if (sourceBase == null){
                return;
            }

            WormholeLinkState linkState = WormholeLinkState.get(world);
            Optional<BlockPos> destBaseOpt = linkState.getPortalPos(payload.destinationName());
            if (destBaseOpt.isEmpty()){
                player.sendMessage(
                        Text.literal("Portal '" + payload.destinationName() + "' no longer exists")
                                .formatted(Formatting.RED),
                        false
                );
                return;
            }

            BlockPos destBase = destBaseOpt.get();
            boolean opened = linkState.activatePortal(world, sourceBase, destBase);
            if (!opened){
                player.sendMessage(
                        Text.literal("Could not open portal. Is the frame intact?")
                                .formatted(Formatting.RED),
                        false
                );
                return;
            }

            player.sendMessage(
                    Text.literal("Portal opened → " + payload.destinationName())
                            .formatted(Formatting.GREEN),
                    false
            );
        });

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
