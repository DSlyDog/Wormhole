package com.reedwellarts.wormhole.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.reedwellarts.wormhole.util.WormholeLinkState;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

import java.util.Map;

public class WormholeCommands {

    private WormholeCommands() {}

    public static void registerCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            register(dispatcher);
        });
    }

    private static void register(CommandDispatcher<ServerCommandSource> dispatcher){
        dispatcher.register(
                CommandManager.literal("wormhole")
                        .then(CommandManager.literal("list").executes(WormholeCommands::listPortals))
        );
    }

    private static int listPortals(CommandContext<ServerCommandSource> ctx){
        ServerCommandSource source = ctx.getSource();
        ServerWorld world = source.getWorld();
        WormholeLinkState state = WormholeLinkState.get(world);

        Map<String, BlockPos> portals = state.allPortals();
        if (portals.isEmpty()){
            source.sendFeedback(() ->
                    Text.literal("No portals registered in this dimension")
                            .formatted(Formatting.RED),
                    false);
            return 0;
        }

        portals.forEach((name, pos) ->
                source.sendFeedback(() ->
                        Text.literal(" '" + name + "' → " + pos.toShortString())
                                .formatted(Formatting.AQUA),
                        false));

        source.sendFeedback(() ->
                Text.literal("Total: " + portals.size() + " portal(s)")
                        .formatted(Formatting.YELLOW),
                false);
        return portals.size();
    }
}
