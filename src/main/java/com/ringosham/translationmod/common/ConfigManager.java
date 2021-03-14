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

package com.ringosham.translationmod.common;

import com.google.common.primitives.Ints;
import com.ringosham.translationmod.client.LangManager;
import com.ringosham.translationmod.client.types.Language;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;


public class ConfigManager {
    public static final ConfigManager INSTANCE = new ConfigManager();
    public static final String[] defaultRegex = {
            "<(\\w+)> ", //Default
            "\\(From (\\w+)\\):( )?", //PM
            "(\\w+) whispers ", //PM
            "(\\[\\S+\\]( )?){0,2}(\\w+)( )?\u00BB( )?", //The Hive, etc.
            "(\\[\\S+\\]( )?){0,2}(\\w+)( )?:( )?(Eye\\[\\d\\] )?", //Shotbow, MineZ, etc.
            //Screw you Mineplex for the most complicated chat system
            "\\d{1,3} (\\w+ )?(\\w+) ", //Mineplex survival games
            "Dead (\\d+ )?(\\w+ )?(\\w+) ", //Other Mineplex games
            "(\\w+) > \\w+ ", //Mineplex PM
            "(\\w+) whispers to you: ", //Default PM
            "(\\(Team\\) )?(\\[\\w+\\] ){1,2}(\\w+): ", //Annihilation
            "(\\w+ )?\\w+: (\\w+) > ", //Pika network. Thanks for the shout out!
            "\\[Lvl \\d+\\] \u25b6 (\\w+: )?(\\w+) > ",
            "\u25b6 \\[\\d+\\] (\\w+: )?(\\w+) > ",
            "\u25b6 (\\w+: )?(\\w+) > ",
            "(\\[\\w+\\])?\\[Level \\d+\\] \\[(\\w+)\\] ",  //Frostcraft
            "(\\w+) tells you: ", //Frostcraft PM
            "\\[(\\w+) -> \\w+\\] ", //Default Bukkit PM? Essential?
            "(\\w+ )?(\\w+-)?(\\w+)(\\*)?(\\+){0,2}:", //Mineyourmind(Specifically the forum members). Thanks for the shout out!
    };
    public static final int[] defaultGroups = {
            1,
            1,
            1,
            3,
            3,
            2,
            3,
            1,
            1,
            3,
            2,
            2,
            2,
            2,
            2,
            1,
            1,
            3
    };
    private static final String[] engines = {"google", "baidu"};
    //In case there are future updates that drastically change how the mod works. This variable would be here to check if the configs are out of date.
    private static final int configMinVersion = 1;
    private Configuration config;
    private Language targetLanguage;
    private Language selfLanguage;
    private Language speakAsLanguage;
    private boolean bold;
    private boolean italic;
    private boolean underline;
    private String color;
    private boolean translateSign;
    private List<String> regexList;
    private List<Integer> groupList;
    private String translationEngine;
    private String googleKey;
    private String baiduKey;
    private String baiduAppId;

    private ConfigManager() {
    }

    public void init(FMLPreInitializationEvent e) {
        config = new Configuration(e.getSuggestedConfigurationFile());
        config.load();
        syncConfig();
    }

