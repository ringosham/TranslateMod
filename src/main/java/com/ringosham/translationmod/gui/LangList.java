package com.ringosham.translationmod.gui;

import com.ringosham.translationmod.client.LangManager;
import com.ringosham.translationmod.client.models.Language;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.fml.client.GuiScrollingList;

import java.util.Comparator;
import java.util.List;

public class LangList extends GuiScrollingList {
    private final List<Language> langList;
    private GuiScreen parent;
    private int selectedIndex;

    {
        langList = LangManager.getInstance().getAllLanguages();
        //Sort alphabetically.
        langList.sort(Comparator.comparing(Language::getName));
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
        parent.mc.fontRenderer.drawString(langName, left + 5, top, 0xffffff);
    }

    public Language getSelected() {
        return langList.get(selectedIndex);
    }
}
