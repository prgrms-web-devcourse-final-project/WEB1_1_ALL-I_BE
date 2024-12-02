package com.JAI.chatbot.controller;

import com.JAI.chatbot.RedisChatbotUtil;
import com.JAI.chatbot.controller.dto.ChatbotRedisDataDTO;
import com.JAI.chatbot.controller.dto.request.ChatbotReqDTO;
import com.JAI.chatbot.controller.dto.request.TokenReqDTO;
import com.JAI.chatbot.controller.dto.response.ChatbotResponseWrapper;
import com.JAI.chatbot.service.ChatbotService;
import com.JAI.user.service.dto.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chatbot")
@RequiredArgsConstructor
public class ChatbotController {

    private final ChatbotService chatbotService;

    private final RedisChatbotUtil redisChatbotUtil;

    @PostMapping("/messages")
    public ChatbotResponseWrapper message(
            @RequestBody @Valid ChatbotReqDTO request,
            @AuthenticationPrincipal CustomUserDetails user) {

        // 사용자 입력 데이터 레디스에 저장
        TokenReqDTO tokenReqDTO = chatbotService.saveRequest(user, request);

        // 레디스에 저장된 intention 불러오기
        ChatbotRedisDataDTO chatbotRedisDataDTO = redisChatbotUtil.getChatbotData(tokenReqDTO.getToken());
        String intention = chatbotRedisDataDTO.getChatbotReqDTO().getIntention();

        // intention이 null 아닌 경우 -> createResponseJson() 호출
        if (intention != null) {
            return chatbotService.createResponseJson(user, tokenReqDTO);
        }

        // intention이 null인 경우
        // analyzeIntention() 호출해서 텍스트 의도 분석 후
        // createResponseJson() 호출
        else {
            chatbotService.analyzeIntention(user, tokenReqDTO);
            return chatbotService.createResponseJson(user, tokenReqDTO);
        }
    }

    @PostMapping("/message")
    public void accept(
            @RequestBody TokenReqDTO token,
            @RequestParam Boolean accept, Boolean isAlarmed,
            @AuthenticationPrincipal CustomUserDetails user) {
        System.out.println("controller");
        System.out.println("token: "+token);
        System.out.println("accept: "+accept);
        System.out.println("isAlarmed: "+isAlarmed);
        chatbotService.validateAcceptAlarm(user, accept, isAlarmed, token);
    }
}