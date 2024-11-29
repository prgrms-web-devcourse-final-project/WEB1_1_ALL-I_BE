package com.JAI.chatbot;

import com.JAI.chatbot.controller.dto.ChatbotRedisDataDTO;
import com.JAI.chatbot.controller.dto.request.ChatbotReqDTO;
import com.JAI.chatbot.controller.dto.response.ChatbotEventRespDTO;
import com.JAI.chatbot.controller.dto.response.ChatbotTodoRespDTO;
import com.JAI.global.util.RedisUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RedisChatbotUtil {

    @Autowired
    private final RedisUtil redisUtil;

    private final ObjectMapper objectMapper;

    private static final String PREFIX_CHATBOT = "chatbot:";
    private static final String PREFIX_KEY_TOKEN = "chatbot_token:";
    private static final Long ttlSeconds = 3600L;


    // token을 이용해 저장된 key값을 조회
    public String getKeyByToken(String token) {
        System.out.println("getKeyByToken");
        System.out.println("token: "+token);
        String key = redisUtil.get(PREFIX_KEY_TOKEN + token);
        if (key == null) {
            throw new RuntimeException("토큰에 해당하는 키가 Redis에 존재하지 않습니다. 토큰: " + token);
        }
        return key;
    }

    // ChatbotRedisData를 Redis에 저장
    public void saveChatbotData(String key, String token, UUID userId, ChatbotReqDTO chatbotReqDTO) {
        System.out.println("saveChatbotData");
        System.out.println("key: "+key);
        System.out.println("token: "+token);
        try {
            ChatbotRedisDataDTO chatbotData = ChatbotRedisDataDTO.builder()
                    .token(token)
                    .userId(userId)
                    .chatbotReqDTO(chatbotReqDTO)
                    .chatbotEventRespDTO(null)
                    .chatbotTodoRespDTO(null)
                    .build();

            String json = objectMapper.writeValueAsString(chatbotData);     // DTO -> JSON 변환
            redisUtil.save(PREFIX_CHATBOT + key, json, ttlSeconds);    // Redis 저장

            // token을 사용하여 Redis에서 key를 찾을 수 있도록 매핑
            redisUtil.save(PREFIX_KEY_TOKEN + token, key, ttlSeconds); // 토큰에 대한 key 저장
        } catch (JsonProcessingException e) {
            throw new RuntimeException("ChatbotRedisData 직렬화 실패", e);
        }
    }

    // Redis에서 ChatbotRedisData를 조회
    public ChatbotRedisDataDTO getChatbotData(String token) {
        System.out.println("getChatbotData");
        System.out.println("token: "+token);

        String key = getKeyByToken(token);
        System.out.println("key: "+key);

        try {
            String json = redisUtil.get(PREFIX_CHATBOT + key); // Redis에서 JSON 가져오기
            if (json == null) return null; // 데이터가 없으면 null 반환
            return objectMapper.readValue(json, ChatbotRedisDataDTO.class); // JSON -> DTO 변환
        } catch (JsonProcessingException e) {
            throw new RuntimeException("ChatbotRedisData 역직렬화 실패", e);
        }
    }

    public void saveChatbotReq(String token, ChatbotReqDTO chatbotReqDTO) {
        System.out.println("saveChatbotReq");
        System.out.println("token: "+token);

        String key = getKeyByToken(token);
        System.out.println("key: "+key);

        ChatbotRedisDataDTO existingData = getChatbotData(token);
        if(existingData == null) {
            throw new RuntimeException("ChatbotData with key " + key + " does not exist");
        }

        existingData = ChatbotRedisDataDTO.builder()
                .token(existingData.getToken())
                .userId(existingData.getUserId())
                .chatbotReqDTO(chatbotReqDTO)
                .chatbotEventRespDTO(existingData.getChatbotEventRespDTO())
                .chatbotTodoRespDTO(existingData.getChatbotTodoRespDTO())
                .build();

        saveChatbotData(token, existingData);
    }

    // ChatbotEventResp 저장/수정 (기존 ChatbotRedisData 업데이트)
    public void saveChatbotEventResp(String token, List<ChatbotEventRespDTO> chatbotEventRespDTO) {
        System.out.println("saveChatbotEventResp");
        System.out.println("token: "+token);
        // token을 통해 key 조회
        String key = getKeyByToken(token);
        System.out.println("key: "+key);

        // 기존 데이터 가져오기
        ChatbotRedisDataDTO existingData = getChatbotData(token);
        if (existingData == null) {
            throw new RuntimeException("ChatbotData with key " + key + " does not exist");
        }

        // ChatbotEventResp를 기존 데이터에 추가
        existingData = ChatbotRedisDataDTO.builder()
                .token(existingData.getToken())
                .userId(existingData.getUserId())
                .chatbotReqDTO(existingData.getChatbotReqDTO())
                .chatbotEventRespDTO(chatbotEventRespDTO)
                .chatbotTodoRespDTO(existingData.getChatbotTodoRespDTO())
                .build();

        // 수정된 데이터 다시 Redis에 저장
        saveChatbotData(token, existingData); // 기존 ChatbotData에 EventResp 추가 후 덮어쓰기
    }

    // ChatbotTodoResp 저장/수정 (기존 ChatbotRedisData 업데이트)
    public void saveChatbotTodoResp(String token, List<ChatbotTodoRespDTO> chatbotTodoRespDTO) {
        System.out.println("saveChatbotTodoResp");
        System.out.println("token: "+token);

        // token을 통해 key 조회
        String key = getKeyByToken(token);
        System.out.println("key: "+key);

        // 기존 데이터 가져오기
        ChatbotRedisDataDTO existingData = getChatbotData(token);
        if (existingData == null) {
            throw new RuntimeException("ChatbotData with key " + key + " does not exist");
        }

        // ChatbotTodoResp를 기존 데이터에 추가
        existingData = ChatbotRedisDataDTO.builder()
                .token(existingData.getToken())
                .userId(existingData.getUserId())
                .chatbotReqDTO(existingData.getChatbotReqDTO())
                .chatbotEventRespDTO(existingData.getChatbotEventRespDTO())
                .chatbotTodoRespDTO(chatbotTodoRespDTO)
                .build();

        // 수정된 데이터 다시 Redis에 저장
        saveChatbotData(token, existingData); // 기존 ChatbotData에 TodoResp 추가 후 덮어쓰기
    }

    // ChatbotRedisData를 Redis에 저장하는 내부 메서드
    private void saveChatbotData(String token, ChatbotRedisDataDTO chatbotData) {
        try {
            System.out.println("saveChatbotData");
            System.out.println("token: "+token);

            String key = getKeyByToken(token);
            System.out.println("key: "+key);

            String json = objectMapper.writeValueAsString(chatbotData); // DTO -> JSON 변환
            redisUtil.save(PREFIX_CHATBOT + key, json, ttlSeconds);    // Redis 저장
        } catch (JsonProcessingException e) {
            throw new RuntimeException("ChatbotRedisData 직렬화 실패", e);
        }
    }

    public void deleteChatbotData(String token) {
        System.out.println("deleteChatbotData");
        System.out.println("token: "+token);

        String key = getKeyByToken(token);
        System.out.println("key: "+key);
        redisUtil.delete(key);
    }
}
