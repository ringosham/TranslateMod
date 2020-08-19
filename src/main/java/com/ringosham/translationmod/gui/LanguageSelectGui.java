package com.ringosham.translationmod.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.ringosham.translationmod.TranslationMod;
import com.ringosham.translationmod.client.LangManager;
import com.ringosham.translationmod.client.types.Language;
import com.ringosham.translationmod.common.ConfigManager;
import com.ringosham.translationmod.translate.Retranslate;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.ModList;

public class LanguageSelectGui extends CommonGui {
    private static final int guiWidth = 400;
    private static final int guiHeight = 200;
    private static final String title;

    static {
        @SuppressWarnings("OptionalGetWithoutIsPresent")
        String modName = ModList.get().getModContainerById(TranslationMod.MODID).get().getModInfo().getDisplayName();
        title = modName + " - Language select";
    }

    private final ConfigGui config;
    private final int langSelect;
    private final String sender;
    private final String message;
    private LangList langList;

    //Calling from ConfigGui
    LanguageSelectGui(ConfigGui config, int langSelect) {
        super(title, guiHeight, guiWidth);
        this.config = config;
        this.langSelect = langSelect;
        this.message = null;
        this.sender = null;
    }

    //Calling from RetranslateGui
    LanguageSelectGui(String sender, String message) {
        super(title, guiHeight, guiWidth);
        this.message = message;
        this.sender = sender;
        this.config = null;
        this.langSelect = -1;
    }

    @Override
    public void render(MatrixStack stack, int x, int y, float tick) {
        super.render(stack, x, y, tick);
        font.drawString(stack, title, getLeftMargin(), getTopMargin(), 0x555555);
        langList.render(stack, x, y, tick);
    }

    @Override
    public void init() {
        langList = new LangList(getMinecraft(), font, guiWidth - 18, guiHeight - 48, getYOrigin() + 15, getYOrigin() + guiHeight - 10 - regularButtonHeight, 18);
        langList.setLeftPos(getLeftMargin());
        this.children.add(langList);
        addButton(new Button(getRightMargin(regularButtonWidth), getYOrigin() + guiHeight - regularButtonHeight - 5, regularButtonWidth, regularButtonHeight, new StringTextComponent("Select language"),
                (button) -> {
                    if (langList.getSelected() != null) {
                        if (config != null)
                            this.selectLanguage(langList.getSelected().getLang());
                        else
                            this.retranslate(langList.getSelected().getLang());
                    }
                }));
        addButton(new Button(getLeftMargin(), getYOrigin() + guiHeight - regularButtonHeight - 5, regularButtonWidth, regularButtonHeight, new StringTextComponent("Back"),
                (button) -> {
                    if (config != null)
                        this.selectLanguage(null);
                    else
                        getMinecraft().displayGuiScreen(new RetranslateGui());
                }));
    }

    @SuppressWarnings("ConstantConditions")
    private void selectLanguage(Language lang) {
        getMinecraft().displayGuiScreen(new ConfigGui(config, langSelect, lang));
    }

    private void retranslate(Language source) {
        Thread retranslate = new Retranslate(sender, message, source, LangManager.getInstance().findLanguageFromName(ConfigManager.config.targetLanguage.get()));
        retranslate.start();
        getMinecraft().displayGuiScreen(null);
    }
}
