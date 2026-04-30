package com.reedwellarts.wormhole.client;

import com.reedwellarts.wormhole.Wormhole;
import com.reedwellarts.wormhole.WormholeClient;
import com.reedwellarts.wormhole.network.RegisterPortalNamePayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.glfw.GLFW;

public class NamePortalScreen extends Screen {

    private final BlockPos basePos;
    private TextFieldWidget nameField;
    private TextWidget textWidget;
    private Text errorText;

    public NamePortalScreen(BlockPos basePos){
        super(Text.literal("Name this portal"));
        this.basePos = basePos;
    }

    @Override
    protected void init() {
        textWidget = new TextWidget(width / 2 - (textRenderer.getWidth(getTitle().asOrderedText()) / 2), height / 2 - 60, textRenderer.getWidth(getTitle().asOrderedText()), 20, getTitle(), textRenderer);
        addDrawableChild(textWidget);

        nameField = new TextFieldWidget(textRenderer, width / 2 - 100, height / 2 - 20, 200 , 20, Text.literal("Portal name"));
        nameField.setMaxLength(32);
        nameField.setFocused(true);
        setFocused(nameField);
        addDrawableChild(nameField);

        addDrawableChild(ButtonWidget.builder(Text.literal("Confirm"), button -> confirm())
                .dimensions(width / 2 - 102, height / 2 + 8, 98, 20)
                .build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Cancel"), button -> close())
                .dimensions(width / 2 + 4, height / 2 + 8, 98, 20)
                .build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);

        if (errorText != null){
            int textWidth = this.textRenderer.getWidth(this.errorText);
            context.drawText(textRenderer, errorText, width / 2 - textWidth / 2, height / 2 - 45, 0xFFFFFFFF, false);
        }
    }

    @Override
    public boolean keyPressed(KeyInput keyInput){
        if (keyInput.getKeycode() == GLFW.GLFW_KEY_ENTER || keyInput.getKeycode() == GLFW.GLFW_KEY_KP_ENTER){
            confirm();
            return true;
        }
        return super.keyPressed(keyInput);
    }

    private void confirm(){
        String name = nameField.getText().trim();

        if (name.isEmpty()){
            return;
        }

        if (PortalNames.isNameTaken(name)){
            errorText = Text.literal("Name taken").formatted(Formatting.RED);
            return;
        }

        WormholeClientNetworking.registerPortalName(basePos, name);
        close();
    }

    @Override
    public boolean shouldPause(){
        return false;
    }
}
