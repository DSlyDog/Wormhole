package com.reedwellarts.wormhole.items.blocks.BlockEntity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;

public class WormholeControllerBlockEntity extends BlockEntity {

    private BlockPos linkedBase = null;

    public WormholeControllerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state){
        super(type, pos, state);
    }

    public BlockPos getLinkedBase(){
        return linkedBase;
    }

    public void setLinkedBase(BlockPos pos){
        this.linkedBase = pos == null? null : pos.toImmutable();
        markDirty();
    }

    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);
        if (linkedBase != null){
            view.putLong("LinkedBase", linkedBase.asLong());
        }
    }

    @Override
    protected void readData(ReadView view) {
        super.readData(view);
        if (view.contains("LinkedBase")){
            linkedBase = BlockPos.fromLong(view.getLong("LinkedBase", 0));
        }
    }
}
