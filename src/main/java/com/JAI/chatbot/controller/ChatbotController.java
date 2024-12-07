package com.JAI.chatbot.controller;

import com.JAI.chatbot.RedisChatbotUtil;
import com.JAI.chatbot.controller.dto.ChatbotRedisDataDTO;
import com.JAI.chatbot.controller.dto.request.ChatbotReqDTO;
import com.JAI.chatbot.controller.dto.request.TokenReqDTO;
import com.JAI.chatbot.controller.dto.response.ChatbotResponseWrapper;
import com.JAI.chatbot.service.ChatbotService;
import com.JAI.global.controller.ApiResponse;
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
    public ApiResponse<ChatbotResponseWrapper> message(
            @RequestBody @Valid ChatbotReqDTO request,
            @AuthenticationPrincipal CustomUserDetails user) {

        // 사용자 입력 데이터 레디스에 저장
        TokenReqDTO tokenReqDTO = chatbotService.saveRequest(user, request);

        // analyzeIntention() 호출해서 텍스트 의도 분석 후
        // createResponseJson() 호출
        chatbotService.analyzeIntention(user, tokenReqDTO);
        return ApiResponse.onCreateSuccess(chatbotService.createResponseJson(user, tokenReqDTO));
    }

    @PostMapping("/message")
    public ApiResponse<Void> accept(
            @RequestBody TokenReqDTO token,
            @RequestParam Boolean accept, Boolean isAlarmed,
            @AuthenticationPrincipal CustomUserDetails user) {
//        System.out.println("controller");
//        System.out.println("token: "+token);
//        System.out.println("accept: "+accept);
//        System.out.println("isAlarmed: "+isAlarmed);
        chatbotService.validateAcceptAlarm(user, accept, isAlarmed, token);

        return ApiResponse.onCreateSuccess();
    }
}