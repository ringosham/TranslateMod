package com.ringosham.translationmod.gui;

import com.google.common.primitives.Ints;
import com.ringosham.translationmod.common.ChatUtil;
import com.ringosham.translationmod.common.ConfigManager;
import com.ringosham.translationmod.common.Log;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class RegexGui extends CommonGui implements GuiYesNoCallback {
    //Regex must not be in conflict of the translated message. Otherwise the mod will stuck in a loop spamming the server.
    private static final String testMessage = "Notch --> English: Hello!";
    private static final int guiWidth = 400;
    private static final int guiHeight = 230;
    private static final List<String> cheatsheet;
    private static final List<List<String>> cheatsheetDesc;
    private static final String regexTest = "https://regexr.com";

    static {
        cheatsheet = new ArrayList<>();
        cheatsheetDesc = new ArrayList<>();
        for (int i = 0; i < 12; i++)
            cheatsheetDesc.add(new ArrayList<String>());
        cheatsheet.add(". - Matches any character");
        cheatsheetDesc.get(0).add("Matches any character");
        cheatsheetDesc.get(0).add("The only exception is the newline character (\\n)");
        cheatsheetDesc.get(0).add("Newlines are not used in chat so it doesn't matter");

        cheatsheet.add("\\w - Matches word");
        cheatsheetDesc.get(1).add("Matches all alphabets (Both capital and small), numbers and underscore");
        cheatsheetDesc.get(1).add("Minecraft usernames are based on words. They are perfect to detect player names");

        cheatsheet.add("\\d - Digit");
        cheatsheetDesc.get(2).add("Matches all numbers");

        cheatsheet.add("[a-g] - Match character in range");
        cheatsheetDesc.get(3).add("Matches any characters in tis specific range");
        cheatsheetDesc.get(3).add("Example: [a-g]");
        cheatsheetDesc.get(3).add("✓ " + EnumChatFormatting.GREEN + "a");
        cheatsheetDesc.get(3).add("✓ " + EnumChatFormatting.GREEN + "b");
        cheatsheetDesc.get(3).add("✗ " + EnumChatFormatting.RED + "z");

        cheatsheet.add("* - Matches 0 or more");
        cheatsheetDesc.get(4).add("Matches 0 or more of its character class");
        cheatsheetDesc.get(4).add("Example: N\\w*");
        cheatsheetDesc.get(4).add("✓ " + EnumChatFormatting.GREEN + "N");
        cheatsheetDesc.get(4).add("✓ " + EnumChatFormatting.GREEN + "No");
        cheatsheetDesc.get(4).add("✓ " + EnumChatFormatting.GREEN + "Notch");

        cheatsheet.add("+ - Matches 1 or more");
        cheatsheetDesc.get(5).add("Matches 1 or more of a character/group");
        cheatsheetDesc.get(5).add("Example: N\\w+");
        cheatsheetDesc.get(5).add("✗ " + EnumChatFormatting.RED + "N");
        cheatsheetDesc.get(5).add("✓ " + EnumChatFormatting.GREEN + "No");
        cheatsheetDesc.get(5).add("✓ " + EnumChatFormatting.GREEN + "Notch");

        cheatsheet.add("? - Optional");
        cheatsheetDesc.get(6).add("Exactly as the name suggests");
        cheatsheetDesc.get(6).add("Example: (VIP )?\\w+");
        cheatsheetDesc.get(6).add("✓ " + EnumChatFormatting.GREEN + "VIP PlayerName");
        cheatsheetDesc.get(6).add("✓ " + EnumChatFormatting.GREEN + "PlayerName");

        cheatsheet.add("{2,} - Matches n or more");
        cheatsheetDesc.get(7).add("Matches a group/character n times or more");
        cheatsheetDesc.get(7).add("Add a number after the comma if you want the it match x to y times");
        cheatsheetDesc.get(7).add("Or omit the comma if you want the it match exactly n times");
        cheatsheetDesc.get(7).add("Example: Level \\d{1,3}");
        cheatsheetDesc.get(7).add("✓ " + EnumChatFormatting.GREEN + "Level 1");
        cheatsheetDesc.get(7).add("✓ " + EnumChatFormatting.GREEN + "Level 420");
        cheatsheetDesc.get(7).add("✗ " + EnumChatFormatting.RED + "Level 42069");


        cheatsheet.add("| - Either");
        cheatsheetDesc.get(8).add("Must match either of them, but not both.");
        cheatsheetDesc.get(8).add("Example: (Dead)|(Alive) (\\w+)");
        cheatsheetDesc.get(8).add("✓ " + EnumChatFormatting.GREEN + "Dead PlayerName");
        cheatsheetDesc.get(8).add("✓ " + EnumChatFormatting.GREEN + "Alive PlayerName");
        cheatsheetDesc.get(8).add("✗ " + EnumChatFormatting.RED + "DeadAlive PlayerName");

        cheatsheet.add("() - Group");
        cheatsheetDesc.get(9).add("Think of groups as parentheses like in mathematics");
        cheatsheetDesc.get(9).add("They also have a second function. Capture groups.");
        cheatsheetDesc.get(9).add("By specifying the group number below, the mod can know which group");
        cheatsheetDesc.get(9).add(" contains the player's username");

        cheatsheet.add("\\ - Escape character");
        cheatsheetDesc.get(10).add("If you need to capture special characters mentioned in this list,");
        cheatsheetDesc.get(10).add(" you will need to add an extra backslash to escape them.");
        cheatsheetDesc.get(10).add("Correct:" + EnumChatFormatting.GREEN + " \\(VIP\\) \\w+");
        cheatsheetDesc.get(10).add("Wrong:" + EnumChatFormatting.RED + " (VIP) \\w+");
    }
    private int index;
    private LinkedList<String> regexes = new LinkedList<>();
    private LinkedList<Integer> groups = new LinkedList<>();
    private GuiTextField regexTextbox;
    private GuiTextField groupTextBox;

    {
        regexes.addAll(ConfigManager.INSTANCE.getRegexList());
        groups.addAll(ConfigManager.INSTANCE.getGroupList());
        index = regexes.size() - 1;
    }

    RegexGui() {
        super(guiHeight, guiWidth);
    }

    @Override
    public void drawScreen(int x, int y, float tick) {
        super.drawScreen(x, y, tick);
        fontRendererObj.drawString("%mod_name% - Regex list", getLeftMargin(), getTopMargin(), 0x555555);
        fontRendererObj.drawString("Regex(Regular expression) are search patterns used to detect messages.", getLeftMargin(), getYOrigin() + 15, 0x555555);
        fontRendererObj.drawString("You can use this website to test your regex.", getLeftMargin(), getYOrigin() + 25, 0x555555);
        fontRendererObj.drawString("Cheatsheet: (Hover your mouse to see explanation)", getLeftMargin(), getYOrigin() + 35, 0x555555);
        fontRendererObj.drawString("TIP: Combine classes and quantifiers together to match several characters", getLeftMargin(), getYOrigin() + guiHeight - 40, 0x555555);
        fontRendererObj.drawString((index + 1) + " of " + Math.max(index + 1, regexes.size()), getLeftMargin() + 15 + smallButtonLength * 2, getYOrigin() + guiHeight - regularButtonHeight, 0x555555);
        String regex = regexTextbox.getText();
        int group = groupTextBox.getText().isEmpty() ? -1 : Integer.parseInt(groupTextBox.getText());
        if (validateRegex(regex)) {
            if (!isRegexConflict(regex)) {
                int groupCount = countGroups(regex);
                if (groupCount == 0)
                    fontRendererObj.drawString(EnumChatFormatting.YELLOW + "Regex valid, but it needs at least 1 group to detect player names", getLeftMargin(), getYOrigin() + guiHeight - 120, 0x555555);
                else
                    fontRendererObj.drawString(EnumChatFormatting.GREEN + "Regex valid! The regex should stop at before the message content", getLeftMargin(), getYOrigin() + guiHeight - 120, 0x555555);
                fontRendererObj.drawString("Possible match: " + findMatch(getChatLog(), regex), getLeftMargin(), getYOrigin() + guiHeight - 110, 0x555555);
                if (groupCount > 0)
                    fontRendererObj.drawString("Group number: (1 - " + groupCount + ")", getLeftMargin(), getYOrigin() + guiHeight - 80, 0x555555);
                else
                    fontRendererObj.drawString("Group number: (?)", getLeftMargin(), getYOrigin() + guiHeight - 80, 0x555555);
                fontRendererObj.drawString("Matching username: " + matchUsername(findMatch(getChatLog(), regex), regex, group), getLeftMargin(), getYOrigin() + guiHeight - 70, 0x555555);
            } else {
                fontRendererObj.drawString(EnumChatFormatting.RED + "Regex conflict with the mod messages! Please be more specific", getLeftMargin(), getYOrigin() + guiHeight - 120, 0x555555);
                fontRendererObj.drawString("Possible match: ---", getLeftMargin(), getYOrigin() + guiHeight - 110, 0x555555);
                fontRendererObj.drawString("Matching username: ---", getLeftMargin(), getYOrigin() + guiHeight - 70, 0x555555);
                fontRendererObj.drawString("Group number: (?)", getLeftMargin(), getYOrigin() + guiHeight - 80, 0x555555);
            }
        } else {
            fontRendererObj.drawString(EnumChatFormatting.RED + "Regex invalid! Please check your syntax", getLeftMargin(), getYOrigin() + guiHeight - 120, 0x555555);
            fontRendererObj.drawString("Possible match: ---", getLeftMargin(), getYOrigin() + guiHeight - 110, 0x555555);
            fontRendererObj.drawString("Matching username: ---", getLeftMargin(), getYOrigin() + guiHeight - 70, 0x555555);
            fontRendererObj.drawString("Group number: (?)", getLeftMargin(), getYOrigin() + guiHeight - 80, 0x555555);
        }
        regexTextbox.drawTextBox();
        groupTextBox.drawTextBox();
        //Draw tooltips
        for (int i = 5; i < this.buttonList.size(); i++) {
            HoveringText button = (HoveringText) this.buttonList.get(i);
            if (button.isMouseOver())
                drawHoveringText(button.getHoverText(), x, y);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initGui() {
        regexTextbox = new GuiTextField(this.fontRendererObj, getLeftMargin(), getYOrigin() + guiHeight - 100, guiWidth - 10, 15);
        regexTextbox.setCanLoseFocus(true);
        regexTextbox.setMaxStringLength(200);
        regexTextbox.setEnableBackgroundDrawing(true);
        regexTextbox.setText(regexes.get(index));
        regexTextbox.setFocused(true);
        groupTextBox = new GuiTextField(this.fontRendererObj, getLeftMargin(), getYOrigin() + guiHeight - 60, guiWidth - 10, 15);
        groupTextBox.setCanLoseFocus(true);
        groupTextBox.setMaxStringLength(10);
        groupTextBox.setEnableBackgroundDrawing(true);
        groupTextBox.setText(Integer.toString(groups.get(index)));
        Keyboard.enableRepeatEvents(true);
        this.buttonList.add(new TextButton(0, getRightMargin(150), getYOrigin() + 25, getTextWidth(regexTest), regexTest, 0x0000aa));
        this.buttonList.add(new GuiButton(1, getLeftMargin() + 5 + smallButtonLength, getYOrigin() + guiHeight - 5 - regularButtonHeight, smallButtonLength, smallButtonLength, "+"));
        this.buttonList.add(new GuiButton(2, getRightMargin(regularButtonWidth), getYOrigin() + guiHeight - 5 - regularButtonHeight, regularButtonWidth, regularButtonHeight, "Save and close"));
        this.buttonList.add(new GuiButton(3, getLeftMargin(), getYOrigin() + guiHeight - 5 - regularButtonHeight, smallButtonLength, smallButtonLength, "<"));
        this.buttonList.add(new GuiButton(4, getRightMargin(regularButtonWidth) - 5 - regularButtonWidth, getYOrigin() + guiHeight - 5 - regularButtonHeight, regularButtonWidth, regularButtonHeight, "Reset to default"));
        //Needs to be cleared since resizing the window calls initGui() again
        this.buttonList.add(new HoveringText(5, getLeftMargin(), getYOrigin() + 45, cheatsheet.get(0), cheatsheetDesc.get(0)));
        this.buttonList.add(new HoveringText(6, getLeftMargin(), getYOrigin() + 55, cheatsheet.get(1), cheatsheetDesc.get(1)));
        this.buttonList.add(new HoveringText(7, getLeftMargin(), getYOrigin() + 65, cheatsheet.get(2), cheatsheetDesc.get(2)));
        this.buttonList.add(new HoveringText(8, getLeftMargin(), getYOrigin() + 75, cheatsheet.get(3), cheatsheetDesc.get(3)));
        this.buttonList.add(new HoveringText(9, getLeftMargin(), getYOrigin() + 85, cheatsheet.get(4), cheatsheetDesc.get(4)));
        this.buttonList.add(new HoveringText(10, getLeftMargin(), getYOrigin() + 95, cheatsheet.get(5), cheatsheetDesc.get(5)));
        this.buttonList.add(new HoveringText(11, getLeftMargin() + 210, getYOrigin() + 45, cheatsheet.get(6), cheatsheetDesc.get(6)));
        this.buttonList.add(new HoveringText(12, getLeftMargin() + 210, getYOrigin() + 55, cheatsheet.get(7), cheatsheetDesc.get(7)));
        this.buttonList.add(new HoveringText(13, getLeftMargin() + 210, getYOrigin() + 65, cheatsheet.get(8), cheatsheetDesc.get(8)));
        this.buttonList.add(new HoveringText(14, getLeftMargin() + 210, getYOrigin() + 75, cheatsheet.get(9), cheatsheetDesc.get(9)));
        this.buttonList.add(new HoveringText(15, getLeftMargin() + 210, getYOrigin() + 85, cheatsheet.get(10), cheatsheetDesc.get(10)));
    }

    @Override
    public void actionPerformed(GuiButton button) {
        Keyboard.enableRepeatEvents(false);
        switch (button.id) {
            case 0:
                mc.displayGuiScreen(new GuiConfirmOpenLink(this, regexTest, 0, false));
                break;
            case 1:
                //Add/next regex
                regexes.set(index, regexTextbox.getText());
                if (groupTextBox.getText().trim().isEmpty())
                    groups.set(index, 0);
                else
                    groups.set(index, Integer.parseInt(groupTextBox.getText()));
                index++;
                if (index == regexes.size()) {
                    button.enabled = false;
                    regexes.add("");
                    groups.add(1);
                    regexTextbox.setText("");
                    groupTextBox.setText("1");
                } else {
                    regexTextbox.setText(regexes.get(index));
                    groupTextBox.setText(groups.get(index).toString());
                    button.displayString = ">";
                    button.enabled = true;
                }
                if (index >= regexes.size() - 1)
                    button.displayString = "+";
                ((GuiButton) this.buttonList.get(3)).enabled = true;
                regexTextbox.setFocused(true);
                regexTextbox.setCursorPositionEnd();
                Keyboard.enableRepeatEvents(true);
                break;
            case 2:
                //Save and close
                regexes.set(index, regexTextbox.getText());
                if (groupTextBox.getText().trim().isEmpty())
                    groups.set(index, 0);
                else
                    groups.set(index, Integer.parseInt(groupTextBox.getText()));
                applySettings();
                mc.displayGuiScreen(null);
                break;
            case 3:
                //Previous regex
                //Discard changes if the textboxes are empty.
                if (regexTextbox.getText().trim().isEmpty() || groupTextBox.getText().isEmpty()) {
                    regexes.remove(index);
                    groups.remove(index);
                } else {
                    regexes.set(index, regexTextbox.getText());
                    groups.set(index, Integer.parseInt(groupTextBox.getText()));
                }
                index--;
                if (index == 0)
                    button.enabled = false;
                if (regexes.size() - 1 == index)
                    ((GuiButton) this.buttonList.get(1)).displayString = "+";
                else
                    ((GuiButton) this.buttonList.get(1)).displayString = ">";
                ((GuiButton) this.buttonList.get(1)).enabled = true;
                regexTextbox.setText(regexes.get(index));
                groupTextBox.setText(groups.get(index).toString());
                regexTextbox.setFocused(true);
                regexTextbox.setCursorPositionEnd();
                Keyboard.enableRepeatEvents(true);
                break;
            case 4:
                ((GuiButton) (this.buttonList.get(3))).enabled = true;
                ((GuiButton) (this.buttonList.get(1))).displayString = "+";
                regexes.clear();
                regexes.addAll(Arrays.asList(ConfigManager.defaultRegex));
                groups.clear();
                groups.addAll(Ints.asList(ConfigManager.defaultGroups));
                index = regexes.size() - 1;
                regexTextbox.setText(regexes.get(index));
                groupTextBox.setText(groups.get(index).toString());
                regexTextbox.setFocused(true);
                regexTextbox.setCursorPositionEnd();
                Keyboard.enableRepeatEvents(true);
                break;
        }
    }

    @Override
    public void keyTyped(char typedchar, int keycode) {
        this.regexTextbox.textboxKeyTyped(typedchar, keycode);
        if (this.groupTextBox.isFocused()) {
            if ((typedchar >= 48 && typedchar <= 57) || typedchar == 8)
                //No group 0 allowed.
                if (this.groupTextBox.getText().isEmpty() && typedchar != 48)
                    this.groupTextBox.textboxKeyTyped(typedchar, keycode);
                else if (!this.groupTextBox.getText().isEmpty())
                    this.groupTextBox.textboxKeyTyped(typedchar, keycode);
        }
        if (keycode == Keyboard.KEY_E && !this.regexTextbox.isFocused())
            mc.displayGuiScreen(null);
        else
            super.keyTyped(typedchar, keycode);
    }

    @Override
    public void mouseClicked(int x, int y, int state) {
        super.mouseClicked(x, y, state);
        this.regexTextbox.mouseClicked(x, y, state);
        this.groupTextBox.mouseClicked(x, y, state);
    }

    @Override
    public void confirmClicked(boolean userClicked, int userResponse) {
        if (userResponse == 0) {
            if (userClicked)
                openLink();
            mc.displayGuiScreen(this);
        }
    }

    private void openLink() {
        if (!Desktop.isDesktopSupported()) {
            Log.logger.error("Cannot open link");
            return;
        }
        Desktop desktop = Desktop.getDesktop();
        try {
            desktop.browse(new URI(regexTest));
        } catch (IOException | URISyntaxException e) {
            Log.logger.error("Cannot open link");
        }

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
    private List<String> getChatLog() {
        //Chat log is a private field.
        List<ChatLine> fullChatLog = ObfuscationReflectionHelper.getPrivateValue(GuiNewChat.class, Minecraft.getMinecraft().ingameGUI.getChatGUI(), "field_146252_h");
        //For 1.7.10 debug use.
        //List<ChatLine> fullChatLog = ObfuscationReflectionHelper.getPrivateValue(GuiNewChat.class, Minecraft.getMinecraft().ingameGUI.getChatGUI(), "chatLines");
        List<String> chatLog = new ArrayList<>();
        for (int i = 0; i < Math.min(fullChatLog.size(), 20); i++)
            chatLog.add(fullChatLog.get(i).getChatComponent().getUnformattedText().replaceAll("§(.)", ""));
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
            String matchMessage = EnumChatFormatting.GREEN + matcher.group(0) + EnumChatFormatting.DARK_GRAY + message.replace(matcher.group(0), "");
            String shorten = matchMessage;
            for (int i = getTextWidth(matchMessage); i > 120; i--) {
                shorten = shorten.substring(0, matchMessage.length() - 1);
            }
            shorten = shorten + "...";
            return matchMessage.length() < shorten.length() ? matchMessage : shorten;
        }
        return EnumChatFormatting.RED + "No match from chat log :(";
    }

    private String matchUsername(String message, String regex, int group) {
        if (group == -1 || group > countGroups(regex) || message.equals(EnumChatFormatting.RED + "Can't find player username :("))
            return "---";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(message);
        if (!matcher.find())
            return EnumChatFormatting.RED + "Can't find player username :(";
        return matcher.group(group);
    }

    private void applySettings() {
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
        ConfigManager.INSTANCE.setRegexList(regexes);
        ConfigManager.INSTANCE.setGroupList(groups);
        //Let the manager do all the validation
        ConfigManager.INSTANCE.syncConfig();
        ChatUtil.printChatMessage(true, "Regex list applied", EnumChatFormatting.WHITE);
    }

    //Must be inner class due to protected access to drawHoveringText in GuiScreen
    public class HoveringText extends GuiButton {
        private final List<String> hoverText;

        public HoveringText(int buttonId, int x, int y, String buttonText, List<String> hoverText) {
            super(buttonId, x, y, buttonText);
            this.hoverText = hoverText;
            this.height = 10;
            this.width = getTextWidth(buttonText);
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY) {
            GL11.glColor4f(1, 1, 1, 1);
            mc.fontRendererObj.drawString(this.displayString, xPosition, yPosition, 0x555555, false);
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
        }

        List<String> getHoverText() {
            return hoverText;
        }
    }
}
