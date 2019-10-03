package com.ringosham.translationmod.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import org.lwjgl.opengl.GL11;

public class TextButton extends GuiButton {
    TextButton(int buttonId, int x, int y, int widthIn, String buttonText) {
        super(buttonId, x, y, widthIn, 10, buttonText);
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float tick) {
        GL11.glColor4f(1, 1, 1, 1);
        this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
        mc.fontRenderer.drawString(this.displayString, x, y, 0x0000AA, false);
    }
}
