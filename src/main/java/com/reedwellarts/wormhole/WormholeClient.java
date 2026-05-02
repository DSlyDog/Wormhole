package com.reedwellarts.wormhole;

import com.reedwellarts.wormhole.client.WormholeChargerScreen;
import com.reedwellarts.wormhole.client.WormholeClientNetworking;
import com.reedwellarts.wormhole.registrar.ItemRegistrar;
import com.reedwellarts.wormhole.registrar.ScreenHandlerRegistrar;
import com.reedwellarts.wormhole.screen.WormholeChargerScreenHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.item.ItemGroups;

public class WormholeClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
            entries.add(ItemRegistrar.WORMHOLE_LINKER);
        });

        WormholeClientNetworking.register();

        HandledScreens.register(
                ScreenHandlerRegistrar.WORMHOLE_CHARGER_HANDLER,
                WormholeChargerScreen::new
        );
    }
}
