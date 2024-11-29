package com.JAI.chatbot.controller.dto.response;

import com.JAI.chatbot.controller.dto.ChatGPTMessageDTO;

import lombok.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatGPTChoiceDTO {
    private int index;
    private ChatGPTMessageDTO message;
}
