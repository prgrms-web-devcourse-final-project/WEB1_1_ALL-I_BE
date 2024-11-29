package com.JAI.chatbot.controller.dto.request;

import com.JAI.category.domain.Category;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatbotReqDTO {

    private String intention;

    private Category category;

    @NotBlank
    private String prompt;
}
