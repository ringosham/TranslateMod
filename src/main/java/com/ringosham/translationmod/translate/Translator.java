package com.ringosham.translationmod.translate;

import com.ringosham.translationmod.client.GoogleClient;
import com.ringosham.translationmod.client.KeyManager;
import com.ringosham.translationmod.client.LangManager;
import com.ringosham.translationmod.client.YandexClient;
import com.ringosham.translationmod.client.models.Language;
import com.ringosham.translationmod.client.models.RequestResult;
import com.ringosham.translationmod.common.ChatUtil;
import com.ringosham.translationmod.common.ConfigManager;
import com.ringosham.translationmod.common.Log;
import com.ringosham.translationmod.translate.model.TranslateResult;
import net.minecraft.util.text.TextFormatting;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Translator extends Thread {
    private final String message;
    private final Language from;
    private final Language to;

    public Translator(String message, Language from, Language to) {
        this.message = message;
        this.from = from;
        this.to = to;
    }

    //Parameter required for the raw content without any chat headers from the server
    public TranslateResult translate(String rawMessage) {
        //In case the message content is empty. Thanks to sloppy chat plugins in servers
        rawMessage = rawMessage.trim();
        if (rawMessage.length() == 0)
            return null;
        if (KeyManager.getInstance().isOffline())
            return null;
        String translatedMessage;
        Language from = null;
        //Color me surprised, Google has less language support than Yandex.
        if (GoogleClient.isAccessDenied() || to.getGoogleCode() == null) {
            YandexClient yandex = new YandexClient();
            RequestResult transRequest = yandex.translateAuto(KeyManager.getInstance().getCurrentKey(), rawMessage, to);
            if (transRequest.getCode() != 200) {
                logException(transRequest);
                return null;
            }
            translatedMessage = transRequest.getMessage();
        } else {
            YandexClient yandex = new YandexClient();
            RequestResult langRequest = yandex.detect(KeyManager.getInstance().getCurrentKey(), rawMessage);
            if (langRequest.getCode() != 200) {
                logException(langRequest);
                return null;
            }
            String langCode = langRequest.getMessage();
            from = this.from != null ? this.from : LangManager.getInstance().findLanguageFromYandex(langCode);
            GoogleClient google = new GoogleClient();
            RequestResult transRequest = google.translate(rawMessage, from, to);
            if (transRequest.getCode() != 200) {
                logException(transRequest);
                return null;
            }
            translatedMessage = transRequest.getMessage();
        }
        return new TranslateResult(translatedMessage, from);
    }

    private void logException(RequestResult transRequest) {
        switch (transRequest.getCode()) {
            case 1:
                Log.logger.error("Cannot connect to translation server. Is player offline?");
                break;
            case 429:
                Log.logger.warn("Google denied access to translation API. Switching to Yandex");
                ChatUtil.printChatMessage(true, "Google translate has stopped responding. Fallback to Yandex provider", TextFormatting.WHITE);
                break;
            case 402:
                Log.logger.error("API key blocked. Changing keys");
                changeKeys();
                break;
            case 404:
                Log.logger.info("Daily text limit reached. Changing keys");
                changeKeys();
                break;
            case 422:
                Log.logger.error("Text failed to translate");
                break;
            case 501:
                Log.logger.error("Translation direction not supported");
                ChatUtil.printChatMessage(true, "Translation direction not supported. Please choose a different language.", TextFormatting.RED);
                break;
            case 413:
                Log.logger.error("Text length too long. Server refused to process");
                break;
            case 500:
                Log.logger.error("Internal server error. Is translation server down?");
                break;
            default:
                Log.logger.error("Unknown error: " + transRequest.getMessage());
                break;
        }
    }

    private void changeKeys() {
        if (KeyManager.getInstance().isRotating())
            return;
        ChatUtil.printChatMessage(true, "Switching keys...", TextFormatting.WHITE);
        if (!KeyManager.getInstance().rotateKey()) {
            ChatUtil.printChatMessage(true, "All translation keys have been used up for today. The mod will not function without a translation key", TextFormatting.RED);
            ChatUtil.printChatMessage(true, "You can go to the mod settings -> User key. You can add your own translation key there.", TextFormatting.RED);
        }
    }

    //Finds the player name in the chat using regex groups
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private String findPlayerName(String message, int regexIndex) {
        Pattern pattern = Pattern.compile(ConfigManager.INSTANCE.getRegexList().get(regexIndex));
        Matcher matcher = pattern.matcher(message);
        matcher.find();
        return matcher.group(ConfigManager.INSTANCE.getGroupList().get(regexIndex));
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
        String chatMessage = sender + " --> " + translatedMessage.getFromLanguage().getName() + ": " + translatedMessage.getMessage();
        String hoverText = "Sender: " +
                sender +
                "\n" +
                "Translation: " +
                translatedMessage.getFromLanguage().getName() + " -> " + to.getName();
        //In cases where the message language and the target language is the same
        if (translatedMessage.getMessage().trim().equals(rawMessage.trim()))
            return;
        ChatUtil.printChatMessageAdvanced(chatMessage, hoverText, ConfigManager.INSTANCE.isBold(), ConfigManager.INSTANCE.isItalic(), ConfigManager.INSTANCE.isUnderline(), TextFormatting.getValueByName(ConfigManager.INSTANCE.getColor()));
    }
}
