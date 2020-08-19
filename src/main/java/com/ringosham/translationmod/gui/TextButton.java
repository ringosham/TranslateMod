package com.ringosham.translationmod.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import org.lwjgl.opengl.GL11;

public class TextButton extends Button {

    private final int color;

    public TextButton(int x, int y, int width, ITextComponent text, Button.IPressable onPress, int color) {
        super(x, y, width, 10, text, onPress);
        this.color = color;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float tick) {
        GL11.glColor4f(1, 1, 1, 1);
        this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
        Minecraft.getInstance().fontRenderer.drawString(stack, this.getMessage().getUnformattedComponentText(), x, y, color);
    }
}
