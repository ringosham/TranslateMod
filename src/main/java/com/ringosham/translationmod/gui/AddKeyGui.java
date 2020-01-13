package com.ringosham.translationmod.gui;

import com.ringosham.translationmod.client.KeyManager;
import com.ringosham.translationmod.client.YandexClient;
import com.ringosham.translationmod.common.ChatUtil;
import com.ringosham.translationmod.common.ConfigManager;
import com.ringosham.translationmod.common.Log;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiConfirmOpenLink;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddKeyGui extends CommonGui implements GuiYesNoCallback {
    private static final Pattern keyPattern = Pattern.compile("trnsl\\.1\\.1\\.\\d{8}T\\d{6}Z\\.[0-9a-f]{16}\\.[0-9a-f]{40}");
    private static final int guiWidth = 256;
    private static final int guiHeight = 150;
    private static final String getKeyLink = "https://tech.yandex.com/translate/";
    private GuiTextField textbox;
    private String newKey = "";
    private boolean keyValid = false;

    AddKeyGui() {
        super(guiHeight, guiWidth);
    }

    @Override
    public void drawScreen(int x, int y, float tick) {
        super.drawScreen(x, y, tick);
        fontRenderer.drawString("%mod_name% - Custom key", getLeftMargin(), getTopMargin(), 0x555555);
        fontRenderer.drawString("A key is required to use the translation service.", getLeftMargin(), getYOrigin() + 20, 0x555555);
        fontRenderer.drawString("This mod includes some keys for everyone", getLeftMargin(), getYOrigin() + 30, 0x555555);
        fontRenderer.drawString("You can use your key here if all keys are used", getLeftMargin(), getYOrigin() + 40, 0x555555);
        fontRenderer.drawString("The process is free, but requires signing up", getLeftMargin(), getYOrigin() + 50, 0x555555);
        fontRenderer.drawString("Go to this website to create your key there", getLeftMargin(), getYOrigin() + 60, 0x555555);
        if (!newKey.equals(this.textbox.getText()))
            keyValid = checkKey(this.textbox.getText());
        newKey = this.textbox.getText();
        String keyStatus;
        if (keyValid)
            keyStatus = TextFormatting.GREEN + "This key is valid and usable!";
        else
            keyStatus = TextFormatting.RED + "Invalid key/Key has exceeded daily limits";
        fontRenderer.drawString(keyStatus, getLeftMargin(), getYOrigin() + 80, 0x555555);
        this.buttonList.get(0).enabled = keyValid;
        textbox.drawTextBox();
    }

    private boolean checkKey(String key) {
        Matcher matcher = keyPattern.matcher(key);
        if (!matcher.matches())
            return false;
        YandexClient client = new YandexClient();
        return client.testKey(key);
    }

    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.textbox = new GuiTextField(0, this.fontRenderer, getLeftMargin(), getYOrigin() + 90, guiWidth - 10, 15);
        textbox.setCanLoseFocus(true);
        textbox.setMaxStringLength(84);
        textbox.setEnableBackgroundDrawing(true);
        textbox.setText(ConfigManager.INSTANCE.getUserKey());
        this.buttonList.add(new GuiButton(0, getRightMargin(regularButtonWidth), getYOrigin() + guiHeight - regularButtonHeight - 5, regularButtonWidth, regularButtonHeight, "Use key"));
        this.buttonList.add(new GuiButton(1, getRightMargin(regularButtonWidth) - regularButtonWidth - 5, getYOrigin() + guiHeight - regularButtonHeight - 5, regularButtonWidth, regularButtonHeight, "Back"));
        this.buttonList.add(new TextButton(2, getLeftMargin(), getYOrigin() + 70, getTextWidth("Click here to go to the website"), TextFormatting.DARK_BLUE + "Click here to go to the website"));
    }

    public void mouseClicked(int x, int y, int state) throws IOException {
        super.mouseClicked(x, y, state);
        this.textbox.mouseClicked(x, y, state);
    }

    public void keyTyped(char typedChar, int keyCode) throws IOException {
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
                KeyManager.getInstance().rotateKey();
                ChatUtil.printChatMessage(true, "User custom translation key set.", TextFormatting.WHITE);
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
