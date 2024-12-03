package com.JAI.chatbot.controller.dto.response;

import lombok.*;
import java.time.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatbotTodoRespDTO {
    private String title;
    private LocalDate date;
    private LocalTime time;

}