    //Sync all config values to the mod. As well as error checks
    public void syncConfig() {
        config.load();
        versionCheck();
        //I always hate how forge sets up their configurations.
        //Seriously, why would you need to retype everything just for accessing properties?
        //What happened to simple getters and setters?
        targetLanguage = LangManager.getInstance().findLanguageFromName(config.getString("targetLanguage", Configuration.CATEGORY_GENERAL, "English", "Target language to translate for the chat"));
        selfLanguage = LangManager.getInstance().findLanguageFromName(config.getString("selfLanguage", Configuration.CATEGORY_GENERAL, "English", "The language the user types"));
        speakAsLanguage = LangManager.getInstance().findLanguageFromName(config.getString("speakAsLanguage", Configuration.CATEGORY_GENERAL, "Japanese", "The language the user wants their message to translate to"));
        bold = config.getBoolean("bold", Configuration.CATEGORY_GENERAL, false, "Bold the translated message");
        italic = config.getBoolean("italic", Configuration.CATEGORY_GENERAL, false, "Italic the translated message");
        underline = config.getBoolean("underline", Configuration.CATEGORY_GENERAL, false, "Underline the translated message");
        color = config.getString("color", Configuration.CATEGORY_GENERAL, "gray", "Changes the color of the translated message");
        translateSign = config.getBoolean("translateSign", Configuration.CATEGORY_GENERAL, true, "Allows translating texts in sign by looking");
        regexList = Arrays.asList(config.getStringList("regexList", Configuration.CATEGORY_GENERAL, defaultRegex, "Your regex list"));
        groupList = Ints.asList(config.get("groupList", Configuration.CATEGORY_GENERAL, defaultGroups, "Your match group number to detect player names").getIntList());
        translationEngine = config.getString("translationEngine", Configuration.CATEGORY_GENERAL, engines[0], "Translation engine used");
        googleKey = config.getString("googleKey", Configuration.CATEGORY_GENERAL, "", "Your Google Cloud translation API key");
        baiduKey = config.getString("baiduKey", Configuration.CATEGORY_GENERAL, "", "Your Baidu translation API key");
        baiduAppId = config.getString("baiduAppId", Configuration.CATEGORY_GENERAL, "", "Your Baidu developer app ID");

        //Validations to prevent dumbasses messing with the mod config through notepad
        //Only string validations are needed. Other primitives would be dealt with by forge
        boolean valid = true;
        if (targetLanguage == null) {
            valid = false;
            setTargetLanguage(LangManager.getInstance().findLanguageFromName("English"));
        }
        if (selfLanguage == null) {
            valid = false;
            setSelfLanguage(LangManager.getInstance().findLanguageFromName("English"));
        }
        if (speakAsLanguage == null) {
            valid = false;
            setSpeakAsLanguage(LangManager.getInstance().findLanguageFromName("Japanese"));
        }

        //Regex validation
        Iterator<String> regexIt = regexList.iterator();
        int index = 0;
        while (regexIt.hasNext()) {
            String regex = regexIt.next();
            try {
                //Apparently Java does not have a method to check if a regex is valid. The only to do this is to catch exceptions.
                Pattern.compile(regex);
            } catch (PatternSyntaxException e) {
                valid = false;
                regexIt.remove();
                groupList.remove(index);
                //-1 is needed as everything in the array is shifted left
                index--;
            }
            //Make sure the group number is not less than 0
            if (groupList.get(index) < 0) {
                valid = false;
                regexIt.remove();
                groupList.remove(index);
                index--;
            }
            index++;
        }

        //Validates the color.
        ArrayList<String> colors = new ArrayList<>(TextFormatting.getValidValues(true, false));
        if (!colors.contains(color)) {
            color = "gray";
            valid = false;
        }
        if (!valid) {
            setRegexList(regexList);
            setGroupList(groupList);
            config.save();
            syncConfig();
        }
        if (!Arrays.asList(engines).contains(translationEngine)) {
            setTranslationEngine(engines[0]);
        }
        config.save();
    }

    private void versionCheck() {
        Property prop = config.get(Configuration.CATEGORY_GENERAL, "configVersion", 0, "Config version check to ensure nothing's outdated");
        int configVersion = prop.getInt();
        //In case there might be any major updates that would break under existing configs, this is here to reset everything.
        if (configMinVersion > configVersion) {
            setTargetLanguage(LangManager.getInstance().findLanguageFromName("English"));
            setSelfLanguage(LangManager.getInstance().findLanguageFromName("English"));
            setSpeakAsLanguage(LangManager.getInstance().findLanguageFromName("Japanese"));
            setBold(false);
            setItalic(false);
            setUnderline(false);
            setTranslateSign(true);
            setColor("gray");
            setRegexList(Arrays.asList(defaultRegex));
            setGroupList(Ints.asList(defaultGroups));
            prop.set(configMinVersion);
            setTranslationEngine(engines[0]);
        }
    }

    /**
     * The target language to translate for all chat messages
     *
     * @return Language
     */
    public Language getTargetLanguage() {
        return targetLanguage;
    }

    public void setTargetLanguage(Language targetLanguage) {
        this.targetLanguage = targetLanguage;
        config.get(Configuration.CATEGORY_GENERAL, "targetLanguage", "English", "Target language to translate for the chat").set(targetLanguage.getName());
        config.save();
    }

