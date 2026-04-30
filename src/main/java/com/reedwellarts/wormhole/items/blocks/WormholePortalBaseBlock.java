package com.reedwellarts.wormhole.items.blocks;

import com.reedwellarts.wormhole.Wormhole;
import com.reedwellarts.wormhole.items.ItemRegistrar;
import com.reedwellarts.wormhole.network.OpenNameScreenPayload;
import com.reedwellarts.wormhole.network.WormholeServerNetworking;
import com.reedwellarts.wormhole.portal.WormholeLinkState;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import java.util.Map;

public class WormholePortalBaseBlock extends Block {

    public WormholePortalBaseBlock(Settings settings){
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(Properties.FACING, Direction.NORTH));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder){
        builder.add(Properties.FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx){
        return getDefaultState().with(Properties.FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit){
        if (!player.getMainHandStack().isOf(ItemRegistrar.WORMHOLE_LINKER)){
            return ActionResult.PASS;
        }

        if (world.isClient()){
            return ActionResult.SUCCESS;
        }

        ServerWorld serverWorld = (ServerWorld) world;
        WormholeLinkState linkState = WormholeLinkState.get(serverWorld);

        boolean alreadyRegistered = linkState.allPortals().containsValue(pos.toImmutable());
        if (alreadyRegistered){
            player.sendMessage(
                    Text.literal("This portal base is already registered")
                            .formatted(Formatting.RED),
                    false
            );
            return ActionResult.FAIL;
        }

        ServerPlayNetworking.send((ServerPlayerEntity) player, new OpenNameScreenPayload(pos));
        return ActionResult.SUCCESS;
    }

    @Override
    protected void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
        if (!world.getBlockState(pos).isOf(state.getBlock())){
            WormholeLinkState linkState = WormholeLinkState.get(world);

            if (pos != null) {
                WormholeLinkState.removePortalAbove(world, pos);
                for (Map.Entry<String, BlockPos> link : linkState.allPortals().entrySet()) {
                    Wormhole.LOGGER.info("link value: {}, base value {}", link.getValue().toString(), pos.toString());
                    if (link.getValue().equals(pos)) {
                        linkState.unregisterPortal(link.getKey());
                        WormholeServerNetworking.sendSyncPortalNamesPayload(world);
                        break;
                    }
                }
            }
        }

        super.onStateReplaced(state, world, pos, moved);
    }
}
