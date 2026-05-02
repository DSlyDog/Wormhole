package com.reedwellarts.wormhole.network;

import com.reedwellarts.wormhole.Wormhole;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record SelectDestinationRemotePayload(String destinationName) implements CustomPayload {

    public static final Id<SelectDestinationRemotePayload> ID =
            new Id<>(Identifier.of(Wormhole.MOD_ID, "select_destination_remote"));

    public static final PacketCodec<RegistryByteBuf, SelectDestinationRemotePayload> CODEC =
            PacketCodec.tuple(
                    PacketCodecs.STRING, SelectDestinationRemotePayload::destinationName,
                    SelectDestinationRemotePayload::new
            );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