    /**
     * The language the player is speaking (If the player wants to talk to other people)
     *
     * @return Language
     */
    public Language getSelfLanguage() {
        return selfLanguage;
    }

    public void setSelfLanguage(Language selfLanguage) {
        this.selfLanguage = selfLanguage;
        config.get(Configuration.CATEGORY_GENERAL, "selfLanguage", "English", "The language the user types").set(selfLanguage.getName());
        config.save();
    }

    /**
     * What the player wants their messages to be translated to
     *
     * @return Language
     */
    public Language getSpeakAsLanguage() {
        return speakAsLanguage;
    }

    public void setSpeakAsLanguage(Language speakAsLanguage) {
        this.speakAsLanguage = speakAsLanguage;
        config.get(Configuration.CATEGORY_GENERAL, "speakAsLanguage", "Japanese", "The language the user wants their message to translate to").set(speakAsLanguage.getName());
        config.save();
    }

    public boolean isBold() {
        return bold;
    }

    public void setBold(boolean bold) {
        this.bold = bold;
        config.get(Configuration.CATEGORY_GENERAL, "bold", false, "Bold the translated message").set(bold);
        config.save();
    }

    public boolean isItalic() {
        return italic;
    }

    public void setItalic(boolean italic) {
        this.italic = italic;
        config.get(Configuration.CATEGORY_GENERAL, "italic", false, "Italic the translated message").set(italic);
        config.save();
    }

    public boolean isUnderline() {
        return underline;
    }

    public void setUnderline(boolean underline) {
        this.underline = underline;
        config.get(Configuration.CATEGORY_GENERAL, "underline", false, "Underline the translated message").set(underline);
        config.save();
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
        config.get(Configuration.CATEGORY_GENERAL, "color", "gray", "Changes the color of the translated message").set(color);
        config.save();
    }

    public boolean isTranslateSign() {
        return translateSign;
    }

    public void setTranslateSign(boolean translateSign) {
        this.translateSign = translateSign;
        config.get(Configuration.CATEGORY_GENERAL, "translateSign", true, "Allows translating texts in sign by looking").set(translateSign);
        config.save();
    }

    public List<String> getRegexList() {
        return regexList;
    }

    public void setRegexList(List<String> regexList) {
        this.regexList = regexList;
        String[] array = regexList.toArray(new String[0]);
        config.get(Configuration.CATEGORY_GENERAL, "regexList", defaultRegex, "Your regex list").set(array);
        config.save();
    }

    public List<Integer> getGroupList() {
        return groupList;
    }

    public void setGroupList(List<Integer> groupList) {
        this.groupList = groupList;
        int[] array = Ints.toArray(groupList);
        config.get(Configuration.CATEGORY_GENERAL, "groupList", defaultGroups, "Your match group number to detect player names").set(array);
        config.save();
    }

    public String getTranslationEngine() {
        return translationEngine;
    }

    public void setTranslationEngine(String translationEngine) {
        this.translationEngine = translationEngine;
        config.get(Configuration.CATEGORY_GENERAL, "translationEngine", translationEngine, "Translation engine used").set(translationEngine);
        config.save();
    }

    public String getGoogleKey() {
        return googleKey;
    }

    public void setGoogleKey(String googleKey) {
        this.googleKey = googleKey;
        config.get(Configuration.CATEGORY_GENERAL, "googleKey", googleKey, "Your Google Cloud translation API key").set(googleKey);
        config.save();
    }

    public String getBaiduKey() {
        return baiduKey;
    }

    public void setBaiduKey(String baiduKey) {
        this.baiduKey = baiduKey;
        config.get(Configuration.CATEGORY_GENERAL, "baiduKey", baiduKey, "Your Baidu translation API key").set(baiduKey);
        config.save();
    }

    public String getBaiduAppId() {
        return baiduAppId;
    }

    public void setBaiduAppId(String baiduAppId) {
        this.baiduAppId = baiduAppId;
        config.get(Configuration.CATEGORY_GENERAL, "baiduAppId", baiduAppId, "Your Baidu developer app ID").set(baiduAppId);
        config.save();
    }

    public void saveConfig() {
        config.save();
        syncConfig();
    }
}
