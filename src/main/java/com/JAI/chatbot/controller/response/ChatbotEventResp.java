package com.JAI.chatbot.controller.response;

import java.time.*;

public record ChatbotEventResp(
        String title,
        LocalDate startDate,
        LocalTime startTime,
        LocalDate endDate,
        LocalTime endTime

) {}
