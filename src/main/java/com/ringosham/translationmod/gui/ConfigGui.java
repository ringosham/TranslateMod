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

import com.ringosham.translationmod.TranslationMod;
import com.ringosham.translationmod.client.LangManager;
import com.ringosham.translationmod.client.types.Language;
import com.ringosham.translationmod.common.ChatUtil;
import com.ringosham.translationmod.common.ConfigManager;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ConfigGui extends CommonGui {
    private static final int guiWidth = 250;
    private static final int guiHeight = 206;
    private static final String targetTooltip = "The language your chat will be translated to";
    private static final List<String> selfTooltip = new ArrayList<>();
    private static final List<String> speakAsTooltip = new ArrayList<>();
    private static final List<String> regexTooltip = new ArrayList<>();
    private static final List<String> apiKeyTooltip = new ArrayList<>();
    private static final List<String> colorTooltip = new ArrayList<>();
    private static final List<String> boldTooltip = new ArrayList<>();
    private static final List<String> underlineTooltip = new ArrayList<>();
    private static final List<String> italicTooltip = new ArrayList<>();
    private static final List<String> signTooltip = new ArrayList<>();
    //If this instance is between transition between other GUIs
    private boolean isTransition = false;
    private Language targetLang;
    private Language speakAsLang;
    private Language selfLang;
    private String color;
    private boolean bold;
    private boolean italic;
    private boolean underline;
    private boolean translateSign;

    static {
        selfTooltip.add("The language you speak in game");
        selfTooltip.add("This will be utilised when you want to translate what you speak");
        speakAsTooltip.add("The language your messages will be translated to.");
        speakAsTooltip.add("After you typed your messages through this mod,");
        speakAsTooltip.add("it will be translated to the language you specified");
        regexTooltip.add("Regex are patterns for the mod to detect chat messages.");
        regexTooltip.add("If you notice the mod doesn't do anything on a server,");
        regexTooltip.add("chances are you need to add one here.");
        apiKeyTooltip.add("Change your translation engine options and enter your API key");
        colorTooltip.add("Changes the color of the translated message");
        boldTooltip.add("Bolds the translated message");
        italicTooltip.add("Italics the translated message");
        underlineTooltip.add("Underlines the translated message");
        signTooltip.add("Translates signs when you look at them");
    }

    ConfigGui() {
        super(guiHeight, guiWidth);
    }

    //Use for passing unsaved configurations between GUIs
    ConfigGui(ConfigGui instance, int langSelect, Language lang) {
        super(guiHeight, guiWidth);
        this.targetLang = instance.targetLang;
        this.speakAsLang = instance.speakAsLang;
        this.selfLang = instance.selfLang;
        this.color = instance.color;
        this.bold = instance.bold;
        this.italic = instance.italic;
        this.underline = instance.underline;
        this.translateSign = instance.translateSign;
        this.isTransition = true;
        if (lang != null) {
            switch (langSelect) {
                case 0:
                    this.targetLang = lang;
                    break;
                case 1:
                    this.selfLang = lang;
                    break;
                case 2:
                    this.speakAsLang = lang;
                    break;
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void drawScreen(int x, int y, float tick) {
        super.drawScreen(x, y, tick);
        fontRenderer.drawString(TranslationMod.MOD_NAME + " - Settings", getLeftMargin(), getYOrigin() + 5, 0x555555);
        fontRenderer.drawString("Regex list:", getLeftMargin(), getYOrigin() + 25, 0x555555);
        fontRenderer.drawString("Target language:", getLeftMargin(), getYOrigin() + 55, 0x555555);
        fontRenderer.drawString("Self language:", getLeftMargin(), getYOrigin() + 75, 0x555555);
        fontRenderer.drawString("Speak as language:", getLeftMargin(), getYOrigin() + 95, 0x555555);
        fontRenderer.drawString("Preview: ", getLeftMargin(), getYOrigin() + 115, 0x555555);
        Style previewStyle = new Style().setColor(TextFormatting.getValueByName(color))
                .setBold(bold)
                .setItalic(italic)
                .setUnderlined(underline);
        fontRenderer.drawString(previewStyle.getFormattingCode() + "Notch --> English: Hello!", getLeftMargin() + 45, getYOrigin() + 115, 0);
        //Target language
        if (this.buttonList.get(2).isMouseOver())
            drawHoveringText(targetTooltip, x, y);
        //Self language
        if (this.buttonList.get(3).isMouseOver())
            drawHoveringText(selfTooltip, x, y);
        //Speak as language
        if (this.buttonList.get(4).isMouseOver())
            drawHoveringText(speakAsTooltip, x, y);
        //Regex list
        if (this.buttonList.get(11).isMouseOver())
            drawHoveringText(regexTooltip, x, y);
        //API key
        if (this.buttonList.get(6).isMouseOver())
            drawHoveringText(apiKeyTooltip, x, y);
        //Translate sign
        if (this.buttonList.get(5).isMouseOver())
            drawHoveringText(signTooltip, x, y);
        //Color message
        if (this.buttonList.get(7).isMouseOver())
            drawHoveringText(colorTooltip, x, y);
        //Bold
        if (this.buttonList.get(8).isMouseOver())
            drawHoveringText(boldTooltip, x, y);
        //Italic
        if (this.buttonList.get(9).isMouseOver())
            drawHoveringText(italicTooltip, x, y);
        //Underline
        if (this.buttonList.get(10).isMouseOver())
            drawHoveringText(underlineTooltip, x, y);
    }

    @Override
    public void initGui() {
        if (!isTransition) {
            color = ConfigManager.INSTANCE.getColor();
            bold = ConfigManager.INSTANCE.isBold();
            italic = ConfigManager.INSTANCE.isItalic();
            underline = ConfigManager.INSTANCE.isUnderline();
            translateSign = ConfigManager.INSTANCE.isTranslateSign();
            targetLang = ConfigManager.INSTANCE.getTargetLanguage();
            selfLang = ConfigManager.INSTANCE.getSelfLanguage();
            speakAsLang = ConfigManager.INSTANCE.getSpeakAsLanguage();
        }
        Keyboard.enableRepeatEvents(true);
        this.buttonList.add(new GuiButton(0, getLeftMargin(), getYOrigin() + guiHeight - 5 - regularButtonHeight, regularButtonWidth, regularButtonHeight, "Save and close"));
        this.buttonList.add(new GuiButton(1, getRightMargin(regularButtonWidth), getYOrigin() + guiHeight - 5 - regularButtonHeight, regularButtonWidth, regularButtonHeight, "Reset to default"));
        this.buttonList.add(new GuiButton(2, getRightMargin(regularButtonWidth), getYOrigin() + 50, regularButtonWidth, regularButtonHeight, targetLang.getName()));
        this.buttonList.add(new GuiButton(3, getRightMargin(regularButtonWidth), getYOrigin() + 70, regularButtonWidth, regularButtonHeight, selfLang.getName()));
        this.buttonList.add(new GuiButton(4, getRightMargin(regularButtonWidth), getYOrigin() + 90, regularButtonWidth, regularButtonHeight, speakAsLang.getName()));
        this.buttonList.add(new GuiButton(5, getLeftMargin(), getYOrigin() + guiHeight - 15 - regularButtonHeight * 3, regularButtonWidth, regularButtonHeight, translateSign ? TextFormatting.GREEN + "Translate signs" : TextFormatting.RED + "Translate signs"));
        this.buttonList.add(new GuiButton(6, getLeftMargin(), getYOrigin() + guiHeight - 10 - regularButtonHeight * 2, regularButtonWidth, regularButtonHeight, "Engine options"));
        this.buttonList.add(new GuiButton(7, getRightMargin(regularButtonWidth), getYOrigin() + guiHeight - 10 - regularButtonHeight * 2, regularButtonWidth, regularButtonHeight, TextFormatting.getValueByName(color) + "Message color"));
        this.buttonList.add(new GuiButton(8, getLeftMargin() + regularButtonWidth + 10, getYOrigin() + guiHeight - 15 - regularButtonHeight * 3, smallButtonLength, smallButtonLength, bold ? "\u00a7a" + TextFormatting.BOLD + "B" : "\u00a7c" + TextFormatting.BOLD + "B"));
        this.buttonList.add(new GuiButton(9, getLeftMargin() + regularButtonWidth + 10, getYOrigin() + guiHeight - 10 - regularButtonHeight * 2, smallButtonLength, smallButtonLength, italic ? "\u00a7a" + TextFormatting.ITALIC + "I" : "\u00a7c" + TextFormatting.ITALIC + "I"));
        this.buttonList.add(new GuiButton(10, getLeftMargin() + regularButtonWidth + 10, getYOrigin() + guiHeight - 5 - regularButtonHeight, smallButtonLength, smallButtonLength, underline ? "\u00a7a" + TextFormatting.UNDERLINE + "U" : "\u00a7c" + TextFormatting.UNDERLINE + "U"));
        this.buttonList.add(new GuiButton(11, getRightMargin(regularButtonWidth), getYOrigin() + 20, regularButtonWidth, regularButtonHeight, "View / Add"));
    }

    @Override
    public void actionPerformed(GuiButton button) {
        Keyboard.enableRepeatEvents(false);
        switch (button.id) {
            case 0:
                applySettings();
                mc.displayGuiScreen(null);
                break;
            case 1:
                resetDefault();
                break;
            case 2:
                mc.displayGuiScreen(new LanguageSelectGui(this, 0));
                break;
            case 3:
                mc.displayGuiScreen(new LanguageSelectGui(this, 1));
                break;
            case 4:
                mc.displayGuiScreen(new LanguageSelectGui(this, 2));
                break;
            case 5:
                if (translateSign) {
                    button.displayString = TextFormatting.RED + "Translate signs";
                } else {
                    button.displayString = TextFormatting.GREEN + "Translate signs";
                }
                translateSign = !translateSign;
                break;
            case 6:
                mc.displayGuiScreen(new EngineGui());
                break;
            case 7:
                TextFormatting formatColor = TextFormatting.getValueByName(color);
                int colorCode = Objects.requireNonNull(formatColor).getColorIndex();
                colorCode++;
                if (colorCode == 16)
                    colorCode = 0;
                TextFormatting newColor = TextFormatting.fromColorIndex(colorCode);
                assert newColor != null;
                color = newColor.getFriendlyName();
                this.buttonList.get(7).displayString = newColor + "Message color";
                break;
            case 8:
                bold = !bold;
                this.buttonList.get(8).displayString = bold ? "\u00a7a" + TextFormatting.BOLD + "B" : "\u00a7c" + TextFormatting.BOLD + "B";
                break;
            case 9:
                italic = !italic;
                this.buttonList.get(9).displayString = italic ? "\u00a7a" + TextFormatting.ITALIC + "I" : "\u00a7c" + TextFormatting.ITALIC + "I";
                break;
            case 10:
                underline = !underline;
                this.buttonList.get(10).displayString = underline ? "\u00a7a" + TextFormatting.UNDERLINE + "U" : "\u00a7c" + TextFormatting.UNDERLINE + "U";
                break;
            case 11:
                mc.displayGuiScreen(new RegexGui());
                break;
        }
    }

    private void applySettings() {
        ConfigManager.INSTANCE.setTargetLanguage(targetLang);
        ConfigManager.INSTANCE.setSelfLanguage(selfLang);
        ConfigManager.INSTANCE.setSpeakAsLanguage(speakAsLang);
        ConfigManager.INSTANCE.setColor(color);
        ConfigManager.INSTANCE.setBold(bold);
        ConfigManager.INSTANCE.setItalic(italic);
        ConfigManager.INSTANCE.setUnderline(underline);
        ConfigManager.INSTANCE.setTranslateSign(translateSign);
        ChatUtil.printChatMessage(true, "Settings applied.", TextFormatting.WHITE);
    }

    private void resetDefault() {
        color = "gray";
        bold = false;
        italic = false;
        underline = false;
        translateSign = true;
        targetLang = LangManager.getInstance().findLanguageFromName("English");
        selfLang = targetLang;
        speakAsLang = LangManager.getInstance().findLanguageFromName("Japanese");
        this.buttonList.get(2).displayString = "English";
        this.buttonList.get(3).displayString = "English";
        this.buttonList.get(4).displayString = "Japanese";
        this.buttonList.get(7).displayString = TextFormatting.getValueByName(color) + "Message color";
        this.buttonList.get(8).displayString = bold ? "\u00a7a" : "\u00a7c" + TextFormatting.BOLD + "B";
        this.buttonList.get(9).displayString = italic ? "\u00a7a" : "\u00a7c" + TextFormatting.ITALIC + "I";
        this.buttonList.get(10).displayString = underline ? "\u00a7a" : "\u00a7c" + TextFormatting.UNDERLINE + "U";
        this.buttonList.get(5).displayString = TextFormatting.GREEN + "Translate signs";
    }
}
