package com.ringosham.translationmod.translate;

import com.ringosham.translationmod.client.types.Language;
import com.ringosham.translationmod.common.ChatUtil;
import com.ringosham.translationmod.common.ConfigManager;
import com.ringosham.translationmod.translate.types.TranslateResult;
import net.minecraft.util.EnumChatFormatting;

public class Retranslate extends Thread {

    private final String message;
    private final Language from;
    private final Language to;
    private final String sender;

    public Retranslate(String sender, String message, Language from, Language to) {
        this.sender = sender;
        this.message = message;
        this.from = from;
        this.to = to;
    }

    @Override
    public void run() {
        Translator translator = new Translator(message, from, to);
        //Save bandwidth and money $.
        if (from == to)
            return;
        TranslateResult translatedMessage = translator.translate(message.trim());
        if (translatedMessage == null) {
            ChatUtil.printChatMessage(true, "An error occurred during translation", EnumChatFormatting.RED);
            return;
        }
        String fromStr = null;
        if (translatedMessage.getSourceLanguage() != null)
            fromStr = translatedMessage.getSourceLanguage().getName();
        String chatMessage = sender + " --> " + (fromStr == null ? "Unknown" : fromStr) + ": " + translatedMessage.getMessage();
        String hoverText = "Sender: " +
                sender +
                "\n" +
                "Translation: " +
                (fromStr == null ? "Unknown" : fromStr) + " -> " + to.getName();
        //If the translation result is the same as the original message, this means the translation failed.
        if (translatedMessage.getMessage().trim().equals(message.trim())) {
            ChatUtil.printChatMessage(true, "Translation failed. Try another language maybe?", EnumChatFormatting.RED);
            return;
        }
        ChatUtil.printChatMessageAdvanced(chatMessage, hoverText, ConfigManager.INSTANCE.isBold(), ConfigManager.INSTANCE.isItalic(), ConfigManager.INSTANCE.isUnderline(), EnumChatFormatting.getValueByName(ConfigManager.INSTANCE.getColor()));
    }
}
