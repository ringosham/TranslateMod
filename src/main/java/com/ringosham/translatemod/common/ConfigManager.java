package com.ringosham.translatemod.common;

import com.google.common.primitives.Ints;
import com.ringosham.translatemod.client.LangManager;
import com.ringosham.translatemod.client.models.Language;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

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
    private String userKey;
    private List<String> regexList;
    private List<Integer> groupList;

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
        userKey = config.getString("userKey", Configuration.CATEGORY_GENERAL, "", "Your personal translation key");
        regexList = Arrays.asList(config.getStringList("regexList", Configuration.CATEGORY_GENERAL, defaultRegex, "Your regex list"));
        groupList = Ints.asList(config.get("groupList", Configuration.CATEGORY_GENERAL, defaultGroups, "Your match group number to detect player names").getIntList());

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
        /*
           Filter duplicate regex
           Each regex is paired with a group number, so when removing a regex. The group number needs to be removed as well
           The config file does not support map values or tuples, so they are stored as two separate arrays which is an absolute pain to get them working together
           Normally to remove exact duplicates you can use Set<>, but since there are group numbers we can't use that as we need to know the index number in both arrays.
        */
        regexIt = regexList.iterator();
        int iterAIndex = 0;
        int iterBIndex;
        while (regexIt.hasNext()) {
            String regex = regexIt.next();
            Iterator<String> regexIt2 = regexList.iterator();
            for (int i = 0; i <= iterAIndex; i++)
                regexIt2.next();
            iterBIndex = iterAIndex + 1;
            //Shift the iterator by how many times iterator A called next() + 1.
            regexIt2.next();
            while (regexIt2.hasNext()) {
                String regex2 = regexIt2.next();
                //Removes the entry.
                if (regex.equals(regex2)) {
                    valid = false;
                    regexList.remove(iterBIndex);
                    groupList.remove(iterBIndex);
                    //Array shifted by one after removal
                    iterBIndex--;
                }
                iterBIndex++;
            }
            iterAIndex++;
            //Stop at the second to last index
            if (iterAIndex == regexList.size() - 1)
                break;
        }

        //Validates the color.
        @SuppressWarnings("unchecked")
        ArrayList colors = new ArrayList<>(EnumChatFormatting.getValidValues(true, false));
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

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
        config.get(Configuration.CATEGORY_GENERAL, "userKey", "", "Your personal translation key").set(userKey);
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
        config.get("groupList", Configuration.CATEGORY_GENERAL, defaultGroups, "Your match group number to detect player names").set(array);
        config.save();
    }

    public void saveConfig() {
        config.save();
        syncConfig();
    }
}
