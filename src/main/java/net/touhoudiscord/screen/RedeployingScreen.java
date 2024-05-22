package net.touhoudiscord.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class RedeployingScreen extends Screen {

    private final float duration;
    private float time;

    public RedeployingScreen(float duration) {
        super(Component.empty());
        this.duration = duration;
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        time += delta;
        context.fill(0, 0, width, height, Math.round((1-Math.abs((float)Math.sin(((time/(duration/2))-1)*(Math.PI/2))))*255)<<24);
        super.render(context, mouseX, mouseY, delta);
        if (time > duration) minecraft.setScreen(null);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}
