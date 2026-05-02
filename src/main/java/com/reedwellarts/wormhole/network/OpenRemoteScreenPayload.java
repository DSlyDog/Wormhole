package com.reedwellarts.wormhole.network;

import com.reedwellarts.wormhole.Wormhole;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.List;

public record OpenRemoteScreenPayload(List<String> destinationNames) implements CustomPayload {

    public static final Id<OpenRemoteScreenPayload> ID =
            new Id<>(Identifier.of(Wormhole.MOD_ID, "open_remote_screen"));

    public static final PacketCodec<RegistryByteBuf, OpenRemoteScreenPayload> CODEC =
            PacketCodec.tuple(
                    PacketCodecs.STRING.collect(PacketCodecs.toList()), OpenRemoteScreenPayload::destinationNames,
                    OpenRemoteScreenPayload::new
            );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
