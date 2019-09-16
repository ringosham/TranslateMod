package com.ringosham.translatemod.translate;

import com.ringosham.translatemod.common.ChatUtil;
import com.ringosham.translatemod.common.ConfigManager;
import com.ringosham.translatemod.common.Log;
import com.ringosham.translatemod.translate.model.SignText;
import com.ringosham.translatemod.translate.model.TranslateResult;
import net.minecraft.util.EnumChatFormatting;

public class SignTranslate extends Thread {
    private String text;
    private int x;
    private int y;
    private int z;

    public SignTranslate(String text, int x, int y, int z) {
        this.text = text;
        if (this.x == x && this.y == y && this.z == z)
            return;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void run() {
        Log.logger.debug("Sign detected. Translating");
        SignText signData = new SignText();
        signData.setSign(text, x, y, z);
        //Directly call the translator class as this is already on a separate thread
        Translator translator = new Translator(text, null, ConfigManager.INSTANCE.getTargetLanguage());
        TranslateResult translatedMessage = translator.translate(text);
        //Silently fail. The Translator class should handle the exception
        if (translatedMessage == null)
            return;
        String chatMessage = "[Sign] --> " + translatedMessage.getFromLanguage().getName() + ": " + translatedMessage;
        String hoverText = "Sign location: " +
                x + ", " + y + ", " + z +
                "\n" +
                "Translation: " +
                translatedMessage.getFromLanguage().getName() + " -> " + ConfigManager.INSTANCE.getTargetLanguage().getName();
        ChatUtil.printChatMessageAdvanced(chatMessage, hoverText, ConfigManager.INSTANCE.isBold(), ConfigManager.INSTANCE.isItalic(), ConfigManager.INSTANCE.isUnderline(), EnumChatFormatting.getValueByName(ConfigManager.INSTANCE.getColor()));
    }
}
