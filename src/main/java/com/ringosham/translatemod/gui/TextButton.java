package com.ringosham.translatemod.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import org.lwjgl.opengl.GL11;

public class TextButton extends GuiButton {
    TextButton(int buttonId, int x, int y, int widthIn, String buttonText) {
        super(buttonId, x, y, widthIn, 10, buttonText);
    }

    @Override
    public void drawButton(Minecraft mc, int x, int y) {
        GL11.glColor4f(1, 1, 1, 1);
        this.hovered = x >= this.xPosition && y >= this.yPosition && x < this.xPosition + this.width && y < this.yPosition + this.height;
        mc.fontRendererObj.drawString(this.displayString, xPosition, yPosition, 0x0000AA, false);
    }
}
