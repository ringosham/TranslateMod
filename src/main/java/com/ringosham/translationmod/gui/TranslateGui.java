package com.ringosham.translationmod.gui;

import com.ringosham.translationmod.TranslationMod;
import com.ringosham.translationmod.common.ChatUtil;
import com.ringosham.translationmod.translate.SelfTranslate;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class TranslateGui extends CommonGui {
    private static final int guiHeight = 125;
    private static final int guiWidth = 225;
    private GuiTextField headerField;
    private GuiTextField messageField;

    public TranslateGui() {
        super(guiHeight, guiWidth);
    }

    @Override
    public void drawScreen(int x, int y, float tick) {
        super.drawScreen(x, y, tick);
        fontRenderer.drawString(TranslationMod.MOD_NAME + " - by Ringosham", getLeftMargin(), getTopMargin(), 0x555555);
        fontRenderer.drawString("Enter the command/prefix here (Optional)", getLeftMargin(), getTopMargin() + 10, 0x555555);
        fontRenderer.drawString("Enter your message here (Enter to send)", getLeftMargin(), getTopMargin() + 40, 0x555555);
        headerField.drawTextBox();
        messageField.drawTextBox();
        if (this.headerField.isFocused())
            this.messageField.setFocused(false);
        if (this.messageField.isFocused())
            this.headerField.setFocused(false);
    }

    @Override
    public void initGui() {
        this.headerField = new GuiTextField(0, this.fontRenderer, getLeftMargin(), getYOrigin() + 25, guiWidth - 10, 15);
        this.messageField = new GuiTextField(1, this.fontRenderer, getLeftMargin(), getYOrigin() + 55, guiWidth - 10, 15);
        headerField.setMaxStringLength(25);
        headerField.setCanLoseFocus(true);
        headerField.setEnableBackgroundDrawing(true);
        messageField.setMaxStringLength(75);
        messageField.setCanLoseFocus(true);
        messageField.setEnableBackgroundDrawing(true);
        messageField.setFocused(true);
        Keyboard.enableRepeatEvents(true);
        this.buttonList.add(new GuiButton(0, getRightMargin(regularButtonWidth), getYOrigin() + guiHeight - 10 - regularButtonHeight * 2, regularButtonWidth, regularButtonHeight, "Settings"));
        this.buttonList.add(new GuiButton(1, getRightMargin(regularButtonWidth), getYOrigin() + guiHeight - 5 - regularButtonHeight, regularButtonWidth, regularButtonHeight, "Close"));
        this.buttonList.add(new GuiButton(2, getLeftMargin(), getYOrigin() + guiHeight - 10 - regularButtonHeight * 2, regularButtonWidth, regularButtonHeight, "Credits"));
        this.buttonList.add(new GuiButton(3, getLeftMargin(), getYOrigin() + guiHeight - 5 - regularButtonHeight, regularButtonWidth, regularButtonHeight, "Retranslate"));
    }

    @Override
    public void actionPerformed(GuiButton button) {
        Keyboard.enableRepeatEvents(false);
        switch (button.id) {
            case 0:
                mc.displayGuiScreen(new ConfigGui());
                break;
            case 1:
                mc.displayGuiScreen(null);
                break;
            case 2:
                ChatUtil.printCredits();
                mc.displayGuiScreen(null);
                break;
            case 3:
                mc.displayGuiScreen(new RetranslateGui());
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        this.headerField.textboxKeyTyped(typedChar, keyCode);
        this.messageField.textboxKeyTyped(typedChar, keyCode);
        if (keyCode == Keyboard.KEY_RETURN && (this.messageField.isFocused() || this.headerField.isFocused())) {
            mc.displayGuiScreen(null);
            Thread translate = new SelfTranslate(this.messageField.getText(), this.headerField.getText());
            translate.start();
        }
        if (keyCode == Keyboard.KEY_TAB && this.messageField.isFocused() && (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))) {
            this.headerField.setFocused(true);
            this.messageField.setFocused(false);
        } else if (keyCode == Keyboard.KEY_TAB && this.headerField.isFocused()) {
            this.headerField.setFocused(false);
            this.messageField.setFocused(true);
        }
        if (keyCode == Keyboard.KEY_E && !this.messageField.isFocused() && !this.headerField.isFocused())
            mc.displayGuiScreen(null);
        else
            super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void mouseClicked(int x, int y, int state) throws IOException {
        super.mouseClicked(x, y, state);
        this.headerField.mouseClicked(x, y, state);
        this.messageField.mouseClicked(x, y, state);
    }
}
