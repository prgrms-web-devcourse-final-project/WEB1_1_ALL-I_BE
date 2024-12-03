package com.JAI.chatbot.controller.dto;

import com.JAI.chatbot.controller.dto.request.ChatbotReqDTO;
import com.JAI.chatbot.controller.dto.response.ChatbotEventRespDTO;
import com.JAI.chatbot.controller.dto.response.ChatbotTodoRespDTO;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatbotRedisDataDTO {

    private String token;
    private UUID userId;
    private ChatbotReqDTO chatbotReqDTO;
    private List<ChatbotEventRespDTO> chatbotEventRespDTO;
    private List<ChatbotTodoRespDTO> chatbotTodoRespDTO;

}
