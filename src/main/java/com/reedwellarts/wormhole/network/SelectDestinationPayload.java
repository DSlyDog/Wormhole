package com.reedwellarts.wormhole.network;

import com.reedwellarts.wormhole.Wormhole;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record SelectDestinationPayload(BlockPos controllerPos, String destinationName)
    implements CustomPayload {

    public static final Id<SelectDestinationPayload> ID =
            new Id<>(Identifier.of(Wormhole.MOD_ID, "select_destination"));

    public static final PacketCodec<RegistryByteBuf, SelectDestinationPayload> CODEC =
            PacketCodec.tuple(
                    BlockPos.PACKET_CODEC, SelectDestinationPayload::controllerPos,
                    PacketCodecs.STRING, SelectDestinationPayload::destinationName,
                    SelectDestinationPayload::new
            );

    @Override
    public Id<? extends CustomPayload> getId(){
        return ID;
    }
}
