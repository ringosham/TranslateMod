/*
 * Copyright (C) 2021 Ringosham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.ringosham.translationmod.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import org.lwjgl.opengl.GL11;

public class TextButton extends GuiButton {
    private final int color;

    TextButton(int buttonId, int x, int y, int widthIn, String buttonText, int color) {
        super(buttonId, x, y, widthIn, 10, buttonText);
        this.color = color;
    }

    @Override
    public void drawButton(Minecraft mc, int x, int y) {
        GL11.glColor4f(1, 1, 1, 1);
        this.hovered = x >= this.xPosition && y >= this.yPosition && x < this.xPosition + this.width && y < this.yPosition + this.height;
        mc.fontRendererObj.drawString(this.displayString, xPosition, yPosition, color, false);
    }
}
