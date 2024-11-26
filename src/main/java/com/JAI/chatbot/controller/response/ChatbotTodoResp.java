package com.JAI.chatbot.controller.response;

import java.time.*;
public record ChatbotTodoResp(
        String title,
        LocalDate date,
        LocalTime startTime
) {}
