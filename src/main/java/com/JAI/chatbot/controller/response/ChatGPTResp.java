package com.JAI.chatbot.controller.response;

import java.util.List;

public record ChatGPTResp(
        List<ChatGPTChoice> choices
){}


