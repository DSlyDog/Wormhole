package com.reedwellarts.wormhole.screen;

import com.reedwellarts.wormhole.registrar.ScreenHandlerRegistrar;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.FurnaceOutputSlot;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;
import org.jspecify.annotations.Nullable;

public class WormholeChargerScreenHandler extends ScreenHandler {

    private final Inventory inventory;
    private final PropertyDelegate properties;

    public WormholeChargerScreenHandler(int syncId, PlayerInventory playerInv) {
        this(syncId, playerInv, new SimpleInventory(3), new ArrayPropertyDelegate(2));
    }

    public WormholeChargerScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate){
        super(ScreenHandlerRegistrar.WORMHOLE_CHARGER_HANDLER, syncId);

        this.inventory = inventory;
        this.properties = propertyDelegate;

        checkSize(inventory, 3);
        inventory.onOpen(playerInventory.player);
        addProperties(properties);

        addSlot(new Slot(inventory, 0, 56, 17));
        addSlot(new Slot(inventory, 1, 56, 53));
        addSlot(new FurnaceOutputSlot(playerInventory.player, inventory, 1, 116, 35));

        for (int row = 0; row < 3; row++){
            for (int col = 0; col < 9; col++){
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        for (int col = 0; col < 9; col++){
            addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return inventory.canPlayerUse(player);
    }
}
