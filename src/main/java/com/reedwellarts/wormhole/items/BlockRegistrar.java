package com.reedwellarts.wormhole.items;

import com.reedwellarts.wormhole.Wormhole;
import com.reedwellarts.wormhole.items.blocks.*;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class BlockRegistrar {

    public static final Block WORMHOLE_PORTAL_BASE = register(
            "wormhole_portal_base",
            WormholePortalBaseBlock::new,
            AbstractBlock.Settings.create()
                    .mapColor(MapColor.PURPLE)
                    .strength(3.0f, 6.0f)
                    .sounds(BlockSoundGroup.IRON),
            true
    );

    public static final Block WORMHOLE_PORTAL_FRAME = register(
            "wormhole_portal_frame",
            WormholePortalFrameBlock::new,
            AbstractBlock.Settings.create()
                    .mapColor(MapColor.PURPLE)
                    .strength(3.0f, 6.0f)
                    .sounds(BlockSoundGroup.IRON),
            true
    );

    public static final Block WORMHOLE_PORTAL = register(
            "wormhole_portal",
            WormholePortalBlock::new,
            AbstractBlock.Settings.create()
                    .mapColor(MapColor.PURPLE)
                    .noCollision()
                    .nonOpaque()
                    .strength(-1.0f, 3600000.0f)
                    .luminance(state -> 11)
                    .sounds(BlockSoundGroup.GLASS)
                    .dropsNothing(),
            false
    );

    public static final Block WORMHOLE_PORTAL_CONTROLLER = register(
            "wormhole_portal_controller",
            WormholePortalControllerBlock::new,
            AbstractBlock.Settings.create()
                    .mapColor(MapColor.PURPLE)
                    .strength(3.0f, 6.0f)
                    .sounds(BlockSoundGroup.IRON),
            true
    );

    public static BlockEntityType<WormholeControllerBlockEntity> CONTROLLER_BLOCK_ENTITY;

    private static Block register(String name,
                                  Function<AbstractBlock.Settings, Block> factory,
                                  AbstractBlock.Settings settings,
                                  boolean shouldRegisterItem) {
        Identifier id = Identifier.of(Wormhole.MOD_ID, name);
        RegistryKey<Block> blockKey = RegistryKey.of(RegistryKeys.BLOCK, id);

        Block block = factory.apply(settings.registryKey(blockKey));
        Registry.register(Registries.BLOCK, blockKey, block);

        if (shouldRegisterItem) {
            RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, id);
            BlockItem blockItem = new BlockItem(block, new Item.Settings().registryKey(itemKey).useBlockPrefixedTranslationKey());
            Registry.register(Registries.ITEM, itemKey, blockItem);
        }

        return block;
    }

    public static void registerModBlocks() {
        Wormhole.LOGGER.info( "Registering blocks for {}", Wormhole.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(entries -> {
            entries.add(WORMHOLE_PORTAL_BASE);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(entries -> {
            entries.add(WORMHOLE_PORTAL_FRAME);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(entries -> {
            entries.add(WORMHOLE_PORTAL_CONTROLLER);
        });

        CONTROLLER_BLOCK_ENTITY = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                Identifier.of(Wormhole.MOD_ID, "wormhole_portal_controller"),
                FabricBlockEntityTypeBuilder.create(
                        (pos, state) -> new WormholeControllerBlockEntity(CONTROLLER_BLOCK_ENTITY, pos, state),
                        WORMHOLE_PORTAL_CONTROLLER
                ).build()
        );
    }
}
