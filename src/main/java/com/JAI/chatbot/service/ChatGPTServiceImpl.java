package com.JAI.chatbot.service;

import com.JAI.chatbot.controller.ChatGPTMessage;
import com.JAI.chatbot.controller.request.ChatGPTReq;
import com.JAI.chatbot.controller.request.TokenReq;
import com.JAI.chatbot.controller.response.ChatGPTResp;
import com.JAI.chatbot.domain.Intention;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatGPTServiceImpl implements ChatGPTService {
    @Value("${openai.model}")
    private String model;

    @Value("${openai.api.url}")
    private String apiURL;

    private final RestTemplate openAiRestTemplate;

    @Override
    public ChatGPTResp postMessage(List<ChatGPTMessage> message, TokenReq token) {
        ChatGPTReq request = new ChatGPTReq(model, message);
        ChatGPTResp response = openAiRestTemplate.postForObject(apiURL, request, ChatGPTResp.class);

        return response;
    }

    @Override
    public Intention findIntention(List<ChatGPTMessage> message, TokenReq token) {
        ChatGPTReq request = new ChatGPTReq(model, message);
        ChatGPTResp response =  openAiRestTemplate.postForObject(apiURL, request, ChatGPTResp.class);
        String content = response.choices().get(0).message().content();
        Intention intention;

        if (content.equals("계획 추천")) { intention = Intention.PLAN_RECOMMENDATION; }
        else if (content.equals("일정 자동 기입")) { intention = Intention.AUTOMATIC_EVENT_ENTRY; }
        else if (content.equals("투두 자동 기입")) { intention = Intention.AUTOMATIC_TODO_ENTRY; }
        else { intention = Intention.EXCEPTION; }

        return intention;
    }
}
