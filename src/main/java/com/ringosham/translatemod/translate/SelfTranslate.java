package com.ringosham.translatemod.translate;

import com.ringosham.translatemod.common.ConfigManager;
import com.ringosham.translatemod.translate.model.TranslateResult;
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
        Translator translator = new Translator(message, ConfigManager.INSTANCE.getSelfLanguage(), ConfigManager.INSTANCE.getSpeakAsLanguage());
        TranslateResult translatedMessage = translator.translate(message);
        //Silently fail. The Translator class should handle the exception
        if (translatedMessage == null)
            return;
        Minecraft.getMinecraft().thePlayer.sendChatMessage(selfHeader + " " + translatedMessage.getMessage());
    }
}
