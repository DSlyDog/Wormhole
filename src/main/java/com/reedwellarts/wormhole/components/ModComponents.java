package com.reedwellarts.wormhole.components;

import com.reedwellarts.wormhole.Wormhole;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.function.UnaryOperator;

public class ModComponents {

    public static final ComponentType<BlockPos> PENDING_PORTAL_PS = register(
            "pending_portal_ps",
            builder -> builder
                    .codec(BlockPos.CODEC)
                    .packetCodec(BlockPos.PACKET_CODEC)
    );

    private static <T> ComponentType<T> register(String name, UnaryOperator<ComponentType.Builder<T>> builderOp){
        Identifier id = Identifier.of(Wormhole.MOD_ID, name);
        ComponentType<T> type = builderOp.apply(ComponentType.<T>builder()).build();
        return Registry.register(Registries.DATA_COMPONENT_TYPE, id, type);
    }

    public static void registerModComponents() {
        Wormhole.LOGGER.info("Registering data components for {}", Wormhole.MOD_ID);
    }
}
