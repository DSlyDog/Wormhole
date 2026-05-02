package com.reedwellarts.wormhole.items.blocks.BlockEntity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.FurnaceScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.jspecify.annotations.Nullable;

public class WormholeChargerBlockEntity extends BlockEntity implements NamedScreenHandlerFactory {

    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(3, ItemStack.EMPTY);

    private final PropertyDelegate properties = new PropertyDelegate() {
        private final int[] data = new int[4];

        @Override
        public int get(int index) {
            return data[index];
        }

        @Override
        public void set(int index, int value) {
            data[index] = value;
        }

        @Override
        public int size() {
            return data.length;
        }
    };

    public WormholeChargerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public Text getDisplayName() {
        return Text.literal("Handheld Portal Charger");
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        SimpleInventory inv = new SimpleInventory(3) {

            @Override
            public ItemStack getStack(int slot){
                return items.get(slot);
            }

            @Override
            public void setStack(int slot, ItemStack stack){
                items.set(slot, stack);
                markDirty();
            }
        };

        return new FurnaceScreenHandler(syncId, playerInventory, inv, properties);
    }

    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);

        Inventories.writeData(view, items);
        view.putInt("LitTime", properties.get(0));
        view.putInt("LitDuration", properties.get(1));
        view.putInt("CookTime", properties.get(2));
        view.putInt("CookTimeTotal", properties.get(3));
    }

    @Override
    protected void readData(ReadView view) {
        super.readData(view);

        Inventories.readData(view, items);
        properties.set(0, view.getInt("LitTime", 0));
        properties.set(1, view.getInt("LitDuration", 0));
        properties.set(2, view.getInt("CookTime", 0));
        properties.set(3, view.getInt("CookTimeTotal", 200));
    }
}
