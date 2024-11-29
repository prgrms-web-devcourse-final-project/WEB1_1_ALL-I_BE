package com.JAI.chatbot.controller.dto.request;

import com.JAI.chatbot.controller.dto.ChatGPTMessageDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatGPTReqDTO {

    @NotBlank
    private String model;

    @NotBlank
    private List<ChatGPTMessageDTO> messages;
}

