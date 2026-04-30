package com.reedwellarts.wormhole.items;

import com.reedwellarts.wormhole.Wormhole;
import com.reedwellarts.wormhole.items.item.WormholeLinkerItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class ItemRegistrar {

    public static final Item WORMHOLE_LINKER = register(
                "wormhole_linker",
                WormholeLinkerItem::new,
                new Item.Settings().maxCount(1)
        );

    private static Item register(String name, Function<Item.Settings, Item> factory, Item.Settings settings){
        Identifier id = Identifier.of(Wormhole.MOD_ID, name);
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, id);
        Item item = factory.apply(settings.registryKey(key));
        return Registry.register(Registries.ITEM, key, item);
    }

    public static void registerModItems(){
        Wormhole.LOGGER.info("Registering items for {}", Wormhole.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
            entries.add(WORMHOLE_LINKER);
        });
    }
}
