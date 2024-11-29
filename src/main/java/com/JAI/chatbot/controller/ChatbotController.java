package com.JAI.chatbot.controller;

import com.JAI.chatbot.RedisChatbotUtil;
import com.JAI.chatbot.controller.dto.ChatbotRedisDataDTO;
import com.JAI.chatbot.controller.dto.request.ChatbotReqDTO;
import com.JAI.chatbot.controller.dto.request.TokenReqDTO;
import com.JAI.chatbot.controller.dto.response.ChatbotResponseWrapper;
import com.JAI.chatbot.service.ChatbotService;
import jakarta.validation.Valid;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chatbot")
@RequiredArgsConstructor
public class ChatbotController {

    @Autowired
    private final ChatbotService chatbotService;

    @Autowired
    private final RedisChatbotUtil redisChatbotUtil;

    @PostMapping("/messages")
    public ChatbotResponseWrapper message(@RequestBody @Valid ChatbotReqDTO request) {

        // 사용자 입력 데이터 레디스에 저장
        TokenReqDTO tokenReqDTO = chatbotService.saveRequest(request);

        // 레디스에 저장된 intention 불러오기
        ChatbotRedisDataDTO chatbotRedisDataDTO = redisChatbotUtil.getChatbotData(tokenReqDTO.getToken());
        String intention = chatbotRedisDataDTO.getChatbotReqDTO().getIntention();

        // intention이 null 아닌 경우 -> createResponseJson() 호출
        if (intention != null) {
            return chatbotService.createResponseJson(tokenReqDTO);
        }

        // intention이 null인 경우
        // analyzeIntention() 호출해서 텍스트 의도 분석 후
        // createResponseJson() 호출
        else {
            chatbotService.analyzeIntention(tokenReqDTO);
            return chatbotService.createResponseJson(tokenReqDTO);
        }
    }

}