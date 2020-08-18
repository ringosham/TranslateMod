package com.ringosham.translationmod.translate;

import com.ringosham.translationmod.client.LangManager;
import com.ringosham.translationmod.common.ConfigManager;
import com.ringosham.translationmod.translate.types.TranslateResult;
import net.minecraft.client.Minecraft;

public class SelfTranslate extends Thread {
    private final String message;
    private final String selfHeader;

    public SelfTranslate(String message, String selfHeader) {
        this.message = message;
        this.selfHeader = selfHeader;
    }

    @Override
    public void run() {
        Translator translator = new Translator(message,
                LangManager.getInstance().findLanguageFromName(ConfigManager.config.selfLanguage.get()),
                LangManager.getInstance().findLanguageFromName(ConfigManager.config.speakAsLanguage.get()));
        TranslateResult translatedMessage = translator.translate(message);
        //Silently fail. The Translator class should handle the exception
        if (translatedMessage == null)
            return;
        Minecraft.getInstance().player.sendChatMessage(selfHeader + " " + translatedMessage.getMessage());
    }
}
