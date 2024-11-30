package com.JAI.chatbot.exception;

public class ChatbotUnprocessableEntityException extends RuntimeException {
    private Object data;
    public ChatbotUnprocessableEntityException(String message) { super(message); }
    public ChatbotUnprocessableEntityException(Object data, String message) {
        super(message);
        this.data = data;
    }
    public Object getData() {
        return data;
    }
}
