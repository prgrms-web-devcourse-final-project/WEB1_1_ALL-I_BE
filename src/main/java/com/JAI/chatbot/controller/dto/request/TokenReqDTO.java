package com.JAI.chatbot.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TokenReqDTO {
    @NotBlank
    private String token;


}
