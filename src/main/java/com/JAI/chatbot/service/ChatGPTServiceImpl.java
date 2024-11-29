package com.JAI.chatbot.service;

import com.JAI.chatbot.controller.dto.ChatGPTMessageDTO;
import com.JAI.chatbot.controller.dto.request.ChatGPTReqDTO;
import com.JAI.chatbot.controller.dto.request.TokenReqDTO;
import com.JAI.chatbot.controller.dto.response.ChatGPTRespDTO;
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
    public ChatGPTRespDTO postMessage(List<ChatGPTMessageDTO> message, TokenReqDTO token) {

        // ChatGPT 응답 형식 맞춰서 생성
        ChatGPTReqDTO request = ChatGPTReqDTO.builder()
                .model(model)
                .messages(message)
                .build();

        // ChatGPT 응답 생성 요청
        ChatGPTRespDTO response = openAiRestTemplate.postForObject(apiURL, request, ChatGPTRespDTO.class);
        System.out.println("response: "+response);
        System.out.println("choice: "+response.getChoices().get(0));
        System.out.println("message: "+response.getChoices().get(0).getMessage());
        System.out.println("content: "+response.getChoices().get(0).getMessage().getContent());

        // ChatGPT 응답 반환
        return response;
    }

    @Override
    public String findIntention(List<ChatGPTMessageDTO> message, TokenReqDTO token) {

        // ChatGPT 응답 형식 맞춰서 생성
        ChatGPTReqDTO request = ChatGPTReqDTO.builder()
                .model(model)
                .messages(message)
                .build();

        // ChatGPT 응답 생성 요청
        ChatGPTRespDTO response =  openAiRestTemplate.postForObject(apiURL, request, ChatGPTRespDTO.class);
        System.out.println("response: "+response);
        System.out.println("choice: "+response.getChoices().get(0));
        System.out.println("message: "+response.getChoices().get(0).getMessage());
        System.out.println("content: "+response.getChoices().get(0).getMessage().getContent());

        // ChatGPT 응답으로 intention 결정
        String content = response.getChoices().get(0).getMessage().getContent();



        String intention;
        if (content.equals("계획 추천")) { intention = "PLAN_RECOMMENDATION"; }
        else if (content.equals("일정 자동 기입")) { intention = "EVENT"; }
        else if (content.equals("투두 자동 기입")) { intention = "TODO"; }
        else { intention = "EXCEPTION"; }

        return intention;
    }
}
