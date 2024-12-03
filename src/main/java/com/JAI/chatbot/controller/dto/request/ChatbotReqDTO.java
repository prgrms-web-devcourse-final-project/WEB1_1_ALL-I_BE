package com.JAI.chatbot.controller.dto.request;

import com.JAI.category.domain.Category;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatbotReqDTO {

    private String intention;

    private UUID categoryId;

    @NotBlank
    private String prompt;

}
