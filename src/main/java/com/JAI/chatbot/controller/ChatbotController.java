package com.JAI.chatbot.controller;

import com.JAI.chatbot.RedisChatbotUtil;
import com.JAI.chatbot.controller.dto.ChatbotRedisDataDTO;
import com.JAI.chatbot.controller.dto.request.ChatbotReqDTO;
import com.JAI.chatbot.controller.dto.request.TokenReqDTO;
import com.JAI.chatbot.controller.dto.response.ChatbotResponseWrapper;
import com.JAI.chatbot.service.ChatbotService;
import com.JAI.global.controller.ApiResponse;
import com.JAI.user.service.dto.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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
    @Operation(summary = "챗봇 응답 요청", description = "챗봇 응답 요청 API")
    @Parameters({
            @Parameter(name = "intention", description = "챗봇 응답 요청 의도(NOT NULL)", examples = {
                    @ExampleObject(name = "Plan Recommendation", value = "PLAN_RECOMMENDATION"),
                    @ExampleObject(name = "Todo", value = "TODO"),
                    @ExampleObject(name = "Event", value = "EVENT")
            }),
            @Parameter(name = "categoryId", description = "카테고리 아이디", example = "514220be-71d8-4efc-8649-4a2a3a076f46"),
            @Parameter(name = "prompt", description = "챗봇 응답 요청 메시지", examples = {
                    @ExampleObject(name = "일정 자동 생성", value = "다음주 수요일부터 내년 1월까지 매주 수요일 13시에 개발 회의 일정 잡아줘"),
                    @ExampleObject(name = "투두 자동 생성", value = "이번주 수요일에 Java 과제 제출 투두 만들어줘"),
                    @ExampleObject(name = "계획 추천", value = "12월 안에 한강 작가의 채식주의자 완독하고 싶은데 계획 추천해줘. 나는 주로 주말에 시간이 많아")
            })
    })
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

    @Operation(summary = "챗봇 응답 투두와 개인 일정에 반영", description = "챗봇 응답 투두와 개인 일정 반영 API")
    @Parameter(name = "token", description = "챗봇 응답이 저장된 저장소 토큰", example = "f1a430ef-2d35-4849-80f5-903d3be36fa2")
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