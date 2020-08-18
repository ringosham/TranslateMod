package com.ringosham.translationmod.client.types.google;

public class TranslateTextResponseTranslation {
    private String detectedSourceLanguage;
    private String model;
    private String translatedText;

    public String getDetectedSourceLanguage() {
        return detectedSourceLanguage;
    }

    public String getModel() {
        return model;
    }

    public String getTranslatedText() {
        return translatedText;
    }
}
