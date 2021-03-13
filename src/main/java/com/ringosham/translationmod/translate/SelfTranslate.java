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
        //noinspection ConstantConditions
        Minecraft.getInstance().player.sendChatMessage(selfHeader + " " + translatedMessage.getMessage());
    }
}
