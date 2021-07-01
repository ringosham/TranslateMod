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

import com.ringosham.translationmod.client.LangManager;
import com.ringosham.translationmod.client.types.Language;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.fml.client.GuiScrollingList;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LangList extends GuiScrollingList {
    private final List<Language> langList;
    private final GuiScreen parent;
    private int selectedIndex;

    {
        langList = LangManager.getInstance().getAllLanguages();
        //Sort alphabetically.
        Collections.sort(langList, new Comparator<Language>() {
            @Override
            public int compare(Language o1, Language o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
    }

    public LangList(Minecraft client, GuiScreen parent, int width, int height, int top, int bottom, int left, int entryHeight, int screenWidth, int screenHeight) {
        super(client, width, height, top, bottom, left, entryHeight, screenWidth, screenHeight);
        this.parent = parent;
    }

    @Override
    protected int getSize() {
        return langList.size();
    }

    @Override
    protected void elementClicked(int index, boolean doubleClick) {
        selectedIndex = index;
    }

    @Override
    protected boolean isSelected(int index) {
        return index == selectedIndex;
    }

    @Override
    protected void drawBackground() {
    }

    @Override
    protected void drawSlot(int listIndex, int right, int top, int height, Tessellator var5) {
        Language lang = langList.get(listIndex);
        String langName;
        if (lang.getNameUnicode() != null)
            langName = lang.getName() + " (" + lang.getNameUnicode() + ")";
        else
            langName = lang.getName();
        //Overflow
        if (top < this.top)
            return;
        if (top > this.top + this.listHeight)
            return;
        parent.mc.fontRendererObj.drawString(langName, left + 5, top, 0xffffff);
    }

    public Language getSelected() {
        return langList.get(selectedIndex);
    }
}
