package com.ringosham.translationmod.common;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;

public class ChatUtil {
    private static final String prefix = EnumChatFormatting.GREEN + "[" + EnumChatFormatting.RESET + "RTTM" + EnumChatFormatting.GREEN + "] " + EnumChatFormatting.RESET;

    public static void printChatMessage(boolean addPrefix, String message, EnumChatFormatting color) {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        ChatStyle style = new ChatStyle();
        style.setColor(color);
        player.addChatMessage(new ChatComponentText((addPrefix ? prefix : "") + color + message).setChatStyle(style));
    }

    public static void printChatMessageAdvanced(String message, String hoverText, boolean bold, boolean italic, boolean underline, EnumChatFormatting color) {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        ChatStyle style = new ChatStyle();
        style.setColor(color)
                .setBold(bold)
                .setItalic(italic)
                .setUnderlined(underline);
        if (hoverText != null)
            style.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(hoverText)));
        player.addChatMessage(new ChatComponentText(message).setChatStyle(style));
    }

    public static void printCredits() {
        ChatUtil.printChatMessage(false, "Real-time translation mod by Ringosham. Version %mod_version%", EnumChatFormatting.AQUA);
        ChatUtil.printChatMessage(false, "Online translation services powered by Yandex and Google", EnumChatFormatting.AQUA);
        ChatUtil.printChatMessage(false, "Translation results may not be 100% accurate", EnumChatFormatting.AQUA);
    }

    //For color only.
    public static EnumChatFormatting getFormattingFromChar(char c) {
        for (EnumChatFormatting f : EnumChatFormatting.values())
            if (c == f.getFormattingCode())
                return f;
        return null;
    }
}
