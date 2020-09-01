package com.ringosham.translationmod.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.ringosham.translationmod.TranslationMod;
import com.ringosham.translationmod.common.ChatUtil;
import com.ringosham.translationmod.common.ConfigManager;
import com.ringosham.translationmod.common.Log;
import net.minecraft.client.gui.screen.ConfirmOpenLinkScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.ModList;
import org.lwjgl.glfw.GLFW;

public class AddKeyGui extends CommonGui {
    private static final int guiWidth = 300;
    private static final int guiHeight = 150;
    private static final String getKeyLink = "https://cloud.google.com/translate/pricing";
    private static final String title;

    static {
        @SuppressWarnings("OptionalGetWithoutIsPresent")
        String modName = ModList.get().getModContainerById(TranslationMod.MODID).get().getModInfo().getDisplayName();
        title = modName + " - Custom key";
    }

    private TextFieldWidget textbox;

    AddKeyGui() {
        super(title, guiHeight, guiWidth);
    }

    @Override
    public void render(MatrixStack stack, int x, int y, float tick) {
        super.render(stack, x, y, tick);
        drawStringLine(stack, title, new String[]{
                "If you are tried of the free API banning you constantly,",
                "you can choose to pay for the translation",
                "This uses Google's cloud translation service",
                "This is charged pay as you go. This is NOT a free service",
                "Go to this website for pricing and more information",
        }, 5);
        textbox.render(stack, x, y, tick);
        font.drawString(stack, "Delete/Leave empty to use the free API", getLeftMargin(), getYOrigin() + 110, 0x555555);
    }

    @Override
    public void init() {
        getMinecraft().keyboardListener.enableRepeatEvents(true);
        this.textbox = new TextFieldWidget(this.font, getLeftMargin(), getYOrigin() + 90, guiWidth - 10, 15, new StringTextComponent(""));
        textbox.setCanLoseFocus(true);
        textbox.setMaxStringLength(84);
        textbox.setEnableBackgroundDrawing(true);
        textbox.setText(ConfigManager.config.userKey.get());
        this.children.add(textbox);
        addButton(new Button(getRightMargin(regularButtonWidth), getYOrigin() + guiHeight - regularButtonHeight - 5, regularButtonWidth, regularButtonHeight, new StringTextComponent("Use key"),
                (button) -> this.applyKey()));
        addButton(new Button(getRightMargin(regularButtonWidth) - regularButtonWidth - 5, getYOrigin() + guiHeight - regularButtonHeight - 5, regularButtonWidth, regularButtonHeight, new StringTextComponent("Back"),
                (button) -> this.configGui()));
        addButton(new TextButton(getLeftMargin(), getYOrigin() + 70, getTextWidth("Click here to go to the website"), new StringTextComponent(TextFormatting.DARK_BLUE + "Click here to go to the website"),
                (button) -> this.openLink(), 0x0000aa));
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifier) {
        if (keyCode == GLFW.GLFW_KEY_E && !this.textbox.isFocused()) {
            getMinecraft().keyboardListener.enableRepeatEvents(false);
            getMinecraft().displayGuiScreen(null);
            return false;
        }
        return super.keyPressed(keyCode, scanCode, modifier);
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
        ChatUtil.printChatMessage(true, "User custom translation key set.", TextFormatting.WHITE);
        getMinecraft().displayGuiScreen(null);
    }
}
