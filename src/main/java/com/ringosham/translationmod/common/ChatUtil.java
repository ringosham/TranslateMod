package com.ringosham.translationmod.common;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.fml.ModList;

public class ChatUtil {
    private static final String prefix = TextFormatting.GREEN + "[" + TextFormatting.RESET + "RTTM" + TextFormatting.GREEN + "] " + TextFormatting.RESET;

    public static void printChatMessage(boolean addPrefix, String message, TextFormatting color) {
        PlayerEntity player = Minecraft.getInstance().player;
        Style style = new Style();
        style.setColor(color);
        Minecraft.getInstance().ingameGUI.getChatGUI().printChatMessage((new StringTextComponent((addPrefix ? prefix : "") + color + message).setStyle(style)));
    }

    public static void printChatMessageAdvanced(String message, String hoverText, boolean bold, boolean italic, boolean underline, TextFormatting color) {
        PlayerEntity player = Minecraft.getInstance().player;
        Style style = new Style();
        style.setColor(color)
                .setBold(bold)
                .setItalic(italic)
                .setUnderlined(underline);
        if (hoverText != null)
            style.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent(hoverText)));
        Minecraft.getInstance().ingameGUI.getChatGUI().printChatMessage(new StringTextComponent(message).setStyle(style));
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public static void printCredits() {
        String version = (String) ModList.get().getModContainerById("translationmod").get().getModInfo().getModProperties().get("version");
        ChatUtil.printChatMessage(false, "Real-time translation mod by Ringosham. Version " + version, TextFormatting.AQUA);
        ChatUtil.printChatMessage(false, "Online translation services powered by Yandex and Google", TextFormatting.AQUA);
        ChatUtil.printChatMessage(false, "Translation results may not be 100% accurate", TextFormatting.AQUA);
    }
}
