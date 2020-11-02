package com.ringosham.translationmod.translate.types;

import com.ringosham.translationmod.client.types.Language;

public class TranslateResult {
    private final String message;
    private final Language fromLanguage;

    public TranslateResult(String message, Language fromLanguage) {
        this.message = message;
        this.fromLanguage = fromLanguage;
    }

    public String getMessage() {
        return message;
    }

    public Language getSourceLanguage() {
        return fromLanguage;
    }
}
