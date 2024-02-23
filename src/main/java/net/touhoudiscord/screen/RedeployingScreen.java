package net.touhoudiscord.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class RedeployingScreen extends Screen {

    private final float duration;
    private float time;

    public RedeployingScreen(float duration) {
        super(Text.empty());
        this.duration = duration;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        time += delta;
        context.fill(0, 0, width, height, Math.round((1-Math.abs((float)Math.sin(((time/(duration/2))-1)*(Math.PI/2))))*255)<<24);
        super.render(context, mouseX, mouseY, delta);
        if (time > duration) client.setScreen(null);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}
