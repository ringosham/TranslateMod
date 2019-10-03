package com.ringosham.translationmod.translate;

import com.ringosham.translationmod.common.ChatUtil;
import com.ringosham.translationmod.common.ConfigManager;
import com.ringosham.translationmod.common.Log;
import com.ringosham.translationmod.translate.model.SignText;
import com.ringosham.translationmod.translate.model.TranslateResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

public class SignTranslate extends Thread {
    private String text;
    private final BlockPos pos;

    public SignTranslate(String text, BlockPos pos) {
        this.text = text;
        this.pos = pos;
    }

    @Override
    public void run() {
        Log.logger.debug("Sign detected. Translating");
        SignText signData = new SignText();
        signData.setSign(text, pos);
        //Directly call the translator class as this is already on a separate thread
        Translator translator = new Translator(text, null, ConfigManager.INSTANCE.getTargetLanguage());
        TranslateResult translatedMessage = translator.translate(text);
        //Silently fail. The Translator class should handle the exception
        if (translatedMessage == null)
            return;
        String chatMessage = "[Sign] --> " + translatedMessage.getFromLanguage().getName() + ": " + translatedMessage.getMessage();
        String hoverText = "Sign location: " +
                pos.getX() + ", " + pos.getY() + ", " + pos.getZ() +
                "\n" +
                "Translation: " +
                translatedMessage.getFromLanguage().getName() + " -> " + ConfigManager.INSTANCE.getTargetLanguage().getName();
        ChatUtil.printChatMessageAdvanced(chatMessage, hoverText, ConfigManager.INSTANCE.isBold(), ConfigManager.INSTANCE.isItalic(), ConfigManager.INSTANCE.isUnderline(), TextFormatting.getValueByName(ConfigManager.INSTANCE.getColor()));
    }
}
