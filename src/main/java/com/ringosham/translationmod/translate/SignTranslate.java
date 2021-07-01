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

import com.ringosham.translationmod.common.ChatUtil;
import com.ringosham.translationmod.common.ConfigManager;
import com.ringosham.translationmod.common.Log;
import com.ringosham.translationmod.translate.types.SignText;
import com.ringosham.translationmod.translate.types.TranslateResult;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;

public class SignTranslate extends Thread {
    private final String text;
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
        String chatMessage = "[Sign] --> " + translatedMessage.getSourceLanguage().getName() + ": " + translatedMessage.getMessage();
        String hoverText = "Sign location: " +
                pos.getX() + ", " + pos.getY() + ", " + pos.getZ() +
                "\n" +
                "Translation: " +
                translatedMessage.getSourceLanguage().getName() + " -> " + ConfigManager.INSTANCE.getTargetLanguage().getName();
        ChatUtil.printChatMessageAdvanced(chatMessage, hoverText, ConfigManager.INSTANCE.isBold(), ConfigManager.INSTANCE.isItalic(), ConfigManager.INSTANCE.isUnderline(), EnumChatFormatting.getValueByName(ConfigManager.INSTANCE.getColor()));
    }
}
