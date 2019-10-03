package com.ringosham.translationmod.client.models;

public class RequestResult {
    private int code;
    private String message;

    public RequestResult(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
