package com.reedwellarts.wormhole.items.blocks;

import com.mojang.serialization.MapCodec;
import com.reedwellarts.wormhole.util.TeleportUtil;
import com.reedwellarts.wormhole.util.WormholeLinkState;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.*;

public class WormholePortalBlock extends Block {

    public static final MapCodec<WormholePortalBlock> CODEC = createCodec(WormholePortalBlock::new);
    private static final VoxelShape SHAPE_NS = Block.createCuboidShape(0, 0, 7, 16, 16, 9);
    private static final VoxelShape SHAPE_EW = Block.createCuboidShape(7, 0, 0, 9, 16, 16);

    public WormholePortalBlock(Settings settings){
        super(settings);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder){
        builder.add(Properties.HORIZONTAL_FACING);
    }

    @Override
    protected MapCodec<? extends Block> getCodec(){
        return CODEC;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx){
        return getDefaultState().with(Properties.HORIZONTAL_FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx){
        if (!state.contains(Properties.HORIZONTAL_FACING)){
            return SHAPE_NS;
        }

        Direction facing = state.get(Properties.HORIZONTAL_FACING);
        return (facing == Direction.EAST || facing == Direction.WEST) ? SHAPE_EW : SHAPE_NS;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context){
        return VoxelShapes.empty();
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity, EntityCollisionHandler handler, boolean bl){
        if (world.isClient()){
            return;
        }

        if (entity.hasVehicle() || entity.hasPassengers()){
            return;
        }

        ServerWorld serverWorld = (ServerWorld) world;
        WormholeLinkState linkState = WormholeLinkState.get(serverWorld);

        Direction facing = state.contains(Properties.HORIZONTAL_FACING)
                ? state.get(Properties.HORIZONTAL_FACING)
                : Direction.NORTH;
        boolean alongX = (facing == Direction.NORTH || facing == Direction.SOUTH);

        BlockPos basePos = WormholeLinkState.findBase(serverWorld, pos, alongX);
        if (basePos == null){
            return;
        }

        Optional<BlockPos> destOpt = linkState.consumeDestination(basePos);
        if (destOpt.isEmpty()){
            return;
        }

        TeleportUtil.teleportToBase(serverWorld, entity, destOpt.get());
        entity.setPortalCooldown(Entity.DEFAULT_PORTAL_COOLDOWN);

        WormholeLinkState.removePortalAbove(serverWorld, basePos);
    }

    @Override
    protected void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
        if (!world.getBlockState(pos).isOf(state.getBlock())){
            Direction facing = state.contains(Properties.HORIZONTAL_FACING)
                    ? state.get(Properties.HORIZONTAL_FACING)
                    : Direction.NORTH;
            boolean alongX = (facing == Direction.NORTH || facing == Direction.SOUTH);
            BlockPos base = WormholeLinkState.findBase(world, pos, alongX);
            if (base != null) WormholeLinkState.removePortalAbove(world, base);
        }
    }
}
