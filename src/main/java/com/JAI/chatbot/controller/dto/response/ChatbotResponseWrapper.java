package com.JAI.chatbot.controller.dto.response;

import java.util.List;

public class ChatbotResponseWrapper<T> {
    private List<T> responses;

    public ChatbotResponseWrapper(List<T> responses) {
        this.responses = responses;
    }

    public List<T> getResponses() {
        return responses;
    }
}
