package com.ringosham.translationmod.gui;

import com.ringosham.translationmod.client.KeyManager;
import com.ringosham.translationmod.common.ChatUtil;
import com.ringosham.translationmod.translate.SelfTranslate;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraftforge.fml.ModList;
import org.lwjgl.glfw.GLFW;

public class TranslateGui extends CommonGui {
    private static final int guiHeight = 125;
    private static final int guiWidth = 225;
    private static final String title;

    static {
        @SuppressWarnings("OptionalGetWithoutIsPresent")
        String modName = ModList.get().getModContainerById("translationmod").get().getModInfo().getDisplayName();
        title = modName + " - by Ringosham";
    }

    private TextFieldWidget headerField;
    private TextFieldWidget messageField;

    public TranslateGui() {
        super(title, guiHeight, guiWidth);
    }

    @Override
    public void render(int x, int y, float tick) {
        super.render(x, y, tick);
        font.drawString(title, getLeftMargin(), getTopMargin(), 0x555555);
        font.drawString("Enter the command/prefix here (Optional)", getLeftMargin(), getTopMargin() + 10, 0x555555);
        font.drawString("Enter your message here (Enter to send)", getLeftMargin(), getTopMargin() + 40, 0x555555);
        headerField.render(x, y, tick);
        messageField.render(x, y, tick);
    }

    @Override
    public void init() {
        this.headerField = new TextFieldWidget(font, getLeftMargin(), getYOrigin() + 25, guiWidth - 10, 15, "");
        this.messageField = new TextFieldWidget(font, getLeftMargin(), getYOrigin() + 55, guiWidth - 10, 15, "");
        headerField.setMaxStringLength(25);
        headerField.setCanLoseFocus(true);
        headerField.setEnableBackgroundDrawing(true);
        messageField.setMaxStringLength(75);
        messageField.setCanLoseFocus(true);
        messageField.setEnableBackgroundDrawing(true);
        getMinecraft().keyboardListener.enableRepeatEvents(true);
        this.buttons.add(new Button(getRightMargin(regularButtonWidth), getYOrigin() + guiHeight - 10 - regularButtonHeight * 2, regularButtonWidth, regularButtonHeight, "Settings",
                (button) -> this.configGui()));
        this.buttons.add(new Button(getRightMargin(regularButtonWidth), getYOrigin() + guiHeight - 5 - regularButtonHeight, regularButtonWidth, regularButtonHeight, "Close",
                (button) -> this.exitGui()));
        this.buttons.add(new Button(getLeftMargin(), getYOrigin() + guiHeight - 10 - regularButtonHeight * 2, regularButtonWidth, regularButtonHeight, "Credits",
                (button) -> {
                    ChatUtil.printCredits();
                    this.exitGui();
                }));
    }

    private void configGui() {
        getMinecraft().keyboardListener.enableRepeatEvents(false);
        getMinecraft().displayGuiScreen(new ConfigGui());
    }

    private void exitGui() {
        getMinecraft().keyboardListener.enableRepeatEvents(false);
        getMinecraft().displayGuiScreen(null);
    }

    @Override
    public boolean charTyped(char typedChar, int keyCode) {
        this.headerField.charTyped(typedChar, keyCode);
        this.messageField.charTyped(typedChar, keyCode);
        if (keyCode == GLFW.GLFW_KEY_ENTER && (this.messageField.isFocused() || this.headerField.isFocused())) {
            exitGui();
            if (!KeyManager.getInstance().isKeyUsedUp()) {
                Thread translate = new SelfTranslate(this.messageField.getText(), this.headerField.getText());
                translate.start();
            }
            return false;
        }
        if (keyCode == GLFW.GLFW_KEY_E && !this.messageField.isFocused() && !this.headerField.isFocused()) {
            exitGui();
            return false;
        } else
            return super.charTyped(typedChar, keyCode);
    }
}