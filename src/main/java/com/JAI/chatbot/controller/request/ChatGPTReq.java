package com.JAI.chatbot.controller.request;

import com.JAI.chatbot.controller.ChatGPTMessage;
import java.util.*;

public record ChatGPTReq(
        String model,
        List<ChatGPTMessage> messages
) {}

