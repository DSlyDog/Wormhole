package com.reedwellarts.wormhole.portal;

import com.jcraft.jorbis.Block;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.reedwellarts.wormhole.Wormhole;
import com.reedwellarts.wormhole.items.BlockRegistrar;
import com.reedwellarts.wormhole.network.WormholeServerNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.PersistentStateType;

import java.util.*;

public class WormholeLinkState extends PersistentState {

    private static final String SAVE_ID = Wormhole.MOD_ID + "_links";

    private static final Codec<WormholeLinkState> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.unboundedMap(Codec.STRING, BlockPos.CODEC)
                            .fieldOf("portals")
                            .forGetter(s -> Map.copyOf(s.namedPortals))
            ).apply(instance, WormholeLinkState::new)
    );

    public static final PersistentStateType<WormholeLinkState> TYPE = new PersistentStateType<>(
            SAVE_ID,
            WormholeLinkState::new,
            CODEC,
            null
    );

    private final Map<String, BlockPos> namedPortals = new HashMap<>();
    private final Map<BlockPos, BlockPos> oneShotDestinations = new HashMap<>();

    public WormholeLinkState(){}

    private WormholeLinkState(Map<String, BlockPos> initial){
        this.namedPortals.putAll(initial);
    }

    public static WormholeLinkState get(ServerWorld world) {
        PersistentStateManager manager = world.getPersistentStateManager();
        return manager.getOrCreate(TYPE);
    }

    public void registerPortal(String name, BlockPos base){
        namedPortals.put(name, base.toImmutable());
        markDirty();
    }

    public void unregisterPortal(String name){
        if (namedPortals.remove(name) != null) markDirty();
    }

    public Optional<BlockPos> getPortalPos(String name){
        return Optional.ofNullable(namedPortals.get(name));
    }

    public Map<String, BlockPos> allPortals() {
        return Collections.unmodifiableMap(namedPortals);
    }

    public boolean activatePortal(ServerWorld world, BlockPos sourceBase, BlockPos destBase){
        if (!tryPlacePortalAbove(world, sourceBase)){
            return false;
        }

        oneShotDestinations.put(sourceBase.toImmutable(), destBase.toImmutable());
        return true;
    }

    public Optional<BlockPos> consumeDestination(BlockPos sourceBase){
        return Optional.ofNullable(oneShotDestinations.remove(sourceBase.toImmutable()));
    }

    public boolean hasActiveDestination(BlockPos sourceBase){
        return oneShotDestinations.containsKey(sourceBase);
    }

    public static boolean tryPlacePortalAbove(ServerWorld world, BlockPos base){
        BlockState baseState = world.getBlockState(base);
        if (!baseState.isOf(BlockRegistrar.WORMHOLE_PORTAL_BASE)){
            return false;
        }

        Direction facing = baseState.contains(Properties.FACING)
                ? baseState.get(Properties.FACING)
                : Direction.NORTH;

        boolean alongX = (facing == Direction.NORTH || facing == Direction.SOUTH);

        BlockPos seed = base.up();
        BlockState seedState = world.getBlockState(seed);
        if (!seedState.isAir() && !seedState.isReplaceable()){
            return false;
        }

        Set<BlockPos> interior = new HashSet<>();
        Queue<BlockPos> queue = new ArrayDeque<>();
        queue.add(seed.toImmutable());
        interior.add(seed.toImmutable());

        while(!queue.isEmpty()){
            if (interior.size() > 256){
                return false;
            }

            BlockPos current = queue.poll();
            for (BlockPos neighbor : planeNeighbors(current, alongX, false)){
                if (interior.contains(neighbor)){
                    continue;
                }

                BlockState neighborState = world.getBlockState(neighbor);
                if (isFrameBlock(neighborState)){
                    continue;
                }else if (neighborState.isAir() || neighborState.isReplaceable()){
                    interior.add(neighbor.toImmutable());
                    queue.add(neighbor.toImmutable());
                }else{
                    Wormhole.LOGGER.info("Not frame or mutable: {}", neighborState);
                    Wormhole.LOGGER.info("Immutable pos: {}", neighbor.toString());
                    return false;
                }
            }
        }

        for (BlockPos pos : interior){
            for (BlockPos neighbor : planeNeighbors(pos, alongX, false)){
                if (interior.contains(neighbor)){
                    continue;
                }
                if (!isFrameBlock(world.getBlockState(neighbor))){
                    return false;
                }
            }
        }

        BlockState portalState = BlockRegistrar.WORMHOLE_PORTAL.getDefaultState()
                .with(Properties.HORIZONTAL_FACING, facing);
        for (BlockPos pos : interior){
            world.setBlockState(pos.toImmutable(), portalState);
        }

        return true;
    }

    private static boolean isFrameBlock(BlockState blockState) {
        return blockState.isOf(BlockRegistrar.WORMHOLE_PORTAL_FRAME)
                || blockState.isOf(BlockRegistrar.WORMHOLE_PORTAL_BASE);
    }

    public static List<BlockPos> planeNeighbors(BlockPos pos, boolean alongX, boolean includeDown){
        if (alongX){
            List<BlockPos> positions = new ArrayList<>(List.of(pos.east(), pos.west(), pos.up()));
            if (includeDown) positions.add(pos.down());
            return positions;
        }else{
            List<BlockPos> positions = new ArrayList<>(List.of(pos.north(), pos.south(), pos.up()));
            if (includeDown) positions.add(pos.down());
            return positions;
        }
    }

    public static void removePortalAbove(ServerWorld world, BlockPos base){
        BlockState baseState = world.getBlockState(base);
        Direction facing = baseState.contains(Properties.FACING)
                ? baseState.get(Properties.FACING)
                : Direction.NORTH;
        boolean alongX = (facing == Direction.NORTH || facing == Direction.SOUTH);

        BlockPos seed = base.up();
        if (!world.getBlockState(seed).isOf(BlockRegistrar.WORMHOLE_PORTAL)){
            return;
        }

        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new ArrayDeque<>();
        queue.add(seed.toImmutable());

        while(!queue.isEmpty()){
            BlockPos current = queue.poll();
            if (!visited.add(current)){
                continue;
            }

            if (world.getBlockState(current).isOf(BlockRegistrar.WORMHOLE_PORTAL)){
                world.removeBlock(current, false);
                for (BlockPos neighbor : planeNeighbors(current, alongX, false)) {
                    if (!visited.contains(neighbor)){
                        queue.add(neighbor.toImmutable());
                    }
                }
            }
        }
    }

    public static BlockPos findBase(ServerWorld world, BlockPos start, boolean alongX){
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new ArrayDeque<>();
        queue.add(start.toImmutable());

        while(!queue.isEmpty()){
            BlockPos current = queue.poll();
            if (!visited.add(current)) continue;

            for (BlockPos neighbor : WormholeLinkState.planeNeighbors(current, alongX, true)){
                BlockState neighborState = world.getBlockState(neighbor);
                if (neighborState.isOf(BlockRegistrar.WORMHOLE_PORTAL_BASE)){
                    return neighbor;
                }
                if (neighborState.isOf(BlockRegistrar.WORMHOLE_PORTAL) && !visited.contains(neighbor)){
                    queue.add(neighbor.toImmutable());
                }
            }
        }
        return null;
    }
}
