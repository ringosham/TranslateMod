package com.ringosham.translationmod.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import org.lwjgl.opengl.GL11;

public class CommonGui extends Screen {
    final int regularButtonWidth = 100;
    final int regularButtonHeight = 20;
    final int smallButtonLength = 20;
    private final ResourceLocation texture = new ResourceLocation("minecraft", "textures/gui/demo_background.png");
    private int guiHeight;
    private int guiWidth;

    CommonGui(String title, int guiHeight, int guiWidth) {
        super(new StringTextComponent(title));
        if (guiWidth < 10 || guiHeight < 10)
            throw new IllegalArgumentException("GUI width too short!");
        this.guiHeight = guiHeight;
        this.guiWidth = guiWidth;
    }

    @Override
    public void render(int x, int y, float tick) {
        //Draws the base background
        GL11.glColor4f(1, 1, 1, 1);
        renderBackground();
        getMinecraft().getTextureManager().bindTexture(texture);
        //Top left corner
        blit(getXOrigin(), getYOrigin(), 0, 0, 4, 4);
        //Bottom left corner
        blit(getXOrigin(), getYOrigin() + guiHeight - 4, 0, 160, 4, 4);
        //Top right corner
        blit(getXOrigin() + guiWidth - 4, getYOrigin(), 242, 0, 4, 4);
        //Bottom right corner
        blit(getXOrigin() + guiWidth - 4, getYOrigin() + guiHeight - 4, 242, 160, 4, 4);
        //Top side
        for (int i = 0; i < guiWidth - 8; i++)
            blit(getXOrigin() + 4 + i, getYOrigin(), 4, 0, 1, 4);
        //Left side
        for (int i = 0; i < guiHeight - 8; i++)
            blit(getXOrigin(), getYOrigin() + 4 + i, 0, 4, 4, 1);
        //Right side
        for (int i = 0; i < guiHeight - 8; i++)
            blit(getXOrigin() + guiWidth - 4, getYOrigin() + 4 + i, 242, 4, 4, 1);
        //Bottom side
        for (int i = 0; i < guiWidth - 8; i++)
            blit(getXOrigin() + 4 + i, getYOrigin() + guiHeight - 4, 4, 160, 1, 4);
        //Center
        fill(getXOrigin() + 4, getYOrigin() + 4, getXOrigin() + guiWidth - 4, getYOrigin() + guiHeight - 4, 0xffc6c6c6);
        //super to draw the buttons registered in GuiInit()
        super.render(x, y, tick);
    }

    @Override
    public boolean isPauseScreen() {
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
            length += Minecraft.getInstance().fontRenderer.getCharWidth(character);
        }
        return length;
    }
}
