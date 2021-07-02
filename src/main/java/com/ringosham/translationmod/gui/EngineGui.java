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

import com.ringosham.translationmod.TranslationMod;
import com.ringosham.translationmod.common.ChatUtil;
import com.ringosham.translationmod.common.ConfigManager;
import com.ringosham.translationmod.common.Log;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static net.minecraft.client.Minecraft.getMinecraft;

public class EngineGui extends CommonGui {
    private static final int guiWidth = 300;
    private static final int guiHeight = 150;
    private static final String title;

    private static final List<String> googleTooltip = new ArrayList<>();
    private static final List<String> baiduTooltip = new ArrayList<>();

    static {
        title = TranslationMod.MOD_NAME + " - Engine options";
        googleTooltip.add("By default, you are using the \"free\" version of Google translation");
        googleTooltip.add("This is the same API the Google translate website is using");
        googleTooltip.add("However, too many requests and Google will block you for a few minutes");
        googleTooltip.add("Cloud translation API is the paid version of Google translate");
        googleTooltip.add("Please check the mod page for details");
        baiduTooltip.add("If you cannot use Google due to country restrictions,");
        baiduTooltip.add("Baidu is your second option");
        baiduTooltip.add("An account is needed to use this API (Phone verification required)");
        baiduTooltip.add("Free tier only allows 1 request per second");
        baiduTooltip.add("Paying allows for more requests per second");
        baiduTooltip.add("Please check the mod page for details");
    }

    private String engine;
    private GuiTextField googleKeyBox;
    private GuiTextField baiduKeyBox;
    private GuiTextField baiduAppIdBox;

    EngineGui() {
        super(guiHeight, guiWidth);
        engine = ConfigManager.INSTANCE.getTranslationEngine();
    }

    @Override
    public void drawScreen(int x, int y, float tick) {
        super.drawScreen(x, y, tick);
        drawStringLine(title, new String[]{
                "Please choose your translation engine",
                "The mod can only use either of them"
        }, 5);
        switch (engine) {
            case "google":
                fontRenderer.drawString("Cloud platform API key", getLeftMargin(), getYOrigin() + 75, 0x555555);
                googleKeyBox.drawTextBox();
                fontRenderer.drawString("Delete/Leave empty to use the free API", getLeftMargin(), getYOrigin() + 110, 0x555555);
                break;
            case "baidu":
                fontRenderer.drawString("Baidu developer App ID", getLeftMargin(), getYOrigin() + 65, 0x555555);
                baiduAppIdBox.drawTextBox();
                fontRenderer.drawString("Baidu API key", getLeftMargin(), getYOrigin() + 95, 0x555555);
                baiduKeyBox.drawTextBox();
                break;
        }
        if (this.buttonList.get(0).isMouseOver())
            drawHoveringText(googleTooltip, x, y);
        if (this.buttonList.get(1).isMouseOver())
            drawHoveringText(baiduTooltip, x, y);

    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.googleKeyBox = new GuiTextField(0, this.fontRenderer, getLeftMargin(), getYOrigin() + 90, guiWidth - 10, 15);
        googleKeyBox.setCanLoseFocus(true);
        googleKeyBox.setMaxStringLength(84);
        googleKeyBox.setEnableBackgroundDrawing(true);
        googleKeyBox.setText(ConfigManager.INSTANCE.getGoogleKey());
        this.baiduAppIdBox = new GuiTextField(1, this.fontRenderer, getLeftMargin(), getYOrigin() + 75, guiWidth - 10, 15);
        baiduAppIdBox.setCanLoseFocus(true);
        baiduAppIdBox.setMaxStringLength(20);
        baiduAppIdBox.setEnableBackgroundDrawing(true);
        baiduAppIdBox.setText(ConfigManager.INSTANCE.getBaiduAppId());
        this.baiduKeyBox = new GuiTextField(2, this.fontRenderer, getLeftMargin(), getYOrigin() + 105, guiWidth - 10, 15);
        baiduKeyBox.setCanLoseFocus(true);
        baiduKeyBox.setEnableBackgroundDrawing(true);
        baiduKeyBox.setMaxStringLength(24);
        baiduKeyBox.setText(ConfigManager.INSTANCE.getBaiduKey());

        this.buttonList.add(new GuiButton(0, getLeftMargin(), getYOrigin() + 40, guiWidth / 2 - 10, regularButtonHeight, "Google"));
        this.buttonList.add(new GuiButton(1, getRightMargin(guiWidth / 2 - 5), getYOrigin() + 40, guiWidth / 2 - 10, regularButtonHeight, "Baidu"));
        this.buttonList.add(new GuiButton(2, getRightMargin(regularButtonWidth), getYOrigin() + guiHeight - regularButtonHeight - 5, regularButtonWidth, regularButtonHeight, "Apply and close"));
        this.buttonList.add(new GuiButton(3, getRightMargin(regularButtonWidth) - regularButtonWidth - 5, getYOrigin() + guiHeight - regularButtonHeight - 5, regularButtonWidth, regularButtonHeight, "Back"));
        switch (engine) {
            case "google":
                this.buttonList.get(0).enabled = false;
                break;
            case "baidu":
                this.buttonList.get(1).enabled = false;
                break;
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 0:
                engine = "google";
                this.buttonList.get(0).enabled = false;
                this.buttonList.get(1).enabled = true;
                break;
            case 1:
                engine = "baidu";
                this.buttonList.get(0).enabled = true;
                this.buttonList.get(1).enabled = false;
                break;
            case 2:
                applyKey();
                break;
            case 3:
                configGui();
                break;
        }
        super.actionPerformed(button);
    }

    //These methods need to be overridden. Otherwise, Textboxes don't work.
    @Override
    public void mouseClicked(int x, int y, int state) throws IOException {
        super.mouseClicked(x, y, state);
        this.baiduAppIdBox.mouseClicked(x, y, state);
        this.baiduKeyBox.mouseClicked(x, y, state);
        this.googleKeyBox.mouseClicked(x, y, state);
    }

    @Override
    public void keyTyped(char typedchar, int keycode) throws IOException {
        if (this.baiduKeyBox.isFocused())
            this.baiduKeyBox.textboxKeyTyped(typedchar, keycode);
        if (this.baiduAppIdBox.isFocused())
            this.baiduAppIdBox.textboxKeyTyped(typedchar, keycode);
        if (this.googleKeyBox.isFocused())
            this.googleKeyBox.textboxKeyTyped(typedchar, keycode);
        super.keyTyped(typedchar, keycode);
    }

    private void configGui() {
        Keyboard.enableRepeatEvents(false);
        getMinecraft().displayGuiScreen(new ConfigGui());
    }

    private void applyKey() {
        Keyboard.enableRepeatEvents(true);
        ConfigManager.INSTANCE.setGoogleKey(googleKeyBox.getText());
        ConfigManager.INSTANCE.setBaiduAppId(baiduAppIdBox.getText());
        ConfigManager.INSTANCE.setBaiduKey(baiduKeyBox.getText());
        ConfigManager.INSTANCE.setTranslationEngine(engine);
        ConfigManager.INSTANCE.saveConfig();
        Log.logger.info("Saved engine options");
        ChatUtil.printChatMessage(true, "New translation engine options have been applied.", TextFormatting.WHITE);
        getMinecraft().displayGuiScreen(null);
    }
}
