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

import com.google.common.primitives.Ints;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.ringosham.translationmod.TranslationMod;
import com.ringosham.translationmod.common.ChatUtil;
import com.ringosham.translationmod.common.ConfigManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.NewChatGui;
import net.minecraft.client.gui.screen.ConfirmOpenLinkScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class RegexGui extends CommonGui {
    //Regex must not be in conflict of the translated message. Otherwise the mod will stuck in a loop spamming the server.
    private static final String testMessage = "Notch --> English: Hello!";
    private static final int guiWidth = 400;
    private static final int guiHeight = 230;
    private static final List<ITextComponent> cheatsheet;
    private static final List<List<ITextComponent>> cheatsheetDesc;
    private static final String regexTest = "https://regexr.com";
    private static final String title;

    static {
        @SuppressWarnings("OptionalGetWithoutIsPresent")
        String modName = ModList.get().getModContainerById(TranslationMod.MODID).get().getModInfo().getDisplayName();
        title = modName + " - Regex list";
    }

    static {
        cheatsheet = new ArrayList<>();
        cheatsheetDesc = new ArrayList<>();
        for (int i = 0; i < 12; i++)
            cheatsheetDesc.add(new ArrayList<>());
        cheatsheet.add(new StringTextComponent(". - Matches any character"));
        cheatsheetDesc.get(0).add(new StringTextComponent("Matches any character"));
        cheatsheetDesc.get(0).add(new StringTextComponent("The only exception is the newline character (\\n)"));
        cheatsheetDesc.get(0).add(new StringTextComponent("Newlines are not used in chat so it doesn't matter"));

        cheatsheet.add(new StringTextComponent("\\w - Matches word"));
        cheatsheetDesc.get(1).add(new StringTextComponent("Matches all alphabets (Both capital and small), numbers and underscore"));
        cheatsheetDesc.get(1).add(new StringTextComponent("Minecraft usernames are based on words. They are perfect to detect player names"));

        cheatsheet.add(new StringTextComponent("\\d - Digit"));
        cheatsheetDesc.get(2).add(new StringTextComponent("Matches all numbers"));

        cheatsheet.add(new StringTextComponent("[a-g] - Match character in range"));
        cheatsheetDesc.get(3).add(new StringTextComponent("Matches any characters in tis specific range"));
        cheatsheetDesc.get(3).add(new StringTextComponent("Example: [a-g]"));
        cheatsheetDesc.get(3).add(new StringTextComponent("\u2713 " + TextFormatting.GREEN + "a"));
        cheatsheetDesc.get(3).add(new StringTextComponent("\u2713 " + TextFormatting.GREEN + "b"));
        cheatsheetDesc.get(3).add(new StringTextComponent("\u2717 " + TextFormatting.RED + "z"));

        cheatsheet.add(new StringTextComponent("* - Matches 0 or more"));
        cheatsheetDesc.get(4).add(new StringTextComponent("Matches 0 or more of its character class"));
        cheatsheetDesc.get(4).add(new StringTextComponent("Example: N\\w*"));
        cheatsheetDesc.get(4).add(new StringTextComponent("\u2713 " + TextFormatting.GREEN + "N"));
        cheatsheetDesc.get(4).add(new StringTextComponent("\u2713 " + TextFormatting.GREEN + "No"));
        cheatsheetDesc.get(4).add(new StringTextComponent("\u2713 " + TextFormatting.GREEN + "Notch"));

        cheatsheet.add(new StringTextComponent("+ - Matches 1 or more"));
        cheatsheetDesc.get(5).add(new StringTextComponent("Matches 1 or more of a character/group"));
        cheatsheetDesc.get(5).add(new StringTextComponent("Example: N\\w+"));
        cheatsheetDesc.get(5).add(new StringTextComponent("\u2717 " + TextFormatting.RED + "N"));
        cheatsheetDesc.get(5).add(new StringTextComponent("\u2713 " + TextFormatting.GREEN + "No"));
        cheatsheetDesc.get(5).add(new StringTextComponent("\u2713 " + TextFormatting.GREEN + "Notch"));

        cheatsheet.add(new StringTextComponent("? - Optional"));
        cheatsheetDesc.get(6).add(new StringTextComponent("Exactly as the name suggests"));
        cheatsheetDesc.get(6).add(new StringTextComponent("Example: (VIP )?\\w+"));
        cheatsheetDesc.get(6).add(new StringTextComponent("\u2713 " + TextFormatting.GREEN + "VIP PlayerName"));
        cheatsheetDesc.get(6).add(new StringTextComponent("\u2713 " + TextFormatting.GREEN + "PlayerName"));

        cheatsheet.add(new StringTextComponent("{2,} - Matches n or more"));
        cheatsheetDesc.get(7).add(new StringTextComponent("Matches a group/character n times or more"));
        cheatsheetDesc.get(7).add(new StringTextComponent("Add a number after the comma if you want the it match x to y times"));
        cheatsheetDesc.get(7).add(new StringTextComponent("Or omit the comma if you want the it match exactly n times"));
        cheatsheetDesc.get(7).add(new StringTextComponent("Example: Level \\d{1,3}"));
        cheatsheetDesc.get(7).add(new StringTextComponent("\u2713 " + TextFormatting.GREEN + "Level 1"));
        cheatsheetDesc.get(7).add(new StringTextComponent("\u2713 " + TextFormatting.GREEN + "Level 420"));
        cheatsheetDesc.get(7).add(new StringTextComponent("\u2717 " + TextFormatting.RED + "Level 42069"));


        cheatsheet.add(new StringTextComponent("| - Either"));
        cheatsheetDesc.get(8).add(new StringTextComponent("Must match either of them, but not both."));
        cheatsheetDesc.get(8).add(new StringTextComponent("Example: (Dead)|(Alive) (\\w+)"));
        cheatsheetDesc.get(8).add(new StringTextComponent("\u2713 " + TextFormatting.GREEN + "Dead PlayerName"));
        cheatsheetDesc.get(8).add(new StringTextComponent("\u2713 " + TextFormatting.GREEN + "Alive PlayerName"));
        cheatsheetDesc.get(8).add(new StringTextComponent("\u2717 " + TextFormatting.RED + "DeadAlive PlayerName"));

        cheatsheet.add(new StringTextComponent("() - Group"));
        cheatsheetDesc.get(9).add(new StringTextComponent("Think of groups as parentheses like in mathematics"));
        cheatsheetDesc.get(9).add(new StringTextComponent("They also have a second function. Capture groups."));
        cheatsheetDesc.get(9).add(new StringTextComponent("By specifying the group number below, the mod can know which group"));
        cheatsheetDesc.get(9).add(new StringTextComponent(" contains the player's username"));

        cheatsheet.add(new StringTextComponent("\\ - Escape character"));
        cheatsheetDesc.get(10).add(new StringTextComponent("If you need to capture special characters mentioned in this list,"));
        cheatsheetDesc.get(10).add(new StringTextComponent(" you will need to add an extra backslash to escape them."));
        cheatsheetDesc.get(10).add(new StringTextComponent("Correct:" + TextFormatting.GREEN + " \\(VIP\\) \\w+"));
        cheatsheetDesc.get(10).add(new StringTextComponent("Wrong:" + TextFormatting.RED + " (VIP) \\w+"));
    }

    private int index;
    private final LinkedList<String> regexes = new LinkedList<>();
    private final LinkedList<Integer> groups = new LinkedList<>();
    private TextFieldWidget regexTextBox;
    private TextFieldWidget groupTextBox;

    {
        regexes.addAll(ConfigManager.config.regexList.get());
        groups.addAll(ConfigManager.config.groupList.get());
        index = regexes.size() - 1;
    }

    RegexGui() {
        super(title, guiHeight, guiWidth);
    }

    @Override
    public void render(MatrixStack stack, int x, int y, float tick) {
        super.render(stack, x, y, tick);
        drawStringLine(stack, title, new String[]{
                "Regex(Regular expression) are search patterns used to detect messages.",
                "You can use this website to test your regex.",
                "Cheatsheet: (Hover your mouse to see explanation)",
        }, 0);
        font.drawString(stack, "TIP: Combine classes and quantifiers together to match several characters", getLeftMargin(), getYOrigin() + guiHeight - 40, 0x555555);
        font.drawString(stack, (index + 1) + " of " + Math.max(index + 1, regexes.size()), getLeftMargin() + 15 + smallButtonLength * 2, getYOrigin() + guiHeight - regularButtonHeight, 0x555555);
        String regex = regexTextBox.getText();
        int group = groupTextBox.getText().isEmpty() ? -1 : Integer.parseInt(groupTextBox.getText());
        if (validateRegex(regex)) {
            if (!isRegexConflict(regex)) {
                int groupCount = countGroups(regex);
                if (groupCount == 0)
                    font.drawString(stack, TextFormatting.YELLOW + "Regex valid, but it needs at least 1 group to detect player names", getLeftMargin(), getYOrigin() + guiHeight - 120, 0x555555);
                else
                    font.drawString(stack, TextFormatting.GREEN + "Regex valid! The regex should stop at before the message content", getLeftMargin(), getYOrigin() + guiHeight - 120, 0x555555);
                font.drawString(stack, "Possible match: " + findMatch(getChatLog(), regex), getLeftMargin(), getYOrigin() + guiHeight - 110, 0x555555);
                if (groupCount > 0)
                    font.drawString(stack, "Group number: (1 - " + groupCount + ")", getLeftMargin(), getYOrigin() + guiHeight - 80, 0x555555);
                else
                    font.drawString(stack, "Group number: (?)", getLeftMargin(), getYOrigin() + guiHeight - 80, 0x555555);
                font.drawString(stack, "Matching username: " + matchUsername(findMatch(getChatLog(), regex), regex, group), getLeftMargin(), getYOrigin() + guiHeight - 70, 0x555555);
            } else {
                font.drawString(stack, TextFormatting.RED + "Regex conflict with the mod messages! Please be more specific", getLeftMargin(), getYOrigin() + guiHeight - 120, 0x555555);
                font.drawString(stack, "Possible match: ---", getLeftMargin(), getYOrigin() + guiHeight - 110, 0x555555);
                font.drawString(stack, "Matching username: ---", getLeftMargin(), getYOrigin() + guiHeight - 70, 0x555555);
                font.drawString(stack, "Group number: (?)", getLeftMargin(), getYOrigin() + guiHeight - 80, 0x555555);
            }
        } else {
            font.drawString(stack, TextFormatting.RED + "Regex invalid! Please check your syntax", getLeftMargin(), getYOrigin() + guiHeight - 120, 0x555555);
            font.drawString(stack, "Possible match: ---", getLeftMargin(), getYOrigin() + guiHeight - 110, 0x555555);
            font.drawString(stack, "Matching username: ---", getLeftMargin(), getYOrigin() + guiHeight - 70, 0x555555);
            font.drawString(stack, "Group number: (?)", getLeftMargin(), getYOrigin() + guiHeight - 80, 0x555555);
        }
        regexTextBox.render(stack, x, y, tick);
        groupTextBox.render(stack, x, y, tick);
        //Draw tooltips
        for (int i = 5; i < this.buttons.size(); i++) {
            HoveringText button = (HoveringText) this.buttons.get(i);
            if (button.isHovered())
                //func_243308_b(MatrixStack, List<ITextComponent>, int, int) -> renderTooltip(...)
                func_243308_b(stack, button.getHoverText(), x, y);
        }
    }

    @Override
    public void init() {
        regexTextBox = new TextFieldWidget(this.font, getLeftMargin(), getYOrigin() + guiHeight - 100, guiWidth - 10, 15, new StringTextComponent(""));
        regexTextBox.setCanLoseFocus(true);
        regexTextBox.setMaxStringLength(200);
        regexTextBox.setEnableBackgroundDrawing(true);
        regexTextBox.setText(regexes.get(index));
        groupTextBox = new TextFieldWidget(this.font, getLeftMargin(), getYOrigin() + guiHeight - 60, guiWidth - 10, 15, new StringTextComponent(""));
        groupTextBox.setCanLoseFocus(true);
        groupTextBox.setMaxStringLength(10);
        groupTextBox.setEnableBackgroundDrawing(true);
        groupTextBox.setText(Integer.toString(groups.get(index)));
        this.children.add(groupTextBox);
        this.children.add(regexTextBox);
        getMinecraft().keyboardListener.enableRepeatEvents(false);
        addButton(new TextButton(getRightMargin(150), getYOrigin() + 25, getTextWidth(regexTest), new StringTextComponent(regexTest),
                (button) -> this.openLink(), 0x0000aa));
        addButton(new Button(getLeftMargin() + 5 + smallButtonLength, getYOrigin() + guiHeight - 5 - regularButtonHeight, smallButtonLength, smallButtonLength, new StringTextComponent("+"),
                this::nextPage));
        addButton(new Button(getRightMargin(regularButtonWidth), getYOrigin() + guiHeight - 5 - regularButtonHeight, regularButtonWidth, regularButtonHeight, new StringTextComponent("Save and close"),
                (button) -> this.applySettings()));
        addButton(new Button(getLeftMargin(), getYOrigin() + guiHeight - 5 - regularButtonHeight, smallButtonLength, smallButtonLength, new StringTextComponent("<"),
                this::previousPage));
        addButton(new Button(getRightMargin(regularButtonWidth) - 5 - regularButtonWidth, getYOrigin() + guiHeight - 5 - regularButtonHeight, regularButtonWidth, regularButtonHeight, new StringTextComponent("Reset to default"),
                (button) -> this.resetDefault()));
        //Needs to be cleared since resizing the window calls initGui() again
        addButton(new HoveringText(getLeftMargin(), getYOrigin() + 45, cheatsheet.get(0), cheatsheetDesc.get(0)));
        addButton(new HoveringText(getLeftMargin(), getYOrigin() + 55, cheatsheet.get(1), cheatsheetDesc.get(1)));
        addButton(new HoveringText(getLeftMargin(), getYOrigin() + 65, cheatsheet.get(2), cheatsheetDesc.get(2)));
        addButton(new HoveringText(getLeftMargin(), getYOrigin() + 75, cheatsheet.get(3), cheatsheetDesc.get(3)));
        addButton(new HoveringText(getLeftMargin(), getYOrigin() + 85, cheatsheet.get(4), cheatsheetDesc.get(4)));
        addButton(new HoveringText(getLeftMargin(), getYOrigin() + 95, cheatsheet.get(5), cheatsheetDesc.get(5)));
        addButton(new HoveringText(getLeftMargin() + 210, getYOrigin() + 45, cheatsheet.get(6), cheatsheetDesc.get(6)));
        addButton(new HoveringText(getLeftMargin() + 210, getYOrigin() + 55, cheatsheet.get(7), cheatsheetDesc.get(7)));
        addButton(new HoveringText(getLeftMargin() + 210, getYOrigin() + 65, cheatsheet.get(8), cheatsheetDesc.get(8)));
        addButton(new HoveringText(getLeftMargin() + 210, getYOrigin() + 75, cheatsheet.get(9), cheatsheetDesc.get(9)));
        addButton(new HoveringText(getLeftMargin() + 210, getYOrigin() + 85, cheatsheet.get(10), cheatsheetDesc.get(10)));
    }

    private void openLink() {
        getMinecraft().keyboardListener.enableRepeatEvents(false);
        getMinecraft().displayGuiScreen(new ConfirmOpenLinkScreen((ConfirmOpen) -> {
            if (ConfirmOpen)
                Util.getOSType().openURI(regexTest);
            getMinecraft().displayGuiScreen(this);
        }, regexTest, false));
        getMinecraft().keyboardListener.enableRepeatEvents(true);
    }

    private void nextPage(Button button) {
        regexes.set(index, regexTextBox.getText());
        if (groupTextBox.getText().trim().isEmpty())
            groups.set(index, 0);
        else
            groups.set(index, Integer.parseInt(groupTextBox.getText()));
        index++;
        if (index == regexes.size()) {
            button.active = false;
            regexes.add("");
            groups.add(1);
            regexTextBox.setText("");
            groupTextBox.setText("1");
        } else {
            regexTextBox.setText(regexes.get(index));
            groupTextBox.setText(groups.get(index).toString());
            button.setMessage(new StringTextComponent(">"));
            button.active = true;
        }
        if (index >= regexes.size() - 1)
            button.setMessage(new StringTextComponent("+"));
        this.buttons.get(3).active = true;
        regexTextBox.setCursorPositionEnd();
        getMinecraft().keyboardListener.enableRepeatEvents(true);
    }

    private void previousPage(Button button) {
        //Discard changes if the textboxes are empty.
        if (regexTextBox.getText().trim().isEmpty() || groupTextBox.getText().isEmpty()) {
            regexes.remove(index);
            groups.remove(index);
        } else {
            regexes.set(index, regexTextBox.getText());
            groups.set(index, Integer.parseInt(groupTextBox.getText()));
        }
        index--;
        if (index == 0)
            button.active = false;
        if (regexes.size() - 1 == index)
            this.buttons.get(1).setMessage(new StringTextComponent("+"));
        else
            this.buttons.get(1).setMessage(new StringTextComponent(">"));
        this.buttons.get(1).active = true;
        regexTextBox.setText(regexes.get(index));
        groupTextBox.setText(groups.get(index).toString());
        regexTextBox.setCursorPositionEnd();
        getMinecraft().keyboardListener.enableRepeatEvents(true);
    }

    private void resetDefault() {
        this.buttons.get(3).active = true;
        this.buttons.get(1).setMessage(new StringTextComponent("+"));
        regexes.clear();
        regexes.addAll(Arrays.asList(ConfigManager.defaultRegex));
        groups.clear();
        groups.addAll(Ints.asList(ConfigManager.defaultGroups));
        index = regexes.size() - 1;
        regexTextBox.setText(regexes.get(index));
        groupTextBox.setText(groups.get(index).toString());
        regexTextBox.setCursorPositionEnd();
        getMinecraft().keyboardListener.enableRepeatEvents(true);
    }

    private void exitGui() {
        getMinecraft().keyboardListener.enableRepeatEvents(false);
        getMinecraft().displayGuiScreen(null);
    }

    @Override
    public boolean charTyped(char typedchar, int keyCode) {
        if (this.groupTextBox.isFocused()) {
            if (typedchar >= '0' && typedchar <= '9')
                //No group 0 allowed.
                if (this.groupTextBox.getText().isEmpty() && typedchar != 48)
                    return super.charTyped(typedchar, keyCode);
                else if (!this.groupTextBox.getText().isEmpty())
                    return super.charTyped(typedchar, keyCode);
            return false;
        }
        return super.charTyped(typedchar, keyCode);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifier) {
        if (keyCode == GLFW.GLFW_KEY_E && !this.regexTextBox.isFocused()) {
            getMinecraft().displayGuiScreen(null);
            return false;
        }
        return super.keyPressed(keyCode, scanCode, modifier);
    }

    private boolean validateRegex(String regex) {
        if (regex == null)
            return false;
        if (regex.trim().isEmpty())
            return false;
        try {
            Pattern.compile(regex);
        } catch (PatternSyntaxException e) {
            return false;
        }
        return true;
    }

    private int countGroups(String regex) {
        Pattern pattern = Pattern.compile(regex);
        //Why is matching even needed... This is stupid.
        Matcher matcher = pattern.matcher("Reality is a shitty game! -Katsuragi Keima");
        return matcher.groupCount();
    }

    //Ensure the regex does not conflict with the translated chat output.
    private boolean isRegexConflict(String regex) {
        if (!regex.contains("^"))
            regex = "^" + regex;
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(testMessage);
        return matcher.find();
    }

    //Gets the chat log of 20 messages for testing regex
    @SuppressWarnings("ConstantConditions")
    private List<String> getChatLog() {
        //Chat log is a private field.
        List<ChatLine<ITextComponent>> fullChatLog = ObfuscationReflectionHelper.getPrivateValue(NewChatGui.class, Minecraft.getInstance().ingameGUI.getChatGUI(), "field_146252_h");
        //For 1.7.10 debug use.
        //List<ChatLine> fullChatLog = ObfuscationReflectionHelper.getPrivateValue(GuiNewChat.class, Minecraft.getInstance().ingameGUI.getChatGUI(), "chatLines");
        List<String> chatLog = new ArrayList<>();
        for (int i = 0; i < Math.min(fullChatLog.size(), 20); i++) {
            //func_238169_a_() --> getITextComponent()
            ITextComponent chatLine = fullChatLog.get(i).getLineString();
            if (chatLine instanceof TranslationTextComponent) {
                TranslationTextComponent ttc = (TranslationTextComponent) chatLine;
                chatLog.add(ttc.getString().replaceAll("§(.)", ""));
            } else {
                chatLog.add(fullChatLog.get(i).getLineString().getUnformattedComponentText().replaceAll("§(.)", ""));
            }
        }
        return chatLog;
    }

    //An indicator to see how much the regex matches the chat message
    private String findMatch(List<String> chatLog, String regex) {
        if (!regex.contains("^"))
            regex = "^" + regex;
        Pattern pattern = Pattern.compile(regex);
        for (String message : chatLog) {
            Matcher matcher = pattern.matcher(message);
            if (!matcher.find())
                continue;
            String matchMessage = TextFormatting.GREEN + matcher.group(0) + TextFormatting.DARK_GRAY + message.replace(matcher.group(0), "");
            String shorten = matchMessage;
            for (int i = getTextWidth(matchMessage); i > 120; i--) {
                shorten = shorten.substring(0, matchMessage.length() - 1);
            }
            shorten = shorten + "...";
            return matchMessage.length() < shorten.length() ? matchMessage : shorten;
        }
        return TextFormatting.RED + "No match from chat log :(";
    }

    private String matchUsername(String message, String regex, int group) {
        if (group == -1 || group > countGroups(regex) || message.equals(TextFormatting.RED + "No match from chat log :("))
            return "---";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(message);
        if (!matcher.find())
            return TextFormatting.RED + "Can't find player username :(";
        return matcher.group(group);
    }

    private void applySettings() {
        regexes.set(index, regexTextBox.getText());
        if (groupTextBox.getText().trim().isEmpty())
            groups.set(index, 0);
        else
            groups.set(index, Integer.parseInt(groupTextBox.getText()));
        for (int i = 0; i < regexes.size(); i++) {
            if (!validateRegex(regexes.get(i)) || isRegexConflict(regexes.get(i))) {
                regexes.remove(i);
                groups.remove(i);
                i--;
                continue;
            }
            int groupCount = countGroups(regexes.get(i));
            if (groupCount < groups.get(i)) {
                regexes.remove(i);
                groups.remove(i);
                i--;
            }
        }
        ConfigManager.config.regexList.set(regexes);
        ConfigManager.config.groupList.set(groups);
        //Let the manager do all the validation
        ConfigManager.validateConfig();
        ChatUtil.printChatMessage(true, "Regex list applied", TextFormatting.WHITE);
        exitGui();
    }

    //Must be inner class due to protected access to renderTooltip in GuiScreen
    public class HoveringText extends Button {
        private final List<ITextComponent> hoverText;

        HoveringText(int x, int y, ITextComponent text, List<ITextComponent> hoverText) {
            super(x, y, getTextWidth(text.getUnformattedComponentText()), 10, text, (button) -> {
            });
            this.hoverText = hoverText;
        }

        @Override
        public void render(MatrixStack stack, int mouseX, int mouseY, float tick) {
            GL11.glColor4f(1, 1, 1, 1);
            font.drawString(stack, this.getMessage().getString(), x, y, 0xFF555555);
            this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
        }

        List<ITextComponent> getHoverText() {
            return hoverText;
        }
    }
}
