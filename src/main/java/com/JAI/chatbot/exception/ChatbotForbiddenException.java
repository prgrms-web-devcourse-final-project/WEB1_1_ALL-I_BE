package com.JAI.chatbot.exception;

public class ChatbotForbiddenException extends RuntimeException {
    private Object data;
    public ChatbotForbiddenException(String message) { super(message); }
    public ChatbotForbiddenException(Object data, String message) {
        super(message);
        this.data = data;
    }
    public Object getData() {
        return data;
    }
}
