package com.JAI.chatbot.controller;

import com.JAI.chatbot.controller.request.ChatbotReq;
import com.JAI.chatbot.controller.response.ChatGPTResp;
import com.JAI.chatbot.service.ChatbotService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chatbot")
@RequiredArgsConstructor
public class ChatbotController {

    private final ChatbotService chatbotService;

    @PostMapping("/messages")
    public ChatGPTResp message(@RequestBody @Validated ChatbotReq request) {
        chatbotService.validateRequest(request);
        return null;
    }

}