package com.ringosham.translationmod.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import org.lwjgl.opengl.GL11;

public class CommonGui extends Screen {
    final int regularButtonWidth = 100;
    final int regularButtonHeight = 20;
    final int smallButtonLength = 20;
    private final ResourceLocation texture = new ResourceLocation("minecraft", "textures/gui/demo_background.png");
    private final int guiHeight;
    private final int guiWidth;

    CommonGui(String title, int guiHeight, int guiWidth) {
        super(new StringTextComponent(title));
        if (guiWidth < 10 || guiHeight < 10)
            throw new IllegalArgumentException("GUI width too short!");
        this.guiHeight = guiHeight;
        this.guiWidth = guiWidth;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void render(MatrixStack stack, int x, int y, float tick) {
        //Draws the base background
        GL11.glColor4f(1, 1, 1, 1);
        renderBackground(stack);
        getMinecraft().getTextureManager().bindTexture(texture);
        //Top left corner
        blit(stack, getXOrigin(), getYOrigin(), 0, 0, 4, 4);
        //Bottom left corner
        blit(stack, getXOrigin(), getYOrigin() + guiHeight - 4, 0, 160, 4, 4);
        //Top right corner
        blit(stack, getXOrigin() + guiWidth - 4, getYOrigin(), 242, 0, 4, 4);
        //Bottom right corner
        blit(stack, getXOrigin() + guiWidth - 4, getYOrigin() + guiHeight - 4, 242, 160, 4, 4);
        //Top side
        for (int i = 0; i < guiWidth - 8; i++)
            blit(stack, getXOrigin() + 4 + i, getYOrigin(), 4, 0, 1, 4);
        //Left side
        for (int i = 0; i < guiHeight - 8; i++)
            blit(stack, getXOrigin(), getYOrigin() + 4 + i, 0, 4, 4, 1);
        //Right side
        for (int i = 0; i < guiHeight - 8; i++)
            blit(stack, getXOrigin() + guiWidth - 4, getYOrigin() + 4 + i, 242, 4, 4, 1);
        //Bottom side
        for (int i = 0; i < guiWidth - 8; i++)
            blit(stack, getXOrigin() + 4 + i, getYOrigin() + guiHeight - 4, 4, 160, 1, 4);
        //Center
        fill(stack, getXOrigin() + 4, getYOrigin() + 4, getXOrigin() + guiWidth - 4, getYOrigin() + guiHeight - 4, 0xffc6c6c6);
        //super to draw the buttons registered in GuiInit()
        super.render(stack, x, y, tick);
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
        return font.getStringWidth(text);
    }

    /**
     * Draws strings from the top-left of the gui
     */
    public void drawStringLine(MatrixStack stack, String title, String[] lines, int offset) {
        font.drawString(stack, title, getLeftMargin(), getTopMargin(), 0x555555);
        int lineCount = 1;
        if (lines == null)
            return;
        for (String text : lines) {
            font.drawString(stack, text, getLeftMargin(), getTopMargin() + offset + 10 * lineCount, 0x555555);
            lineCount++;
        }
    }
}
