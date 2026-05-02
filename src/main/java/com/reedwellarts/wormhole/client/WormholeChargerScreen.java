package com.reedwellarts.wormhole.client;

import com.reedwellarts.wormhole.screen.WormholeChargerScreenHandler;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class WormholeChargerScreen extends HandledScreen<WormholeChargerScreenHandler> {

    private static final Identifier TEXTURE = Identifier.of("wormhole", "textures/gui/wormhole_charger.png");

    public WormholeChargerScreen(WormholeChargerScreenHandler handler, PlayerInventory inventory, Text title){
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.playerInventoryTitleX = 8;
        this.playerInventoryTitleY = this.backgroundHeight - 94;
        this.titleX = 8;
        this.titleY = 6;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        renderBackground(context, mouseX, mouseY, deltaTicks);
        super.render(context, mouseX, mouseY, deltaTicks);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY){
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, backgroundWidth, backgroundHeight);
    }
}
