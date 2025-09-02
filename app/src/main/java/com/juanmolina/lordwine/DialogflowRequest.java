package com.juanmolina.lordwine;

public class DialogflowRequest {
    private String message;
    private String userId;

    public DialogflowRequest(String message, String userId) {
        this.message = message;
        this.userId = userId;
    }
}