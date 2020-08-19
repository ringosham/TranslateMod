package com.ringosham.translationmod.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.ringosham.translationmod.TranslationMod;
import com.ringosham.translationmod.common.ChatUtil;
import com.ringosham.translationmod.translate.SelfTranslate;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.ModList;
import org.lwjgl.glfw.GLFW;

public class TranslateGui extends CommonGui {
    private static final int guiHeight = 125;
    private static final int guiWidth = 225;
    private static final String title;

    static {
        @SuppressWarnings("OptionalGetWithoutIsPresent")
        String modName = ModList.get().getModContainerById(TranslationMod.MODID).get().getModInfo().getDisplayName();
        title = modName + " - by Ringosham";
    }

    private TextFieldWidget headerField;
    private TextFieldWidget messageField;

    public TranslateGui() {
        super(title, guiHeight, guiWidth);
    }

    @Override
    public void render(MatrixStack stack, int x, int y, float tick) {
        super.render(stack, x, y, tick);
        font.drawString(stack, title, getLeftMargin(), getTopMargin(), 0x555555);
        font.drawString(stack, "Enter the command/prefix here (Optional)", getLeftMargin(), getTopMargin() + 10, 0x555555);
        font.drawString(stack, "Enter your message here (Enter to send)", getLeftMargin(), getTopMargin() + 40, 0x555555);
        headerField.render(stack, x, y, tick);
        messageField.render(stack, x, y, tick);
    }

    @Override
    public void init() {
        this.headerField = new TextFieldWidget(font, getLeftMargin(), getYOrigin() + 25, guiWidth - 10, 15, new StringTextComponent(""));
        this.messageField = new TextFieldWidget(font, getLeftMargin(), getYOrigin() + 55, guiWidth - 10, 15, new StringTextComponent(""));
        headerField.setMaxStringLength(25);
        headerField.setCanLoseFocus(true);
        headerField.setEnableBackgroundDrawing(true);
        messageField.setMaxStringLength(75);
        messageField.setCanLoseFocus(true);
        messageField.setEnableBackgroundDrawing(true);
        children.add(headerField);
        children.add(messageField);
        getMinecraft().keyboardListener.enableRepeatEvents(true);
        addButton(new Button(getRightMargin(regularButtonWidth), getYOrigin() + guiHeight - 10 - regularButtonHeight * 2, regularButtonWidth, regularButtonHeight, new StringTextComponent("Settings"),
                (button) -> this.configGui()));
        addButton(new Button(getRightMargin(regularButtonWidth), getYOrigin() + guiHeight - 5 - regularButtonHeight, regularButtonWidth, regularButtonHeight, new StringTextComponent("Close"),
                (button) -> this.exitGui()));
        addButton(new Button(getLeftMargin(), getYOrigin() + guiHeight - 5 - regularButtonHeight, regularButtonWidth, regularButtonHeight, new StringTextComponent("Retranslate"), (button) -> this.retranslateGui()));
        addButton(new Button(getLeftMargin(), getYOrigin() + guiHeight - 10 - regularButtonHeight * 2, regularButtonWidth, regularButtonHeight, new StringTextComponent("Credits"),
                (button) -> {
                    ChatUtil.printCredits();
                    this.exitGui();
                }));
    }

    private void retranslateGui() {
        getMinecraft().keyboardListener.enableRepeatEvents(false);
        getMinecraft().displayGuiScreen(new RetranslateGui());
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
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ENTER && (this.messageField.isFocused() || this.headerField.isFocused())) {
            exitGui();
            Thread translate = new SelfTranslate(this.messageField.getText(), this.headerField.getText());
            translate.start();
            return false;
        }
        if (keyCode == GLFW.GLFW_KEY_E && !this.messageField.isFocused() && !this.headerField.isFocused()) {
            exitGui();
            return false;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
