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

import com.mojang.blaze3d.matrix.MatrixStack;
import com.ringosham.translationmod.TranslationMod;
import com.ringosham.translationmod.common.ChatUtil;
import com.ringosham.translationmod.common.ConfigManager;
import com.ringosham.translationmod.common.Log;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.ModList;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class EngineGui extends CommonGui {
    private static final int guiWidth = 300;
    private static final int guiHeight = 150;
    private static final String title;

    private static final List<ITextComponent> googleTooltip = new ArrayList<>();
    private static final List<ITextComponent> baiduTooltip = new ArrayList<>();

    static {
        @SuppressWarnings("OptionalGetWithoutIsPresent")
        String modName = ModList.get().getModContainerById(TranslationMod.MODID).get().getModInfo().getDisplayName();
        title = modName + " - Engine options";
        googleTooltip.add(new StringTextComponent("By default, you are using the \"free\" version of Google translation"));
        googleTooltip.add(new StringTextComponent("This is the same API the Google translate website is using"));
        googleTooltip.add(new StringTextComponent("However, too many requests and Google will block you for a few minutes"));
        googleTooltip.add(new StringTextComponent("Cloud translation API is the paid version of Google translate"));
        googleTooltip.add(new StringTextComponent("Please check the mod page for details"));
        baiduTooltip.add(new StringTextComponent("If you cannot use Google due to country restrictions,"));
        baiduTooltip.add(new StringTextComponent("Baidu is your second option"));
        baiduTooltip.add(new StringTextComponent("An account is needed to use this API (Phone verification required)"));
        baiduTooltip.add(new StringTextComponent("Free tier only allows 1 request per second"));
        baiduTooltip.add(new StringTextComponent("Paying allows for more requests per second"));
        baiduTooltip.add(new StringTextComponent("Please check the mod page for details"));
    }

    private String engine;
    private TextFieldWidget googleKeyBox;
    private TextFieldWidget baiduKeyBox;
    private TextFieldWidget baiduAppIdBox;

    EngineGui() {
        super(title, guiHeight, guiWidth);
        engine = ConfigManager.config.translationEngine.get();
    }

    @Override
    public void render(MatrixStack stack, int x, int y, float tick) {
        super.render(stack, x, y, tick);
        drawStringLine(stack, title, new String[]{
                "Please choose your translation engine",
                "The mod can only use either of them"
        }, 5);
        switch (engine) {
            case "google":
                font.drawString(stack, "Cloud platform API key", getLeftMargin(), getYOrigin() + 75, 0x555555);
                googleKeyBox.render(stack, x, y, tick);
                font.drawString(stack, "Delete/Leave empty to use the free API", getLeftMargin(), getYOrigin() + 110, 0x555555);
                break;
            case "baidu":
                font.drawString(stack, "Baidu developer App ID", getLeftMargin(), getYOrigin() + 65, 0x555555);
                baiduAppIdBox.render(stack, x, y, tick);
                font.drawString(stack, "Baidu API key", getLeftMargin(), getYOrigin() + 95, 0x555555);
                baiduKeyBox.render(stack, x, y, tick);
                break;
        }
        if (this.buttons.get(0).isHovered())
            func_243308_b(stack, googleTooltip, x, y);
        if (this.buttons.get(1).isHovered())
            func_243308_b(stack, baiduTooltip, x, y);

    }

    @Override
    public void init() {
        getMinecraft().keyboardListener.enableRepeatEvents(true);
        this.googleKeyBox = new TextFieldWidget(this.font, getLeftMargin(), getYOrigin() + 90, guiWidth - 10, 15, new StringTextComponent(ConfigManager.config.googleKey.get()));
        googleKeyBox.setCanLoseFocus(true);
        googleKeyBox.setMaxStringLength(84);
        googleKeyBox.setEnableBackgroundDrawing(true);
        this.children.add(googleKeyBox);
        this.baiduAppIdBox = new TextFieldWidget(this.font, getLeftMargin(), getYOrigin() + 75, guiWidth - 10, 15, new StringTextComponent(ConfigManager.config.baiduAppId.get())) {
            @Override
            public void onClick(double mouseX, double mouseY) {
                baiduKeyBox.setFocused2(false);
                super.onClick(mouseX, mouseY);
            }
        };
        baiduAppIdBox.setCanLoseFocus(true);
        baiduAppIdBox.setMaxStringLength(20);
        baiduAppIdBox.setEnableBackgroundDrawing(true);
        this.children.add(baiduAppIdBox);
        this.baiduKeyBox = new TextFieldWidget(this.font, getLeftMargin(), getYOrigin() + 105, guiWidth - 10, 15, new StringTextComponent(ConfigManager.config.baiduKey.get())) {
            @Override
            public void onClick(double mouseX, double mouseY) {
                super.onClick(mouseX, mouseY);
                baiduAppIdBox.setFocused2(false);
            }
        };
        baiduKeyBox.setCanLoseFocus(true);
        baiduKeyBox.setEnableBackgroundDrawing(true);
        baiduKeyBox.setMaxStringLength(24);
        this.children.add(baiduKeyBox);

        addButton(new Button(getLeftMargin(), getYOrigin() + 40, guiWidth / 2 - 10, regularButtonHeight, new StringTextComponent("Google"), (button) -> {
            button.active = false;
            this.buttons.get(1).active = true;
            engine = "google";
        }));
        addButton(new Button(getRightMargin(guiWidth / 2 - 5), getYOrigin() + 40, guiWidth / 2 - 10, regularButtonHeight, new StringTextComponent("Baidu"), (button) -> {
            button.active = false;
            this.buttons.get(0).active = true;
            engine = "baidu";
        }));
        addButton(new Button(getRightMargin(regularButtonWidth), getYOrigin() + guiHeight - regularButtonHeight - 5, regularButtonWidth, regularButtonHeight, new StringTextComponent("Apply and close"),
                (button) -> this.applyKey()));
        addButton(new Button(getRightMargin(regularButtonWidth) - regularButtonWidth - 5, getYOrigin() + guiHeight - regularButtonHeight - 5, regularButtonWidth, regularButtonHeight, new StringTextComponent("Back"),
                (button) -> this.configGui()));
        switch (engine) {
            case "google":
                this.buttons.get(0).active = false;
                break;
            case "baidu":
                this.buttons.get(1).active = false;
                break;
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifier) {
        if (keyCode == GLFW.GLFW_KEY_E && !this.googleKeyBox.isFocused()) {
            getMinecraft().keyboardListener.enableRepeatEvents(false);
            getMinecraft().displayGuiScreen(null);
            return false;
        }
        return super.keyPressed(keyCode, scanCode, modifier);
    }

    private void configGui() {
        getMinecraft().keyboardListener.enableRepeatEvents(false);
        getMinecraft().displayGuiScreen(new ConfigGui());
    }

    private void applyKey() {
        getMinecraft().keyboardListener.enableRepeatEvents(true);
        ConfigManager.config.googleKey.set(googleKeyBox.getText());
        ConfigManager.config.baiduAppId.set(baiduAppIdBox.getText());
        ConfigManager.config.baiduKey.set(baiduKeyBox.getText());
        ConfigManager.config.translationEngine.set(engine);
        ConfigManager.saveConfig();
        Log.logger.info("Saved engine options");
        ChatUtil.printChatMessage(true, "New translation engine options have been applied.", TextFormatting.WHITE);
        getMinecraft().displayGuiScreen(null);
    }
}
