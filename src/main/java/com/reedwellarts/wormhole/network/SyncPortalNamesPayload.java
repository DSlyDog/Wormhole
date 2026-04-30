package com.reedwellarts.wormhole.network;

import com.reedwellarts.wormhole.Wormhole;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.List;

public record SyncPortalNamesPayload(List<String> names) implements CustomPayload {

    public static final Id<SyncPortalNamesPayload> ID = new Id<>(Identifier.of(Wormhole.MOD_ID, "sync_portal_names"));

    public static final PacketCodec<RegistryByteBuf, SyncPortalNamesPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING.collect(PacketCodecs.toList()), SyncPortalNamesPayload::names,
            SyncPortalNamesPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId(){
        return ID;
    }
}
