package com.ringosham.translationmod.gui;

import com.ringosham.translationmod.client.LangManager;
import com.ringosham.translationmod.client.models.Language;
import com.ringosham.translationmod.common.ChatUtil;
import com.ringosham.translationmod.common.ConfigManager;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.ModList;

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
    private static final String title;

    static {
        selfTooltip.add("The language you speak in game");
        selfTooltip.add("This will be utilised when you want to translate what you speak");
        speakAsTooltip.add("The language your messages will be translated to.");
        speakAsTooltip.add("After you typed your messages through this mod,");
        speakAsTooltip.add("it will be translated to the language you specified");
        regexTooltip.add("Regex are patterns for the mod to detect chat messages.");
        regexTooltip.add("If you notice the mod doesn't do anything on a server,");
        regexTooltip.add("chances are you need to add one here.");
        @SuppressWarnings("OptionalGetWithoutIsPresent")
        String modName = ModList.get().getModContainerById("translationmod").get().getModInfo().getDisplayName();
        title = modName + " - Settings";
    }

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

    ConfigGui() {
        super(title, guiHeight, guiWidth);
    }

    //Use for passing unsaved configurations between GUIs
    ConfigGui(ConfigGui instance, int langSelect, Language lang) {
        super(title, guiHeight, guiWidth);
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

    @Override
    public void render(int x, int y, float tick) {
        super.render(x, y, tick);
        font.drawString(title, getLeftMargin(), getYOrigin() + 5, 0x555555);
        font.drawString("Regex list:", getLeftMargin(), getYOrigin() + 25, 0x555555);
        font.drawString("Target language:", getLeftMargin(), getYOrigin() + 55, 0x555555);
        font.drawString("Self language:", getLeftMargin(), getYOrigin() + 75, 0x555555);
        font.drawString("Speak as language:", getLeftMargin(), getYOrigin() + 95, 0x555555);
        if (this.buttons.get(2).isHovered())
            renderTooltip(targetTooltip, x, y);
        if (this.buttons.get(3).isHovered())
            renderTooltip(selfTooltip, x, y);
        if (this.buttons.get(4).isHovered())
            renderTooltip(speakAsTooltip, x, y);
        if (this.buttons.get(11).isHovered())
            renderTooltip(regexTooltip, x, y);
    }

    @Override
    public void init() {
        if (!isTransition) {
            color = ConfigManager.config.color.get();
            bold = ConfigManager.config.bold.get();
            italic = ConfigManager.config.italic.get();
            underline = ConfigManager.config.underline.get();
            translateSign = ConfigManager.config.translateSign.get();
            targetLang = LangManager.getInstance().findLanguageFromName(ConfigManager.config.targetLanguage.get());
            selfLang = LangManager.getInstance().findLanguageFromName(ConfigManager.config.selfLanguage.get());
            speakAsLang = LangManager.getInstance().findLanguageFromName(ConfigManager.config.speakAsLanguage.get());
        }
        getMinecraft().keyboardListener.enableRepeatEvents(true);
        addButton(new Button(getLeftMargin(), getYOrigin() + guiHeight - 5 - regularButtonHeight, regularButtonWidth, regularButtonHeight, "Save and close",
                (button) -> this.applySettings()));
        addButton(new Button(getRightMargin(regularButtonWidth), getYOrigin() + guiHeight - 5 - regularButtonHeight, regularButtonWidth, regularButtonHeight, "Reset to default",
                (button) -> this.resetDefault()));
        addButton(new Button(getRightMargin(regularButtonWidth), getYOrigin() + 50, regularButtonWidth, regularButtonHeight, targetLang.getName(),
                (button) -> this.langSelect(0)));
        addButton(new Button(getRightMargin(regularButtonWidth), getYOrigin() + 70, regularButtonWidth, regularButtonHeight, selfLang.getName(),
                (button) -> this.langSelect(1)));
        addButton(new Button(getRightMargin(regularButtonWidth), getYOrigin() + 90, regularButtonWidth, regularButtonHeight, speakAsLang.getName(),
                (button) -> this.langSelect(2)));
        addButton(new Button(getLeftMargin(), getYOrigin() + guiHeight - 15 - regularButtonHeight * 3, regularButtonWidth, regularButtonHeight, translateSign ? TextFormatting.GREEN + "Translate signs" : TextFormatting.RED + "Translate signs",
                (button) -> {
                    translateSign = !translateSign;
                    this.toggleButtonBool(translateSign, button);
                }));
        addButton(new Button(getLeftMargin(), getYOrigin() + guiHeight - 10 - regularButtonHeight * 2, regularButtonWidth, regularButtonHeight, "User key",
                (button) -> this.addKeyGui()));
        addButton(new Button(getRightMargin(regularButtonWidth), getYOrigin() + guiHeight - 10 - regularButtonHeight * 2, regularButtonWidth, regularButtonHeight, TextFormatting.getValueByName(color) + "Message color",
                this::rotateColor));
        addButton(new Button(getLeftMargin() + regularButtonWidth + 10, getYOrigin() + guiHeight - 15 - regularButtonHeight * 3, smallButtonLength, smallButtonLength, bold ? "\u00a7a" + TextFormatting.BOLD + "B" : "\u00a7c" + TextFormatting.BOLD + "B",
                (button) -> {
                    bold = !bold;
                    this.toggleButtonBool(bold, button);
                }));
        addButton(new Button(getLeftMargin() + regularButtonWidth + 10, getYOrigin() + guiHeight - 10 - regularButtonHeight * 2, smallButtonLength, smallButtonLength, italic ? "\u00a7a" + TextFormatting.ITALIC + "I" : "\u00a7c" + TextFormatting.ITALIC + "I",
                (button) -> {
                    italic = !italic;
                    this.toggleButtonBool(italic, button);
                }));
        addButton(new Button(getLeftMargin() + regularButtonWidth + 10, getYOrigin() + guiHeight - 5 - regularButtonHeight, smallButtonLength, smallButtonLength, underline ? "\u00a7a" + TextFormatting.UNDERLINE + "U" : "\u00a7c" + TextFormatting.UNDERLINE + "U",
                (button) -> {
                    underline = !underline;
                    this.toggleButtonBool(underline, button);
                }));
        addButton(new Button(getRightMargin(regularButtonWidth), getYOrigin() + 20, regularButtonWidth, regularButtonHeight, "View / Add",
                (button) -> this.regexGui()));
    }

    private void applySettings() {
        getMinecraft().keyboardListener.enableRepeatEvents(false);
        ConfigManager.config.targetLanguage.set(targetLang.getName());
        ConfigManager.config.selfLanguage.set(selfLang.getName());
        ConfigManager.config.speakAsLanguage.set(speakAsLang.getName());
        ConfigManager.config.color.set(color);
        ConfigManager.config.bold.set(bold);
        ConfigManager.config.italic.set(italic);
        ConfigManager.config.underline.set(underline);
        ConfigManager.config.translateSign.set(translateSign);
        ConfigManager.saveConfig();
        ChatUtil.printChatMessage(true, "Settings applied.", TextFormatting.WHITE);
        exitGui();
    }

    private void exitGui() {
        getMinecraft().keyboardListener.enableRepeatEvents(false);
        getMinecraft().displayGuiScreen(null);
    }

    private void langSelect(int id) {
        getMinecraft().keyboardListener.enableRepeatEvents(false);
        getMinecraft().displayGuiScreen(new LanguageSelectGui(this, id));
    }

    private void toggleButtonBool(boolean state, Button button) {
        getMinecraft().keyboardListener.enableRepeatEvents(false);
        String buttonText = TextFormatting.getTextWithoutFormattingCodes(button.getMessage());
        button.setMessage((state ? TextFormatting.GREEN : TextFormatting.RED) + buttonText);
    }

    private void rotateColor(Button button) {
        getMinecraft().keyboardListener.enableRepeatEvents(false);
        TextFormatting formatColor = TextFormatting.getValueByName(color);
        int colorCode = Objects.requireNonNull(formatColor).getColorIndex();
        colorCode++;
        if (colorCode == 16)
            colorCode = 0;
        TextFormatting newColor = TextFormatting.fromColorIndex(colorCode);
        assert newColor != null;
        color = newColor.getFriendlyName();
        String buttonText = TextFormatting.getTextWithoutFormattingCodes(button.getMessage());
        button.setMessage(newColor + buttonText);
    }

    private void regexGui() {
        getMinecraft().keyboardListener.enableRepeatEvents(true);
        getMinecraft().displayGuiScreen(new RegexGui());
    }

    private void addKeyGui() {
        getMinecraft().keyboardListener.enableRepeatEvents(true);
        getMinecraft().displayGuiScreen(new AddKeyGui());
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
        this.buttons.get(2).setMessage("English");
        this.buttons.get(3).setMessage("English");
        this.buttons.get(4).setMessage("Japanese");
        this.buttons.get(5).setMessage(TextFormatting.GREEN + "Translate signs");
        this.buttons.get(7).setMessage(TextFormatting.getValueByName(color) + "Message color");
        this.buttons.get(8).setMessage(bold ? "\u00a7a" : "\u00a7c" + TextFormatting.BOLD + "B");
        this.buttons.get(9).setMessage(italic ? "\u00a7a" : "\u00a7c" + TextFormatting.ITALIC + "I");
        this.buttons.get(10).setMessage(underline ? "\u00a7a" : "\u00a7c" + TextFormatting.UNDERLINE + "U");
    }
}
