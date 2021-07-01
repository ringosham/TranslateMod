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

import com.ringosham.translationmod.client.types.Language;
import com.ringosham.translationmod.common.ConfigManager;
import com.ringosham.translationmod.translate.Retranslate;
import net.minecraft.client.gui.GuiButton;

public class LanguageSelectGui extends CommonGui {
    private static final int guiWidth = 400;
    private static final int guiHeight = 200;
    private final ConfigGui config;
    private final int langSelect;
    private final String sender;
    private final String message;
    private LangList langList;

    //Calling from ConfigGui
    LanguageSelectGui(ConfigGui config, int langSelect) {
        super(guiHeight, guiWidth);
        this.config = config;
        this.langSelect = langSelect;
        this.message = null;
        this.sender = null;
    }

    //Calling from RetranslateGui
    LanguageSelectGui(String sender, String message) {
        super(guiHeight, guiWidth);
        this.message = message;
        this.sender = sender;
        this.config = null;
        this.langSelect = -1;
    }

    @Override
    public void drawScreen(int x, int y, float tick) {
        super.drawScreen(x, y, tick);
        fontRendererObj.drawString("%mod_name% - Language select", getLeftMargin(), getTopMargin(), 0x555555);
        langList.drawScreen(x, y, tick);
    }

    @Override
    public void initGui() {
        langList = new LangList(mc, this, guiWidth - 18, guiHeight - 48, getYOrigin() + 15, getYOrigin() + guiHeight - 10 - regularButtonHeight, getLeftMargin(), 15, width, height);
        this.buttonList.add(new GuiButton(0, getRightMargin(regularButtonWidth), getYOrigin() + guiHeight - regularButtonHeight - 5, regularButtonWidth, regularButtonHeight, "Select language"));
        this.buttonList.add(new GuiButton(1, getLeftMargin(), getYOrigin() + guiHeight - regularButtonHeight - 5, regularButtonWidth, regularButtonHeight, "Back"));
    }

    @Override
    public void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 0:
                if (langList.getSelected() != null) {
                    if (config != null)
                        this.selectLanguage(langList.getSelected());
                    else
                        this.retranslate(langList.getSelected());
                }
                break;
            case 1:
                if (config != null)
                    this.selectLanguage(null);
                else
                    mc.displayGuiScreen(new RetranslateGui());
                break;
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void selectLanguage(Language lang) {
        mc.displayGuiScreen(new ConfigGui(config, langSelect, lang));
    }

    private void retranslate(Language source) {
        Thread retranslate = new Retranslate(sender, message, source, ConfigManager.INSTANCE.getTargetLanguage());
        retranslate.start();
        mc.displayGuiScreen(null);
    }
}
