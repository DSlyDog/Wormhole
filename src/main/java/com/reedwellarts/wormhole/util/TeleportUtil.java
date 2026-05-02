package com.reedwellarts.wormhole.util;

import com.reedwellarts.wormhole.Wormhole;
import com.reedwellarts.wormhole.items.blocks.BlockEntity.WormholeControllerBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;
import java.util.Set;

public class TeleportUtil {

    public static void teleportToBase(ServerWorld world, Entity entity, BlockPos destBase){
        BlockState destState = world.getBlockState(destBase);
        Direction facing = destState.contains(Properties.FACING)
                ? destState.get(Properties.FACING)
                : Direction.NORTH;

        BlockPos exitPos;
        if (world.getBlockState(destBase.offset(facing)).isAir()) {
            exitPos = destBase.offset(facing);
        }else{
            exitPos = destBase.offset(facing).up();
        }

        Vec3d center = Vec3d.ofBottomCenter(exitPos);

        float yaw = yawFromDirection(facing);
        entity.teleport(world, center.x, center.y, center.z, Set.of(), yaw, entity.getPitch(), false);
    }

    public static float yawFromDirection(Direction direction) {
        return switch (direction) {
            case WEST  -> 90.0f;
            case NORTH -> 180.0f;
            case EAST  -> 270.0f;
            default    -> 0.0f;
        };
    }

    public static void wirePortal(ServerPlayerEntity player, BlockPos controllerPos, String destinationName){
        ServerWorld world = player.getEntityWorld().toServerWorld();

        BlockEntity blockEntity = world.getBlockEntity(controllerPos);
        if (!(blockEntity instanceof WormholeControllerBlockEntity controllerBE)) {
            return;
        }

        BlockPos sourceBase = controllerBE.getLinkedBase();
        if (sourceBase == null){
            return;
        }

        WormholeLinkState linkState = WormholeLinkState.get(world);
        Optional<BlockPos> destBaseOpt = linkState.getPortalPos(destinationName);
        if (destBaseOpt.isEmpty()){
            player.sendMessage(
                    Text.literal("Portal '" + destinationName + "' no longer exists")
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
                Text.literal("Portal opened → " + destinationName)
                        .formatted(Formatting.GREEN),
                false
        );
    }

    public static void wireRemoteConnection(ServerPlayerEntity player, String destinationName){
        WormholeLinkState linkState = WormholeLinkState.get(player.getEntityWorld().toServerWorld());
        Optional<BlockPos> destBaseOpt = linkState.getPortalPos(destinationName);
        ItemStack stack = player.getMainHandStack();

        Wormhole.LOGGER.info("damage {}, max damage {}", stack.getDamage(), stack.getMaxDamage());
        if (stack.getDamage() < stack.getMaxDamage() - 1){
            Wormhole.LOGGER.info("Damaging item");
            ItemConvertible convertible = stack.getItem();
            stack.damage(1, convertible, player, player.getActiveHand() == Hand.MAIN_HAND
                    ? EquipmentSlot.MAINHAND
                    : EquipmentSlot.OFFHAND);
        } else if (stack.getDamage() == stack.getMaxDamage() - 1){
            stack.setDamage(stack.getMaxDamage());
        }

        if (destBaseOpt.isEmpty()){
            player.sendMessage(
                    Text.literal("Portal '" + destinationName + "' no longer exists")
                            .formatted(Formatting.RED),
                    false
            );
            return;
        }

        teleportToBase(player.getEntityWorld().toServerWorld(), player, destBaseOpt.get());
    }
}
