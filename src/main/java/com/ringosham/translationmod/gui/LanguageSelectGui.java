package com.ringosham.translationmod.gui;

import com.ringosham.translationmod.TranslationMod;
import com.ringosham.translationmod.client.models.Language;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraftforge.fml.ModList;

public class LanguageSelectGui extends com.ringosham.translationmod.gui.CommonGui {
    private static final int guiWidth = 400;
    private static final int guiHeight = 200;
    private static final String title;

    static {
        @SuppressWarnings("OptionalGetWithoutIsPresent")
        String modName = ModList.get().getModContainerById(TranslationMod.MODID).get().getModInfo().getDisplayName();
        title = modName + " - Language select";
    }

    private final com.ringosham.translationmod.gui.ConfigGui config;
    private final int langSelect;
    private com.ringosham.translationmod.gui.LangList langList;

    LanguageSelectGui(com.ringosham.translationmod.gui.ConfigGui config, int langSelect) {
        super(title, guiHeight, guiWidth);
        this.config = config;
        this.langSelect = langSelect;
    }

    @Override
    public void render(int x, int y, float tick) {
        super.render(x, y, tick);
        font.drawString(title, getLeftMargin(), getTopMargin(), 0x555555);
        langList.render(x, y, tick);
    }

    @Override
    public void init() {
        langList = new LangList(getMinecraft(), font, guiWidth - 18, guiHeight - 48, getYOrigin() + 15, getYOrigin() + guiHeight - 10 - regularButtonHeight, 15);
        langList.setLeftPos(getLeftMargin());
        this.children.add(langList);
        addButton(new Button(getRightMargin(regularButtonWidth), getYOrigin() + guiHeight - regularButtonHeight - 5, regularButtonWidth, regularButtonHeight, "Select language",
                (button) -> {
                    if (langList.getSelected() != null)
                        this.selectLanguage(langList.getSelected().getLang());
                }));
        addButton(new Button(getLeftMargin(), getYOrigin() + guiHeight - regularButtonHeight - 5, regularButtonWidth, regularButtonHeight, "Back",
                (button) -> this.selectLanguage(null)));
    }

    private void selectLanguage(Language lang) {
        getMinecraft().displayGuiScreen(new com.ringosham.translationmod.gui.ConfigGui(config, langSelect, lang));
    }
}
