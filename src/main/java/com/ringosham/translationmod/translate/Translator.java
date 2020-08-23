package com.ringosham.translationmod.translate;

import com.ringosham.translationmod.client.GoogleClient;
import com.ringosham.translationmod.client.GooglePaidClient;
import com.ringosham.translationmod.client.types.Language;
import com.ringosham.translationmod.client.types.RequestResult;
import com.ringosham.translationmod.common.ChatUtil;
import com.ringosham.translationmod.common.ConfigManager;
import com.ringosham.translationmod.common.Log;
import com.ringosham.translationmod.translate.types.TranslateResult;
import net.minecraft.util.EnumChatFormatting;

import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Translator extends Thread {
    private final String message;
    private final Language from;
    private final Language to;
    private static final LinkedList<TranslationLog> translationLog = new LinkedList<>();

    public Translator(String message, Language from, Language to) {
        this.message = message;
        this.from = from;
        this.to = to;
    }

    public static LinkedList<TranslationLog> getTranslationLog() {
        return translationLog;
    }

    //Parameter required for the raw content without any chat headers from the server
    public TranslateResult translate(String rawMessage) {
        //Save bandwidth and money $.
        if (from == to)
            return null;
        //In case the message content is empty. Thanks to sloppy chat plugins in servers
        //Also if you are using the paid option, Google charges per character, including whitespaces.
        //If the request is empty, Google STILL charges a character.
        rawMessage = rawMessage.trim();
        if (rawMessage.length() == 0)
            return null;
        if (!ConfigManager.INSTANCE.getUserKey().equals("") && !GooglePaidClient.getDisable()) {
            //Paid options go first.
            GooglePaidClient google = new GooglePaidClient();
            RequestResult transRequest;
            if (from == null)
                transRequest = google.translateAuto(rawMessage, to);
            else
                transRequest = google.translate(rawMessage, from, to);
            if (transRequest.getCode() != 200) {
                logException(transRequest);
                return null;
            }
            return new TranslateResult(transRequest.getMessage(), transRequest.getFrom());
        } else if (!GoogleClient.isAccessDenied()) {
            //Use free ones later
            GoogleClient google = new GoogleClient();
            RequestResult transRequest;
            if (from == null)
                transRequest = google.translateAuto(rawMessage, to);
            else
                transRequest = google.translate(rawMessage, from, to);
            if (transRequest.getCode() != 200) {
                logException(transRequest);
                return null;
            }
            return new TranslateResult(transRequest.getMessage(), transRequest.getFrom());
        }
        //Otherwise ignore
        return null;
    }

    //Finds the player name in the chat using regex groups
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private String findPlayerName(String message, int regexIndex) {
        Pattern pattern = Pattern.compile(ConfigManager.INSTANCE.getRegexList().get(regexIndex));
        Matcher matcher = pattern.matcher(message);
        matcher.find();
        return matcher.group(ConfigManager.INSTANCE.getGroupList().get(regexIndex));
    }

    private void logException(RequestResult transRequest) {
        switch (transRequest.getCode()) {
            case 1:
                Log.logger.error("Cannot connect to translation server. Is player offline?");
                break;
            case 2:
                //Unknown response
                Log.logger.error(transRequest.getMessage());
            case 429:
                Log.logger.warn("Google denied access to translation API. Pausing translation for 5 minutes");
                ChatUtil.printChatMessage(true, "Google translate has stopped responding. Pausing translations", EnumChatFormatting.YELLOW);
                break;
            case 403:
                Log.logger.error("Exceeded API quota");
                ChatUtil.printChatMessage(true, "You have exceeded your quota. Please check your quota settings", EnumChatFormatting.RED);
                ChatUtil.printChatMessage(true, "Falling back to free version until you restart the game", EnumChatFormatting.RED);
                GooglePaidClient.setDisable();
                break;
            case 400:
                Log.logger.error("API key invalid");
                ChatUtil.printChatMessage(true, "API key invalid. If you do not wish to use a key, please remove it from the settings", EnumChatFormatting.RED);
                break;
            case 500:
                Log.logger.error("Failed to determine source language: " + transRequest.getMessage());
                break;
            default:
                Log.logger.error("Unknown error: " + transRequest.getMessage());
                break;
        }
    }

    //If this class is run as a thread, it handles incoming chat messages
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void run() {
        int regexIndex = 0;
        //There could be multiple regex that matches the chat message. In that case, choose the longest match after matching.
        if (message.trim().isEmpty())
            return;
        //Replace section signs one more time for the sake of bugs. Thanks Java.
        String messageTrim = message.replaceAll("\u00A7(.)", "");
        String headerMatch = null;
        for (String regex : ConfigManager.INSTANCE.getRegexList()) {
            String regexFixed = regex;
            if (!regex.contains("^"))
                regexFixed = "^" + regex;
            Pattern pattern = Pattern.compile(regexFixed);
            Matcher matcher = pattern.matcher(messageTrim);
            if (matcher.find()) {
                if (headerMatch == null) {
                    headerMatch = matcher.group(0);
                    //There should be no duplicates in the list. This should be fine.
                    regexIndex = ConfigManager.INSTANCE.getRegexList().indexOf(regex);
                } else if (headerMatch.length() < matcher.group(0).length()) {
                    headerMatch = matcher.group(0);
                    regexIndex = ConfigManager.INSTANCE.getRegexList().indexOf(regex);
                }
            }
        }
        //Ignore message if no regex match
        if (headerMatch == null)
            return;
        String sender = findPlayerName(headerMatch, regexIndex);
        //If the player name cannot be found, chances are it's not a chat message
        if (sender == null)
            return;
        Pattern pattern = Pattern.compile(ConfigManager.INSTANCE.getRegexList().get(regexIndex));
        Matcher matcher = pattern.matcher(messageTrim);
        matcher.find();
        //Remove the chat header to get the actual content
        String rawMessage = messageTrim.replace(matcher.group(0), "");
        TranslateResult translatedMessage = translate(rawMessage);
        addToLog(new TranslationLog(sender, rawMessage));
        if (translatedMessage == null)
            return;
        String fromStr = null;
        if (translatedMessage.getFromLanguage() != null)
            fromStr = translatedMessage.getFromLanguage().getName();
        String chatMessage = sender + " --> " + (fromStr == null ? "Unknown" : fromStr) + ": " + translatedMessage.getMessage();
        String hoverText = "Sender: " +
                sender +
                "\n" +
                "Translation: " +
                (fromStr == null ? "Unknown" : fromStr) + " -> " + to.getName();
        //In cases where the message language and the target language is the same
        if (translatedMessage.getMessage().trim().equals(rawMessage.trim()))
            return;
        ChatUtil.printChatMessageAdvanced(chatMessage, hoverText, ConfigManager.INSTANCE.isBold(), ConfigManager.INSTANCE.isItalic(), ConfigManager.INSTANCE.isUnderline(), EnumChatFormatting.getValueByName(ConfigManager.INSTANCE.getColor()));
    }

    private void addToLog(TranslationLog log) {
        translationLog.add(log);
        if (translationLog.size() > 15)
            translationLog.pollFirst();
    }

    public static class TranslationLog {
        private final String sender;
        private final String message;

        public TranslationLog(String sender, String message) {
            this.sender = sender;
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public String getSender() {
            return sender;
        }
    }
}
