package com.ringosham.translationmod.gui;

import com.ringosham.translationmod.client.LangManager;
import com.ringosham.translationmod.client.models.Language;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.list.ExtendedList;

import java.util.Comparator;
import java.util.List;

public class LangList extends ExtendedList<LangList.LangEntry> {
    private final List<Language> langList;
    private FontRenderer font;

    {
        langList = LangManager.getInstance().getAllLanguages();
        //Sort alphabetically.
        langList.sort(Comparator.comparing(Language::getName));
    }

    public LangList(Minecraft client, FontRenderer font, int width, int height, int top, int bottom, int entryHeight) {
        super(client, width, height, top, bottom, entryHeight);
        this.font = font;
        for (Language lang : langList)
            this.addEntry(new LangEntry(lang));
    }

    @Override
    protected int getScrollbarPosition() {
        return getLeft();
    }

    @Override
    public int getRowWidth() {
        return this.width;
    }

    @Override
    public void renderHoleBackground(int a, int b, int c, int d) {
        //We do not need to render the dirt background
    }

    public class LangEntry extends ExtendedList.AbstractListEntry<LangEntry> {
        private final Language lang;
        private final String langName;

        public LangEntry(Language lang) {
            this.lang = lang;
            if (lang.getNameUnicode() != null)
                this.langName = lang.getName() + " (" + lang.getNameUnicode() + ")";
            else
                this.langName = lang.getName();
        }

        @Override
        public void render(int entryIdx, int top, int left, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean p_194999_5_, float partialTicks) {
            font.setBidiFlag(true);
            if (top + 1 < getBottom() && getTop() < top + 1)
                LangList.this.drawString(font, this.langName, left + 5, top + 1, 16777215);
        }

        //Undocumented parameter names are fun!
        @Override
        public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
            //Copied straight from Minecraft source.
            if (p_mouseClicked_1_ == 0) {
                LangList.this.setSelected(this);
                return true;
            }
            return false;
        }

        public Language getLang() {
            return lang;
        }
    }
}
