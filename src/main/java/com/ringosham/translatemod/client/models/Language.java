package com.ringosham.translatemod.client.models;

public class Language {
    private String name;
    private String nameUnicode;
    private String googleCode;
    private String yandexCode;

    public Language(String name, String nameUnicode, String googleCode, String yandexCode) {
        this.name = name;
        this.nameUnicode = nameUnicode;
        this.googleCode = googleCode;
        this.yandexCode = yandexCode;
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

    public String getYandexCode() {
        return yandexCode;
    }
}
