package com.ringosham.translationmod.client.types;

public class RequestResult {
    private final int code;
    private final String message;
    private final Language from;
    private final Language to;

    public RequestResult(int code, String message, Language from, Language to) {
        this.code = code;
        this.message = message;
        this.from = from;
        this.to = to;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Language getFrom() {
        return from;
    }

    public Language getTo() {
        return to;
    }
}
