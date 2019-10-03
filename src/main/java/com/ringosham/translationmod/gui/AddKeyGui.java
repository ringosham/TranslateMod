package com.ringosham.translationmod.gui;

import com.ringosham.translationmod.client.KeyManager;
import com.ringosham.translationmod.client.YandexClient;
import com.ringosham.translationmod.common.ChatUtil;
import com.ringosham.translationmod.common.ConfigManager;
import com.ringosham.translationmod.common.Log;
import net.minecraft.client.gui.screen.ConfirmOpenLinkScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.Util;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.ModList;
import org.lwjgl.glfw.GLFW;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddKeyGui extends CommonGui {
    private static final Pattern keyPattern = Pattern.compile("trnsl\\.1\\.1\\.\\d{8}T\\d{6}Z\\.[0-9a-f]{16}\\.[0-9a-f]{40}");
    private static final int guiWidth = 256;
    private static final int guiHeight = 150;
    private static final String getKeyLink = "https://tech.yandex.com/translate/";
    private static final String title;

    static {
        @SuppressWarnings("OptionalGetWithoutIsPresent")
        String modName = ModList.get().getModContainerById("translationmod").get().getModInfo().getDisplayName();
        title = modName + " - Custom key";
    }

    private TextFieldWidget textbox;

    AddKeyGui() {
        super(title, guiHeight, guiWidth);
    }

    @Override
    public void render(int x, int y, float tick) {
        super.render(x, y, tick);
        font.drawString(title, getLeftMargin(), getTopMargin(), 0x555555);
        font.drawString("A key is required to use the translation service.", getLeftMargin(), getYOrigin() + 20, 0x555555);
        font.drawString("This mod includes some keys for everyone", getLeftMargin(), getYOrigin() + 30, 0x555555);
        font.drawString("You can use your key here if all keys are used", getLeftMargin(), getYOrigin() + 40, 0x555555);
        font.drawString("The process is free, but requires signing up", getLeftMargin(), getYOrigin() + 50, 0x555555);
        font.drawString("Go to this website to create your key there", getLeftMargin(), getYOrigin() + 60, 0x555555);
        boolean keyValid = checkKey(textbox.getText());
        String keyStatus;
        if (keyValid)
            keyStatus = TextFormatting.GREEN + "This key is valid and usable!";
        else
            keyStatus = TextFormatting.RED + "Invalid key/Key has exceeded daily limits";
        font.drawString(keyStatus, getLeftMargin(), getYOrigin() + 80, 0x555555);
        this.buttons.get(0).active = keyValid;
        textbox.render(x, y, tick);
    }

    private boolean checkKey(String key) {
        Matcher matcher = keyPattern.matcher(key);
        if (!matcher.matches())
            return false;
        YandexClient client = new YandexClient();
        return client.testKey(key);
    }

    @Override
    public void init() {
        getMinecraft().keyboardListener.enableRepeatEvents(true);
        this.textbox = new TextFieldWidget(this.font, getLeftMargin(), getYOrigin() + 90, guiWidth - 10, 15, "");
        textbox.setCanLoseFocus(true);
        textbox.setMaxStringLength(84);
        textbox.setEnableBackgroundDrawing(true);
        textbox.setText(ConfigManager.config.userKey.get());
        this.children.add(textbox);
        addButton(new Button(getRightMargin(regularButtonWidth), getYOrigin() + guiHeight - regularButtonHeight - 5, regularButtonWidth, regularButtonHeight, "Use key",
                (button) -> this.applyKey()));
        addButton(new Button(getRightMargin(regularButtonWidth) - regularButtonWidth - 5, getYOrigin() + guiHeight - regularButtonHeight - 5, regularButtonWidth, regularButtonHeight, "Back",
                (button) -> this.configGui()));
        addButton(new TextButton(getLeftMargin(), getYOrigin() + 70, getTextWidth("Click here to go to the website"), TextFormatting.DARK_BLUE + "Click here to go to the website",
                (button) -> this.openLink()));
    }

    @Override
    public boolean charTyped(char typedChar, int keyCode) {
        this.textbox.charTyped(typedChar, keyCode);
        if (keyCode == GLFW.GLFW_KEY_E && !this.textbox.isFocused())
            getMinecraft().displayGuiScreen(null);
        else
            super.charTyped(typedChar, keyCode);
        return false;
    }

    private void openLink() {
        getMinecraft().keyboardListener.enableRepeatEvents(false);
        getMinecraft().displayGuiScreen(new ConfirmOpenLinkScreen((ConfirmOpen) -> {
            if (ConfirmOpen)
                Util.getOSType().openURI(getKeyLink);
            getMinecraft().displayGuiScreen(this);
        }, getKeyLink, false));
    }

    private void configGui() {
        getMinecraft().keyboardListener.enableRepeatEvents(false);
        getMinecraft().displayGuiScreen(new ConfigGui());
    }

    private void applyKey() {
        getMinecraft().keyboardListener.enableRepeatEvents(true);
        ConfigManager.config.userKey.set(textbox.getText());
        Log.logger.info("Key added/changed. Using new key for translations");
        KeyManager.getInstance().rotateKey();
        ChatUtil.printChatMessage(true, "User custom translation key set.", TextFormatting.WHITE);
        getMinecraft().displayGuiScreen(null);
    }
}
