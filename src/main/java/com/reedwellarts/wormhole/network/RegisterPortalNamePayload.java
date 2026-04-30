package com.reedwellarts.wormhole.network;

import com.reedwellarts.wormhole.Wormhole;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record RegisterPortalNamePayload(BlockPos basePos, String name) implements CustomPayload {

    public static final Id<RegisterPortalNamePayload> ID =
            new Id<>(Identifier.of(Wormhole.MOD_ID, "register_portal_name"));

    public static final PacketCodec<RegistryByteBuf, RegisterPortalNamePayload> CODEC =
            PacketCodec.tuple(
                    BlockPos.PACKET_CODEC, RegisterPortalNamePayload::basePos,
                    PacketCodecs.STRING, RegisterPortalNamePayload::name,
                    RegisterPortalNamePayload::new
            );

    @Override
    public Id<? extends CustomPayload> getId(){
        return ID;
    }
}
