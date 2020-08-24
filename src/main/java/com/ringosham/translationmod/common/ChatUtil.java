package com.ringosham.translationmod.common;

import com.ringosham.translationmod.TranslationMod;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.HoverEvent;

public class ChatUtil {
    private static final String prefix = TextFormatting.GREEN + "[" + TextFormatting.RESET + "RTTM" + TextFormatting.GREEN + "] " + TextFormatting.RESET;

    public static void printChatMessage(boolean addPrefix, String message, TextFormatting color) {
        EntityPlayer player = Minecraft.getMinecraft().player;
        Style style = new Style();
        style.setColor(color);
        Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage((new TextComponentString((addPrefix ? prefix : "") + color + message).setStyle(style)));
    }

    public static void printChatMessageAdvanced(String message, String hoverText, boolean bold, boolean italic, boolean underline, TextFormatting color) {
        EntityPlayer player = Minecraft.getMinecraft().player;
        Style style = new Style();
        style.setColor(color)
                .setBold(bold)
                .setItalic(italic)
                .setUnderlined(underline);
        if (hoverText != null)
            style.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(hoverText)));
        Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentString(message).setStyle(style));
    }

    public static void printCredits() {
        ChatUtil.printChatMessage(false, "Real-time translation mod by Ringosham. Version " + TranslationMod.MOD_VERSION, TextFormatting.AQUA);
        ChatUtil.printChatMessage(false, "Online translation services powered by Google", TextFormatting.AQUA);
        ChatUtil.printChatMessage(false, "Translation results may not be 100% accurate", TextFormatting.AQUA);
    }
}
