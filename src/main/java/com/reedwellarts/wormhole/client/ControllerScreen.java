package com.reedwellarts.wormhole.client;

import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.*;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class ControllerScreen extends Screen {

    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 20;
    private static final int BUTTON_GAP = 4;

    private final BlockPos controllerPos;
    private final List<String> destinations;

    public ControllerScreen(BlockPos controllerPos, List<String> destinations){
        super(Text.literal("Select Destination"));
        this.controllerPos = controllerPos;
        this.destinations = destinations;
    }

    @Override
    protected void init(){
        addDrawableChild(new TextWidget(
                width / 2 - (textRenderer.getWidth(getTitle().asOrderedText()) / 2),
                10,
                textRenderer.getWidth(getTitle().asOrderedText()),
                20,
                getTitle(),
                textRenderer
        ));

        addDrawableChild(new PortalListWidget());

        addDrawableChild(ButtonWidget.builder(Text.literal("Cancel"), button -> close())
                .dimensions(width / 2 - BUTTON_WIDTH / 4, height - 25, BUTTON_WIDTH / 2, BUTTON_HEIGHT)
                .build());
    }

    @Override
    public boolean shouldPause(){
        return false;
    }

    private class PortalListWidget extends EntryListWidget<PortalListWidget.PortalEntry> {

        public PortalListWidget(){
            super(
                    ControllerScreen.this.client,
                    ControllerScreen.this.width,
                    ControllerScreen.this.height - 60,
                    30,
                    BUTTON_HEIGHT + 4
            );

            for (String destination : destinations){
                addEntry(new PortalEntry(destination));
            }
        }

        @Override
        protected void appendClickableNarrations(NarrationMessageBuilder builder) {

        }

        @Override
        protected void renderEntry(DrawContext context, int mouseX, int mouseY, float delta, PortalEntry entry) {
            entry.button.setY(entry.getY());
            super.renderEntry(context, mouseX, mouseY, delta, entry);
        }

        private class PortalEntry extends EntryListWidget.Entry<PortalEntry>{

            private final ButtonWidget button;

            public PortalEntry(String name){
                this.button = ButtonWidget.builder(Text.literal(name), btn -> {
                    WormholeClientNetworking.sendSelectedDestination(controllerPos, name);
                    close();
                }).width(BUTTON_WIDTH).build();
                button.setX(width / 2 - BUTTON_WIDTH / 2);
                button.setY(height / 2 - 40);
                button.setX(width / 2 - BUTTON_WIDTH / 2);
            }

            @Override
            public void setY(int y){
                super.setY(y);
                button.setY(y);
            }

            @Override
            public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
                button.render(context, mouseX, mouseY, deltaTicks);
            }

            @Override
            public boolean mouseClicked(Click click, boolean doubled) {
                return button.mouseClicked(click, doubled);
            }
        }
    }
}
