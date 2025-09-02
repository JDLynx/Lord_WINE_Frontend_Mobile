package com.juanmolina.lordwine;

public class ChatMessage {
    private String text;
    private String sender;

    public ChatMessage(String text, String sender) {
        this.text = text;
        this.sender = sender;
    }

    public String getText() {
        return text;
    }

    public String getSender() {
        return sender;
    }
}