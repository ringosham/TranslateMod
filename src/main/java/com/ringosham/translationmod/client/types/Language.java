package com.ringosham.translationmod.client.types;

public class Language {
    private final String name;
    private final String nameUnicode;
    private final String googleCode;

    public Language(String name, String nameUnicode, String googleCode) {
        this.name = name;
        this.nameUnicode = nameUnicode;
        this.googleCode = googleCode;
    }

    public String getName() {
        return name;
    }

    public String getNameUnicode() {
        return nameUnicode;
    }

    public String getGoogleCode() {
        return googleCode;
    }

}
