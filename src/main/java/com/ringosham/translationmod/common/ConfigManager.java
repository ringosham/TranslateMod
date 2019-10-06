package com.ringosham.translationmod.common;

import com.google.common.primitives.Ints;
import com.ringosham.translationmod.TranslationMod;
import com.ringosham.translationmod.client.LangManager;
import com.ringosham.translationmod.client.models.Language;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;


public class ConfigManager {
    public static final ClientConfig config;
    public static final ForgeConfigSpec configSpec;
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

    static {
        final Pair<ClientConfig, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
        configSpec = pair.getRight();
        config = pair.getLeft();
    }

    //Sync all config values to the mod. As well as error checks
    public static void validateConfig() {
        versionCheck();
        //Validations to prevent dumbasses messing with the mod config through notepad
        //Regex validation
        boolean valid = true;
        List<String> regexList = config.regexList.get();
        List<Integer> groupList = config.groupList.get();
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
        ArrayList colors = new ArrayList<>(TextFormatting.getValidValues(true, false));
        if (!colors.contains(config.color.get())) {
            config.color.set("gray");
            valid = false;
        }
        if (!valid) {
            config.regexList.set(regexList);
            config.groupList.set(groupList);
            saveConfig();
        }
    }

    private static void versionCheck() {
        int configVersion = config.configMinVersion.get();
        //In case there might be any major updates that would break under existing configs, this is here to reset everything.
        if (configMinVersion > configVersion) {
            config.targetLanguage.set("English");
            config.selfLanguage.set("English");
            config.speakAsLanguage.set("Japanese");
            config.bold.set(false);
            config.italic.set(false);
            config.underline.set(false);
            config.translateSign.set(true);
            config.color.set("gray");
            config.regexList.set(Arrays.asList(defaultRegex));
            config.groupList.set(Ints.asList(defaultGroups));
            config.configMinVersion.set(configMinVersion);
        }
    }

    public static void saveConfig() {
        configSpec.save();
        config.selfLanguage.save();
        config.targetLanguage.save();
        config.speakAsLanguage.save();
        config.groupList.save();
        config.regexList.save();
        config.bold.save();
        config.italic.save();
        config.underline.save();
        config.color.save();
        config.configMinVersion.save();
        config.userKey.save();
        config.translateSign.save();
        validateConfig();
    }

    //1.13+ no longer requires long property definition. Yay!
    public static class ClientConfig {
        public final ConfigValue<String> targetLanguage;
        public final ConfigValue<String> selfLanguage;
        public final ConfigValue<String> speakAsLanguage;
        public final BooleanValue bold;
        public final BooleanValue italic;
        public final BooleanValue underline;
        public final ConfigValue<String> color;
        public final BooleanValue translateSign;
        public final ConfigValue<String> userKey;
        public final ConfigValue<List<String>> regexList;
        public final ConfigValue<List<Integer>> groupList;
        final IntValue configMinVersion;


        ClientConfig(ForgeConfigSpec.Builder builder) {
            builder.comment("Real time translation mod configs").push(TranslationMod.MODID);
            configMinVersion = builder.comment("Config version. DO NOT CHANGE.").defineInRange("configMinVersion", 1, 0, Integer.MAX_VALUE);
            targetLanguage = builder.comment("Target language to translate for the chat").define("targetLanguage", "English", lang -> validateLang((String) lang));
            selfLanguage = builder.comment("The language the user types").define("selfLanguage", "English", lang -> validateLang((String) lang));
            speakAsLanguage = builder.comment("The language the user wants their message to translate to").define("speakAsLanguage", "Japanese", lang -> validateLang((String) lang));
            bold = builder.comment("Bold the translated message").define("bold", false);
            italic = builder.comment("Italic the translated message").define("italic", false);
            underline = builder.comment("Underline the translated message").define("underline", false);
            color = builder.comment("Changes the color of the translated message").define("color", "gray", color -> {
                List<String> colors = new ArrayList<>(TextFormatting.getValidValues(true, false));
                String c = (String) color;
                return colors.contains(c);
            });
            translateSign = builder.comment("Allows translating texts in sign by looking").define("translateSign", true);
            userKey = builder.comment("Your personal translation key").define("userKey", "");
            //Not using forge to correct. It will just replace the entire list with the default.
            regexList = builder.comment("Your regex list").define("regexList", Arrays.asList(defaultRegex), o -> true);
            groupList = builder.comment("Your match group number to detect player names").define("groupList", Ints.asList(defaultGroups), o -> true);
        }

        private boolean validateLang(String lang) {
            List<Language> languages = LangManager.getInstance().getAllLanguages();
            for (Language language : languages) {
                if (lang.equals(language.getName()))
                    return true;
            }
            return false;
        }
    }
}
