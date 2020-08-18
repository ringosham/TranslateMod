package com.ringosham.translationmod.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import org.lwjgl.opengl.GL11;

public class TextButton extends Button {

    private final int color;

    public TextButton(int x, int y, int width, String text, Button.IPressable onPress, int color) {
        super(x, y, width, 10, text, onPress);
        this.color = color;
    }

    @Override
    public void render(int mouseX, int mouseY, float tick) {
        GL11.glColor4f(1, 1, 1, 1);
        this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
        Minecraft.getInstance().fontRenderer.drawString(this.getMessage(), x, y, color);
    }
}
