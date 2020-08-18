package com.ringosham.translationmod.gui;

import com.ringosham.translationmod.TranslationMod;
import com.ringosham.translationmod.translate.Translator;
import net.minecraftforge.fml.ModList;

import java.util.ArrayList;
import java.util.List;

public class RetranslateGui extends CommonGui {
    private static final String title;
    private static final int guiHeight;
    private static final int guiWidth;

    static {
        @SuppressWarnings("OptionalGetWithoutIsPresent")
        String modName = ModList.get().getModContainerById(TranslationMod.MODID).get().getModInfo().getDisplayName();
        title = modName + " - Retranslate";
        guiHeight = 200;
        guiWidth = 350;
    }

    private final List<Translator.TranslationLog> logs;

    public RetranslateGui() {
        super(title, guiHeight, guiWidth);
        //Cache the log. As the chat will overwrite the log.
        logs = Translator.getTranslationLog();
    }

    @Override
    public void render(int x, int y, float tick) {
        super.render(x, y, tick);
        drawStringLine(title, new String[]{
                "Translations are in the incorrect language?",
                "Select the messages below to retranslate.",
        }, 0);
        for (int i = 0; i < buttons.size(); i++) {
            TextButton button = (TextButton) buttons.get(i);
            if (button.isHovered()) {
                List<String> hoverText = new ArrayList<>();
                hoverText.add("Sender: " + logs.get(i).getSender());
                hoverText.add("Message: " + logs.get(i).getMessage());
                renderTooltip(hoverText, x, y);
            }
        }
    }

    @Override
    public void init() {
        int offset = 0;
        for (Translator.TranslationLog log : logs) {
            String buttonText = log.getMessage() + "...";
            while (getTextWidth(buttonText) > guiWidth - 15)
                buttonText = buttonText.substring(0, buttonText.length() - 4) + "...";
            addButton(new TextButton(getLeftMargin(), getTopMargin() + 40 + offset, getTextWidth(buttonText), buttonText, (button) -> selectLanguage(log.getSender(), log.getMessage()), 0));
            offset += 10;
        }
    }

    private void selectLanguage(String sender, String message) {
        getMinecraft().displayGuiScreen(new LanguageSelectGui(sender, message));
    }
}
