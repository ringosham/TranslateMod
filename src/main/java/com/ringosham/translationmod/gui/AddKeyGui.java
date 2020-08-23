package com.ringosham.translationmod.gui;

import com.ringosham.translationmod.common.ChatUtil;
import com.ringosham.translationmod.common.ConfigManager;
import com.ringosham.translationmod.common.Log;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiConfirmOpenLink;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class AddKeyGui extends CommonGui implements GuiYesNoCallback {
    private static final int guiWidth = 300;
    private static final int guiHeight = 150;
    private static final String getKeyLink = "https://cloud.google.com/translate/pricing";
    private static final String title = "%mod_name% - Custom key";

    private GuiTextField textbox;

    AddKeyGui() {
        super(guiHeight, guiWidth);
    }

    @Override
    public void drawScreen(int x, int y, float tick) {
        super.drawScreen(x, y, tick);
        drawStringLine(title, new String[]{
                "If you are tried of the free API banning you constantly,",
                "you can choose to pay for the translation",
                "This uses Google's cloud translation service",
                "This is charged pay as you go. This is NOT a free service",
                "Go to this website for pricing and more information",
        }, 5);
        textbox.drawTextBox();
    }

    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.textbox = new GuiTextField(this.fontRendererObj, getLeftMargin(), getYOrigin() + 90, guiWidth - 10, 15);
        textbox.setCanLoseFocus(true);
        textbox.setMaxStringLength(84);
        textbox.setEnableBackgroundDrawing(true);
        textbox.setText(ConfigManager.INSTANCE.getUserKey());
        this.buttonList.add(new GuiButton(0, getRightMargin(regularButtonWidth), getYOrigin() + guiHeight - regularButtonHeight - 5, regularButtonWidth, regularButtonHeight, "Use key"));
        this.buttonList.add(new GuiButton(1, getRightMargin(regularButtonWidth) - regularButtonWidth - 5, getYOrigin() + guiHeight - regularButtonHeight - 5, regularButtonWidth, regularButtonHeight, "Back"));
        this.buttonList.add(new TextButton(2, getLeftMargin(), getYOrigin() + 70, getTextWidth("Click here to go to the website"), EnumChatFormatting.DARK_BLUE + "Click here to go to the website", 0x0000aa));
    }

    public void mouseClicked(int x, int y, int state) {
        super.mouseClicked(x, y, state);
        this.textbox.mouseClicked(x, y, state);
    }

    public void keyTyped(char typedChar, int keyCode) {
        this.textbox.textboxKeyTyped(typedChar, keyCode);
        if (keyCode == Keyboard.KEY_E && !this.textbox.isFocused())
            mc.displayGuiScreen(null);
        else
            super.keyTyped(typedChar, keyCode);
    }

    public void actionPerformed(GuiButton button) {
        Keyboard.enableRepeatEvents(false);
        switch (button.id) {
            case 0:
                //This creates lag since the web request is done on the game thread. Since it's only one request it shouldn't affect gameplay too much.
                ConfigManager.INSTANCE.setUserKey(textbox.getText());
                Log.logger.info("Key added/changed. Using new key for translations");
                ChatUtil.printChatMessage(true, "User custom translation key set.", EnumChatFormatting.WHITE);
                mc.displayGuiScreen(null);
                break;
            case 1:
                mc.displayGuiScreen(new ConfigGui());
                break;
            case 2:
                mc.displayGuiScreen(new GuiConfirmOpenLink(this, getKeyLink, 0, false));
                break;
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void confirmClicked(boolean userClicked, int userResponse) {
        if (userResponse == 0) {
            if (userClicked) {
                openLink();
            }
            mc.displayGuiScreen(this);
        }
    }

    //Sorry Linux users. Java simply sucks at opening links on desktop environments.
    //Even Mojang can't find a solution to this.
    private void openLink() {
        if (!Desktop.isDesktopSupported()) {
            Log.logger.error("Cannot open link");
            return;
        }
        Desktop desktop = Desktop.getDesktop();
        try {
            desktop.browse(new URI(getKeyLink));
        } catch (IOException | URISyntaxException e) {
            Log.logger.error("Cannot open link");
        }
    }
}
