package com.reedwellarts.wormhole.items.blocks;

import com.reedwellarts.wormhole.util.WormholeLinkState;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class WormholePortalFrameBlock extends Block {

    public WormholePortalFrameBlock(Settings settings){
        super(settings);
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
