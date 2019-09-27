package com.ringosham.translatemod.gui;

import com.ringosham.translatemod.client.KeyManager;
import com.ringosham.translatemod.common.ChatUtil;
import com.ringosham.translatemod.translate.SelfTranslate;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class TranslateGui extends CommonGui {
    private static final int guiHeight = 125;
    private static final int guiWidth = 225;
    private GuiTextField messageField;
    private GuiTextField commandField;

    public TranslateGui() {
        super(guiHeight, guiWidth);
    }

    @Override
    public void drawScreen(int x, int y, float tick) {
        super.drawScreen(x, y, tick);
        fontRenderer.drawString("%mod_name% - by Ringosham", getLeftMargin(), getTopMargin(), 0x555555);
        fontRenderer.drawString("Enter the command/prefix here (Optional)", getLeftMargin(), getTopMargin() + 10, 0x555555);
        fontRenderer.drawString("Enter your message here (Enter to send)", getLeftMargin(), getTopMargin() + 40, 0x555555);
        messageField.drawTextBox();
        commandField.drawTextBox();
        if (this.messageField.isFocused())
            this.commandField.setFocused(false);
        if (this.commandField.isFocused())
            this.messageField.setFocused(false);
    }

    @Override
    public void initGui() {
        this.messageField = new GuiTextField(0, this.fontRenderer, getLeftMargin(), getYOrigin() + 25, guiWidth - 10, 15);
        this.commandField = new GuiTextField(1, this.fontRenderer, getLeftMargin(), getYOrigin() + 55, guiWidth - 10, 15);
        messageField.setMaxStringLength(25);
        messageField.setCanLoseFocus(true);
        messageField.setEnableBackgroundDrawing(true);
        commandField.setMaxStringLength(75);
        commandField.setCanLoseFocus(true);
        commandField.setEnableBackgroundDrawing(true);
        commandField.setFocused(true);
        Keyboard.enableRepeatEvents(true);
        this.buttonList.add(new GuiButton(0, getRightMargin(regularButtonWidth), getYOrigin() + guiHeight - 10 - regularButtonHeight * 2, regularButtonWidth, regularButtonHeight, "Settings"));
        this.buttonList.add(new GuiButton(1, getRightMargin(regularButtonWidth), getYOrigin() + guiHeight - 5 - regularButtonHeight, regularButtonWidth, regularButtonHeight, "Close"));
        this.buttonList.add(new GuiButton(2, getLeftMargin(), getYOrigin() + guiHeight - 10 - regularButtonHeight * 2, regularButtonWidth, regularButtonHeight, "Credits"));
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
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        this.messageField.textboxKeyTyped(typedChar, keyCode);
        this.commandField.textboxKeyTyped(typedChar, keyCode);
        if (keyCode == Keyboard.KEY_RETURN && !this.commandField.getText().trim().isEmpty() && (this.commandField.isFocused() || this.messageField.isFocused())) {
            mc.displayGuiScreen(null);
            if (!KeyManager.getInstance().isOffline() && !KeyManager.getInstance().isKeyUsedUp()) {
                Thread translate = new SelfTranslate(this.messageField.getText(), this.commandField.getText());
                translate.start();
            }
        }
        if (keyCode == Keyboard.KEY_TAB && this.commandField.isFocused() && (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))) {
            this.messageField.setFocused(true);
            this.commandField.setFocused(false);
        } else if (keyCode == Keyboard.KEY_TAB && this.messageField.isFocused()) {
            this.messageField.setFocused(false);
            this.commandField.setFocused(true);
        }
        if (keyCode == Keyboard.KEY_E && !this.commandField.isFocused() && !this.messageField.isFocused())
            mc.displayGuiScreen(null);
        else
            super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void mouseClicked(int x, int y, int state) throws IOException {
        super.mouseClicked(x, y, state);
        this.messageField.mouseClicked(x, y, state);
        this.commandField.mouseClicked(x, y, state);
    }
}
