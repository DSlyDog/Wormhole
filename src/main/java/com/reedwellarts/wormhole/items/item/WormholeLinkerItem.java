package com.reedwellarts.wormhole.items.item;

import com.reedwellarts.wormhole.components.ModComponents;
import com.reedwellarts.wormhole.items.BlockRegistrar;
import com.reedwellarts.wormhole.portal.WormholeLinkState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WormholeLinkerItem extends Item {

    public WormholeLinkerItem(Settings settings){
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context){
        World world = context.getWorld();
        PlayerEntity player = context.getPlayer();
        BlockPos clickedPos = context.getBlockPos();

        if (player == null) {
            return ActionResult.PASS;
        }

        if (!world.getBlockState(clickedPos).isOf(BlockRegistrar.WORMHOLE_PORTAL_BASE)){
            return ActionResult.PASS;
        }

        if (world.isClient()) {
            return ActionResult.SUCCESS;
        }

        context.getStack().set(ModComponents.PENDING_PORTAL_PS, clickedPos.toImmutable());

        return ActionResult.SUCCESS;
    }
}
