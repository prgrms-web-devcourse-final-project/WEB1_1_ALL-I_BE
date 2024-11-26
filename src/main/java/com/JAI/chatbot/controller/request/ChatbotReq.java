package com.JAI.chatbot.controller.request;

import com.JAI.chatbot.domain.Intention;
import com.JAI.category.domain.Category;

import java.util.UUID;

public record ChatbotReq(

        // 일정, 투두 선택
        Intention intention,

        // 카테고리
        Category category,

        // 사용자 입력 텍스트
        String prompt
) {}
