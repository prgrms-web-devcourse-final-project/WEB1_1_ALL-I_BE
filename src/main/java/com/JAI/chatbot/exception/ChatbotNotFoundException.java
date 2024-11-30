package com.JAI.chatbot.exception;

public class ChatbotNotFoundException extends RuntimeException {
    private Object data;
    public ChatbotNotFoundException(String message) { super(message); }
    public ChatbotNotFoundException(Object data, String message) {
        super(message);
        this.data = data;
    }
    public Object getData() {
        return data;
    }
}
