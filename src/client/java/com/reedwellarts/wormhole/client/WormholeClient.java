package com.reedwellarts.wormhole.client;

import com.reedwellarts.wormhole.items.ItemRegistrar;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroups;

public class WormholeClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
            entries.add(ItemRegistrar.WORMHOLE_LINKER);
        });
    }
}
