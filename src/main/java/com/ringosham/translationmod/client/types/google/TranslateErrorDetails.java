package com.ringosham.translationmod.client.types.google;

public class TranslateErrorDetails {
    private String message;
    //Domain level or something?
    private String domain;
    private String reason;

    public String getMessage() {
        return message;
    }

    public String getDomain() {
        return domain;
    }

    public String getReason() {
        return reason;
    }
}
