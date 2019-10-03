package com.ringosham.translationmod.gui;

import net.minecraft.client.gui.GuiButton;

public class LanguageSelectGui extends CommonGui {
    private static final int guiWidth = 400;
    private static final int guiHeight = 200;
    private final ConfigGui config;
    private final int langSelect;
    private LangList langList;

    LanguageSelectGui(ConfigGui config, int langSelect) {
        super(guiHeight, guiWidth);
        this.config = config;
        this.langSelect = langSelect;
    }

    @Override
    public void drawScreen(int x, int y, float tick) {
        super.drawScreen(x, y, tick);
        fontRendererObj.drawString("%mod_name% - Language select", getLeftMargin(), getTopMargin(), 0x555555);
        langList.drawScreen(x, y, tick);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initGui() {
        langList = new LangList(mc, this, guiWidth - 18, guiHeight - 48, getYOrigin() + 15, getYOrigin() + guiHeight - 10 - regularButtonHeight, getLeftMargin(), 15);
        this.buttonList.add(new GuiButton(0, getRightMargin(regularButtonWidth), getYOrigin() + guiHeight - regularButtonHeight - 5, regularButtonWidth, regularButtonHeight, "Select language"));
        this.buttonList.add(new GuiButton(1, getLeftMargin(), getYOrigin() + guiHeight - regularButtonHeight - 5, regularButtonWidth, regularButtonHeight, "Back"));
    }

    @Override
    public void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 0:
                mc.displayGuiScreen(new ConfigGui(config, langSelect, langList.getSelected()));
                break;
            case 1:
                mc.displayGuiScreen(new ConfigGui(config, langSelect, null));
                break;
        }
    }
}
