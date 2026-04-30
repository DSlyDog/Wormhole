package com.reedwellarts.wormhole.network;

import com.reedwellarts.wormhole.Wormhole;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record OpenNameScreenPayload(BlockPos basePos) implements CustomPayload {

    public static final Id<OpenNameScreenPayload> ID =
            new Id<>(Identifier.of(Wormhole.MOD_ID, "open_name_screen"));

    public static final PacketCodec<RegistryByteBuf, OpenNameScreenPayload> CODEC =
            PacketCodec.tuple(
                    BlockPos.PACKET_CODEC, OpenNameScreenPayload::basePos,
                    OpenNameScreenPayload::new
            );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
