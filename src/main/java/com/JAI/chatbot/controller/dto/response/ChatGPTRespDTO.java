package com.JAI.chatbot.controller.dto.response;

import java.util.List;
import lombok.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatGPTRespDTO {

    private List<ChatGPTChoiceDTO> choices;

}


