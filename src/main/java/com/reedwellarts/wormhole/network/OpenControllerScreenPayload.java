package com.reedwellarts.wormhole.network;

import com.reedwellarts.wormhole.Wormhole;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public record OpenControllerScreenPayload(BlockPos controllerPos, List<String> destinationNames)
    implements CustomPayload {

    public static final Id<OpenControllerScreenPayload> ID =
            new Id<>(Identifier.of(Wormhole.MOD_ID, "open_controller_screen"));

    public static final PacketCodec<RegistryByteBuf, OpenControllerScreenPayload> CODEC =
            PacketCodec.tuple(
                    BlockPos.PACKET_CODEC, OpenControllerScreenPayload::controllerPos,
                    PacketCodecs.STRING.collect(PacketCodecs.toList()), OpenControllerScreenPayload::destinationNames,
                    OpenControllerScreenPayload::new
            );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
