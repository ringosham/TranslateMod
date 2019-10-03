package com.ringosham.translationmod.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class CommonGui extends GuiScreen {
    final int regularButtonWidth = 100;
    final int regularButtonHeight = 20;
    final int smallButtonLength = 20;
    private final ResourceLocation texture = new ResourceLocation("minecraft", "textures/gui/demo_background.png");
    private int guiHeight;
    private int guiWidth;

    CommonGui(int guiHeight, int guiWidth) {
        if (guiWidth < 10 && guiHeight < 10)
            throw new IllegalArgumentException("GUI width too short!");
        this.guiHeight = guiHeight;
        this.guiWidth = guiWidth;
    }

    @Override
    public void drawScreen(int x, int y, float tick) {
        //Draws the base background
        GL11.glColor4f(1, 1, 1, 1);
        drawDefaultBackground();
        mc.renderEngine.bindTexture(texture);
        //Top left corner
        drawTexturedModalRect(getXOrigin(), getYOrigin(), 0, 0, 4, 4);
        //Bottom left corner
        drawTexturedModalRect(getXOrigin(), getYOrigin() + guiHeight - 4, 0, 160, 4, 4);
        //Top right corner
        drawTexturedModalRect(getXOrigin() + guiWidth - 4, getYOrigin(), 242, 0, 4, 4);
        //Bottom right corner
        drawTexturedModalRect(getXOrigin() + guiWidth - 4, getYOrigin() + guiHeight - 4, 242, 160, 4, 4);
        //Top side
        for (int i = 0; i < guiWidth - 8; i++)
            drawTexturedModalRect(getXOrigin() + 4 + i, getYOrigin(), 4, 0, 1, 4);
        //Left side
        for (int i = 0; i < guiHeight - 8; i++)
            drawTexturedModalRect(getXOrigin(), getYOrigin() + 4 + i, 0, 4, 4, 1);
        //Right side
        for (int i = 0; i < guiHeight - 8; i++)
            drawTexturedModalRect(getXOrigin() + guiWidth - 4, getYOrigin() + 4 + i, 242, 4, 4, 1);
        //Bottom side
        for (int i = 0; i < guiWidth - 8; i++)
            drawTexturedModalRect(getXOrigin() + 4 + i, getYOrigin() + guiHeight - 4, 4, 160, 1, 4);
        //Center
        drawRect(getXOrigin() + 4, getYOrigin() + 4, getXOrigin() + guiWidth - 4, getYOrigin() + guiHeight - 4, 0xffc6c6c6);
        //super to draw the buttons registered in GuiInit()
        super.drawScreen(x, y, tick);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    //The gui's x coordinate on screen
    public int getXOrigin() {
        return (width - guiWidth) / 2;
    }

    //The gui's y coordinate on screen
    public int getYOrigin() {
        return (height - guiHeight) / 2;
    }

    public int getLeftMargin() {
        return getXOrigin() + 5;
    }

    public int getRightMargin(int elementWidth) {
        return getXOrigin() + guiWidth - elementWidth - 5;
    }

    public int getTopMargin() {
        return getYOrigin() + 5;
    }

    public int getTextWidth(String text) {
        int length = 0;
        for (char character : text.toCharArray()) {
            length += Minecraft.getMinecraft().fontRendererObj.getCharWidth(character);
        }
        return length;
    }
}
