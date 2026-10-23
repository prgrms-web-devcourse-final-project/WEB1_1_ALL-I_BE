package com.JAI.chatbot.approval.dto;

import com.JAI.chatbot.controller.dto.ChatbotRedisDataDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.IntStream;

/** Identity is fixed before publishing READY; list order is part of the proposal. */
public final class ProposalIdentity {
    private ProposalIdentity() { }
    public static UUID itemId(String token, String type, int index) {
        return UUID.nameUUIDFromBytes((token + ":" + type + ":" + index).getBytes(StandardCharsets.UTF_8));
    }
    private static ChatbotRedisDataDTO payload(ChatbotRedisDataDTO data) {
        return ChatbotRedisDataDTO.builder().token(data.getToken()).userId(data.getUserId())
                .chatbotReqDTO(data.getChatbotReqDTO()).chatbotEventRespDTO(data.getChatbotEventRespDTO())
                .chatbotTodoRespDTO(data.getChatbotTodoRespDTO()).build();
    }
    public static String hash(ChatbotRedisDataDTO data, ObjectMapper mapper) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(mapper.writeValueAsBytes(payload(data))));
        } catch (JsonProcessingException | NoSuchAlgorithmException e) {
            throw new IllegalStateException("Cannot hash approval proposal", e);
        }
    }
    public static ChatbotRedisDataDTO ready(ChatbotRedisDataDTO data, ObjectMapper mapper) {
        boolean event = data.getChatbotEventRespDTO() != null;
        int size = event ? data.getChatbotEventRespDTO().size()
                : data.getChatbotTodoRespDTO() == null ? 0 : data.getChatbotTodoRespDTO().size();
        return ChatbotRedisDataDTO.builder().approvalState("READY").proposalHash(hash(data, mapper))
                .proposalItemIds(IntStream.range(0,size).mapToObj(i -> itemId(data.getToken(),event?"EVENT":"TODO",i)).toList())
                .token(data.getToken()).userId(data.getUserId()).chatbotReqDTO(data.getChatbotReqDTO())
                .chatbotEventRespDTO(data.getChatbotEventRespDTO()).chatbotTodoRespDTO(data.getChatbotTodoRespDTO()).build();
    }
}
