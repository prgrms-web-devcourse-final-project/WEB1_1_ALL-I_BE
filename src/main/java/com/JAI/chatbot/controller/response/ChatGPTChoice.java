package com.JAI.chatbot.controller.response;

import com.JAI.chatbot.controller.ChatGPTMessage;

public record ChatGPTChoice(
    int index,
    ChatGPTMessage message
) {}
