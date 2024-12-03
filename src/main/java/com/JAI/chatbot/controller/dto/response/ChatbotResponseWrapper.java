package com.JAI.chatbot.controller.dto.response;

import com.JAI.chatbot.controller.dto.request.TokenReqDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatbotResponseWrapper<T> {
    private List<T> responses;
    private TokenReqDTO tokenReqDTO;
}
