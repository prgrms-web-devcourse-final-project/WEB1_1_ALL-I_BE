package com.JAI.chatbot.controller.dto;

import lombok.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatGPTMessageDTO {
    private String role;
    private String content;
}
