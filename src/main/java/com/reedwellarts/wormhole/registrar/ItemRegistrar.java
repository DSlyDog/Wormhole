package com.reedwellarts.wormhole.registrar;

import com.reedwellarts.wormhole.Wormhole;
import com.reedwellarts.wormhole.items.item.HandheldPortalBattery;
import com.reedwellarts.wormhole.items.item.HandheldPortalItem;
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

    public static final Item HANDHELD_PORTAL_BATTERY = register(
            "handheld_portal_battery",
            HandheldPortalBattery::new,
            new Item.Settings().maxCount(1)
    );

    public static final Item HANDHELD_PORTAL = register(
            "handheld_portal",
            HandheldPortalItem::new,
            new Item.Settings()
                    .maxCount(1)
                    .maxDamage(3)
                    .repairable(HANDHELD_PORTAL_BATTERY)
    );

    private static Item register(String name, Function<Item.Settings, Item> factory, Item.Settings settings){
        Identifier id = Identifier.of(Wormhole.MOD_ID, name);
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, id);
        Item item = factory.apply(settings.registryKey(key));
        return Registry.register(Registries.ITEM, key, item);
    }

    public static void registerModItems(){
        Wormhole.LOGGER.info("Registering items for {}", Wormhole.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(entries ->
            entries.add(WORMHOLE_LINKER)
        );

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(entries ->
                entries.add(HANDHELD_PORTAL)
        );

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(entries ->
                entries.add(HANDHELD_PORTAL_BATTERY));
    }
}
