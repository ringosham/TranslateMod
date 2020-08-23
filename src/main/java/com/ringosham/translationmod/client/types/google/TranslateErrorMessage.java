package com.ringosham.translationmod.client.types.google;

public class TranslateErrorMessage {
    //HTTP code
    private int code;
    private String message;
    //List of sub-errors? Not important. This is left for deserializing purposes
    private TranslateErrorDetails[] errors;
    private String status;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public TranslateErrorDetails[] getErrors() {
        return errors;
    }

    public String getStatus() {
        return status;
    }
}
