package com.JAI.chatbot.controller.dto.response;

import lombok.*;
import java.time.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatbotEventRespDTO {
    private String title;
    private LocalDate startDate;
    private LocalTime startTime;
    private LocalDate endDate;
    private LocalTime endTime;
}
