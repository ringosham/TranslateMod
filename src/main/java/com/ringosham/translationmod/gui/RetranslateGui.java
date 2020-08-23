package com.ringosham.translationmod.gui;

import com.ringosham.translationmod.translate.Translator;
import net.minecraft.client.gui.GuiButton;

import java.util.ArrayList;
import java.util.List;

public class RetranslateGui extends CommonGui {
    private static final String title;
    private static final int guiHeight;
    private static final int guiWidth;

    static {
        title = "%mod_name% - Retranslate";
        guiHeight = 200;
        guiWidth = 350;
    }

    private final List<Translator.TranslationLog> logs;

    public RetranslateGui() {
        super(guiHeight, guiWidth);
        //Cache the log. As the chat will overwrite the log.
        logs = Translator.getTranslationLog();
    }

    @Override
    public void drawScreen(int x, int y, float tick) {
        super.drawScreen(x, y, tick);
        drawStringLine(title, new String[]{
                "Translations are in the incorrect language?",
                "Select the messages below to retranslate.",
        }, 0);
        for (int i = 0; i < buttonList.size(); i++) {
            TextButton button = (TextButton) buttonList.get(i);
            if (button.isMouseOver()) {
                List<String> hoverText = new ArrayList<>();
                hoverText.add("Sender: " + logs.get(i).getSender());
                hoverText.add("Message: " + logs.get(i).getMessage());
                //func_243308_b(MatrixStack, List<ITextComponent>, int, int) -> renderTooltip(...)
                drawHoveringText(hoverText, x, y);
            }
        }
    }

    @Override
    public void initGui() {
        int index = 0;
        int offset = 0;
        for (Translator.TranslationLog log : logs) {
            String buttonText = log.getMessage();
            if (getTextWidth(buttonText) > guiWidth - 15) {
                buttonText = buttonText + "...";
                while (getTextWidth(buttonText) > guiWidth - 15)
                    buttonText = buttonText.substring(0, buttonText.length() - 4) + "...";
            }
            this.buttonList.add(new TextButton(index, getLeftMargin(), getTopMargin() + 40 + offset, getTextWidth(buttonText), buttonText, 0));
            offset += 10;
            index++;
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        selectLanguage(logs.get(button.id).getSender(), logs.get(button.id).getMessage());
    }

    private void selectLanguage(String sender, String message) {
        mc.displayGuiScreen(new LanguageSelectGui(sender, message));
    }
}
