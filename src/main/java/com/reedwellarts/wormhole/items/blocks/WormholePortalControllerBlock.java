package com.reedwellarts.wormhole.items.blocks;

import com.reedwellarts.wormhole.registrar.BlockRegistrar;
import com.reedwellarts.wormhole.items.blocks.BlockEntity.WormholeControllerBlockEntity;
import com.reedwellarts.wormhole.network.OpenControllerScreenPayload;
import com.reedwellarts.wormhole.util.WormholeLinkState;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WormholePortalControllerBlock extends Block implements BlockEntityProvider {

    public WormholePortalControllerBlock(Settings settings) {
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
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state){
        return new WormholeControllerBlockEntity(BlockRegistrar.CONTROLLER_BLOCK_ENTITY, pos, state);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state){
        return BlockRenderType.MODEL;
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit){
        if (world.isClient()){
            return ActionResult.SUCCESS;
        }

        ServerWorld serverWorld = (ServerWorld) world;
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof WormholeControllerBlockEntity controllerBE)){
            return ActionResult.PASS;
        }

        if (controllerBE.getLinkedBase() == null){
            BlockPos base = findNearbyBase(serverWorld, pos);
            if (base == null){
                player.sendMessage(
                        Text.literal("No portal base found within 5 blocks")
                                .formatted(Formatting.RED),
                        false
                );
                return ActionResult.FAIL;
            }
            controllerBE.setLinkedBase(base);
        }

        WormholeLinkState linkState = WormholeLinkState.get(serverWorld);
        Map<String, BlockPos> all = linkState.allPortals();
        BlockPos linkedBase = controllerBE.getLinkedBase();

        List<String> destinations = new ArrayList<>();
        for (Map.Entry<String, BlockPos> entry : all.entrySet()){
            if (!entry.getValue().equals(linkedBase)){
                destinations.add(entry.getKey());
            }
        }
        destinations.sort(String::compareTo);

        if (destinations.isEmpty()){
            player.sendMessage(
                    Text.literal("No other portals are registered yet. Use the Wormhole Linker to add more")
                            .formatted(Formatting.RED),
                    false
            );
        }

        ServerPlayNetworking.send(
                (ServerPlayerEntity) player,
                new OpenControllerScreenPayload(pos, destinations)
        );
        return ActionResult.SUCCESS;
    }

    private static BlockPos findNearbyBase(ServerWorld world, BlockPos controllerPos){
        for (BlockPos candidate : BlockPos.iterateOutwards(controllerPos, 5, 5, 5)){
            if (world.getBlockState(candidate).isOf(BlockRegistrar.WORMHOLE_PORTAL_BASE)){
                return candidate.toImmutable();
            }
        }
        return null;
    }
}
