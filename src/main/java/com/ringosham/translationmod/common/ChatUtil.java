package com.ringosham.translationmod.common;

import com.ringosham.translationmod.TranslationMod;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.fml.ModList;

import java.util.ArrayList;
import java.util.List;

public class ChatUtil {
    private static final String prefix = TextFormatting.GREEN + "[" + TextFormatting.WHITE + "RTTM" + TextFormatting.GREEN + "] " + TextFormatting.RESET;

    public static void printChatMessage(boolean addPrefix, String message, TextFormatting color) {
        //Color.func_240774_a_(TextFormatting) -> Color.fromTextFormatting(...)
        Style style = Style.EMPTY.setColor(Color.func_240744_a_(color));
        Minecraft.getInstance().ingameGUI.getChatGUI().printChatMessage((new StringTextComponent((addPrefix ? prefix : "") + color + message).mergeStyle(style)));
    }

    public static void printChatMessageAdvanced(String message, String hoverText, boolean bold, boolean italic, boolean underline, TextFormatting color) {
        //Styles are immutable in 1.16. So we have that...
        List<TextFormatting> formattings = new ArrayList<>();
        formattings.add(color);
        if (bold)
            formattings.add(TextFormatting.BOLD);
        if (italic)
            formattings.add(TextFormatting.ITALIC);
        if (underline)
            formattings.add(TextFormatting.UNDERLINE);
        Style style = Style.EMPTY.createStyleFromFormattings(formattings.toArray(new TextFormatting[0]));
        if (hoverText != null)
            style = style.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent(hoverText)));
        Minecraft.getInstance().ingameGUI.getChatGUI().printChatMessage(new StringTextComponent(message).mergeStyle(style));
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public static void printCredits() {
        String version = ModList.get().getModContainerById(TranslationMod.MODID).get().getModInfo().getVersion().toString();
        ChatUtil.printChatMessage(false, "Real-time translation mod by Ringosham. Version " + version, TextFormatting.AQUA);
        ChatUtil.printChatMessage(false, "Online translation services powered by Google", TextFormatting.AQUA);
        ChatUtil.printChatMessage(false, "Translation results may not be 100% accurate", TextFormatting.AQUA);
    }
}
