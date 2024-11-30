package com.JAI.chatbot.mapper;

import com.JAI.chatbot.controller.dto.response.ChatGPTRespDTO;
import com.JAI.chatbot.controller.dto.response.ChatbotEventRespDTO;
import com.JAI.chatbot.controller.dto.response.ChatbotTodoRespDTO;
import com.JAI.event.DTO.request.PersonalEventCreateReqDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ChatbotMapper {


    private final ObjectMapper mapper;

    public List<ChatbotEventRespDTO> toChatbotEventResp(ChatGPTRespDTO chatGPTRespDTO) {
        try {
            // ChatGPT 응답의 content(JSON 문자열)를 추출
            String content = chatGPTRespDTO.getChoices().get(0).getMessage().getContent();

            // content를 JSON으로 파싱하여 List<ChatbotEventResp>로 변환
            return mapper.readValue(
                    mapper.readTree(content).get("events").toString(),
                    new TypeReference<>() {}
            );
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to parse ChatGPT response to ChatbotEventResp list", e);
        }
    }

    public List<ChatbotTodoRespDTO> toChatbotTodoResp(ChatGPTRespDTO chatGPTRespDTO) {
        try {
            // ChatGPT 응답의 content(JSON 문자열)를 추출
            String content = chatGPTRespDTO.getChoices().get(0).getMessage().getContent();

            // content를 JSON으로 파싱하여 List<ChatbotTodoResp>로 변환
            return mapper.readValue(
                    mapper.readTree(content).get("todos").toString(),
                    new TypeReference<>() {}
            );
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to parse ChatGPT response to ChatbotTodoResp list", e);
        }
    }

    public PersonalEventCreateReqDTO toPersonalEventCreateReqDTO (
            UUID userId, String categoryId, Boolean alarm,
            ChatbotEventRespDTO chatbotEventRespDTO
            ) {

        PersonalEventCreateReqDTO personalEventCreateReqDTO = PersonalEventCreateReqDTO.builder()
                .title(chatbotEventRespDTO.getTitle())
                .startDate(chatbotEventRespDTO.getStartDate())
                .endDate(chatbotEventRespDTO.getEndDate())
                .startTime(chatbotEventRespDTO.getStartTime())
                .endTime(chatbotEventRespDTO.getEndTime())
                .isAlarmed(alarm)
                .userId(userId)
                .categoryId(UUID.fromString(categoryId))
                .build();

        return personalEventCreateReqDTO;
    }
}

