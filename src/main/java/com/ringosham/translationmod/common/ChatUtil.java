package com.ringosham.translationmod.common;

import com.ringosham.translationmod.TranslationMod;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.fml.ModList;

public class ChatUtil {
    private static final String prefix = TextFormatting.GREEN + "[" + TextFormatting.RESET + "RTTM" + TextFormatting.GREEN + "] " + TextFormatting.RESET;

    public static void printChatMessage(boolean addPrefix, String message, TextFormatting color) {
        Style style = Style.EMPTY;
        //Color.func_240774_a_(TextFormatting) -> Color.fromTextFormatting(...)
        style.setFormatting(color);
        Minecraft.getInstance().ingameGUI.getChatGUI().printChatMessage((new StringTextComponent((addPrefix ? prefix : "") + color + message).setStyle(style)));
    }

    public static void printChatMessageAdvanced(String message, String hoverText, boolean bold, boolean italic, boolean underline, TextFormatting color) {
        Style style = Style.EMPTY;
        //Color.func_240774_a_(TextFormatting) -> Color.fromTextFormatting(...)
        style.setFormatting(color)
                .setBold(bold)
                .setItalic(italic)
                .setUnderlined(underline);
        if (hoverText != null)
            style.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent(hoverText)));
        Minecraft.getInstance().ingameGUI.getChatGUI().printChatMessage(new StringTextComponent(message).setStyle(style));
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public static void printCredits() {
        String version = ModList.get().getModContainerById(TranslationMod.MODID).get().getModInfo().getVersion().toString();
        ChatUtil.printChatMessage(false, "Real-time translation mod by Ringosham. Version " + version, TextFormatting.AQUA);
        ChatUtil.printChatMessage(false, "Online translation services powered by Yandex and Google", TextFormatting.AQUA);
        ChatUtil.printChatMessage(false, "Translation results may not be 100% accurate", TextFormatting.AQUA);
    }
}
