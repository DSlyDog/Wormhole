package com.reedwellarts.wormhole.items.item;

import com.reedwellarts.wormhole.network.OpenRemoteScreenPayload;
import com.reedwellarts.wormhole.util.WormholeLinkState;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HandheldPortalItem extends Item {

    public HandheldPortalItem(Settings settings) {
        super(settings);
    }



    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient()){
            return ActionResult.SUCCESS;
        }

        ItemStack stack = user.getMainHandStack();
        if (stack.getDamage() >= stack.getMaxDamage()){
            return ActionResult.FAIL;
        }

        ServerWorld serverWorld = (ServerWorld) world;
        WormholeLinkState linkState = WormholeLinkState.get(serverWorld);

        List<String> destinations = new ArrayList<>();

        for (Map.Entry<String, BlockPos> entry : linkState.allPortals().entrySet()){
            destinations.add(entry.getKey());
        }

        if (destinations.isEmpty()){
            user.sendMessage(Text.literal("No portals are registered yet. Use a linker to add some")
                    .formatted(Formatting.RED),
                    false);
            return ActionResult.FAIL;
        }

        ServerPlayNetworking.send(
                (ServerPlayerEntity) user,
                new OpenRemoteScreenPayload(destinations)
        );

        return ActionResult.SUCCESS;
    }
}
