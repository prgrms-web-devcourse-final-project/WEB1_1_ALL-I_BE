package com.JAI.chatbot.exception;

public class ChatbotBadRequestException extends RuntimeException {
    private Object data;
    public ChatbotBadRequestException(String message) { super(message); }
    public ChatbotBadRequestException(Object data, String message) {
        super(message);
        this.data = data;
    }
    public Object getData() {
        return data;
    }

}
