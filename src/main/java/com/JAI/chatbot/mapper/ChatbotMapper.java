package com.JAI.chatbot.mapper;

import com.JAI.chatbot.controller.dto.response.ChatGPTRespDTO;
import com.JAI.chatbot.controller.dto.response.ChatbotEventRespDTO;
import com.JAI.chatbot.controller.dto.response.ChatbotTodoRespDTO;
import com.JAI.chatbot.exception.ChatbotBadRequestException;
import com.JAI.chatbot.exception.ChatbotUnprocessableEntityException;
import com.JAI.event.DTO.request.PersonalEventCreateReqDTO;
import com.JAI.todo.controller.request.PersonalTodoCreateReq;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ChatbotMapper {

    private final ObjectMapper mapper;

    public List<ChatbotEventRespDTO> toChatbotEventResp(ChatGPTRespDTO chatGPTRespDTO) {
        try {
            // ChatGPT 응답의 content(JSON 문자열)를 추출
            String content = chatGPTRespDTO.getChoices().get(0).getMessage().getContent();

            // content에서 JSON 객체만 추출 (정규식 사용)
            String jsonContent = extractJson(content);
            if (jsonContent == null) {
                throw new ChatbotUnprocessableEntityException("ChatGPT 응답에서 유효한 JSON을 찾지 못했습니다");
            }

            // JSON을 파싱하여 List<ChatbotEventRespDTO>로 변환
            List<ChatbotEventRespDTO> events = mapper.readValue(
                    mapper.readTree(jsonContent).get("events").toString(),
                    new TypeReference<>() {}
            );

            // 검증 및 처리 로직 추가
            return events.stream()
                    .map(event -> {
                        if (event.getStartDate() == null) {
                            throw new ChatbotBadRequestException("startDate는 null일 수 없습니다");
                        }
                        if (event.getEndDate() == null) {
                            // endDate가 null이면 startDate와 동일하게 설정
                            return ChatbotEventRespDTO.builder()
                                    .title(event.getTitle())
                                    .startDate(event.getStartDate())
                                    .endDate(event.getStartDate())
                                    .startTime(event.getStartTime())
                                    .endTime(event.getEndTime())
                                    .build();
                        }
                        return event; // 기본적으로 그대로 반환
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ChatbotUnprocessableEntityException("ChatGPT 응답이 적절하지 않습니다");
        }
    }

    public List<ChatbotTodoRespDTO> toChatbotTodoResp(ChatGPTRespDTO chatGPTRespDTO) {
        try {
            // ChatGPT 응답의 content(JSON 문자열)를 추출
            String content = chatGPTRespDTO.getChoices().get(0).getMessage().getContent();

            // content에서 JSON 객체만 추출 (정규식 사용)
            String jsonContent = extractJson(content);

            if (jsonContent == null) {
                throw new ChatbotUnprocessableEntityException("ChatGPT 응답에서 유효한 JSON을 찾지 못했습니다");
            }

            // JSON을 파싱하여 List<ChatbotTodoRespDTO>로 변환
            List<ChatbotTodoRespDTO> todos = mapper.readValue(
                    mapper.readTree(jsonContent).get("todos").toString(),
                    new TypeReference<>() {}
            );

            // 검증 및 처리 로직 추가
            return todos.stream()
                    .map(todo -> {
                        if (todo.getDate() == null) {
                            throw new ChatbotBadRequestException("date는 null일 수 없습니다");
                        }
                        return todo; // 기본적으로 그대로 반환
                    })
                    .collect(Collectors.toList());

        } catch (Exception e) {
            e.printStackTrace();
            throw new ChatbotUnprocessableEntityException("ChatGPT 응답이 적절하지 않습니다");
        }
    }

    public PersonalEventCreateReqDTO toPersonalEventCreateReqDTO (
            UUID categoryId, Boolean alarm,
            ChatbotEventRespDTO chatbotEventRespDTO
            ) {

        PersonalEventCreateReqDTO personalEventCreateReqDTO = PersonalEventCreateReqDTO.builder()
                .title(chatbotEventRespDTO.getTitle())
                .startDate(chatbotEventRespDTO.getStartDate())
                .endDate(chatbotEventRespDTO.getEndDate())
                .startTime(chatbotEventRespDTO.getStartTime())
                .endTime(chatbotEventRespDTO.getEndTime())
                .isAlarmed(alarm)
                .categoryId(categoryId)
                .build();

        return personalEventCreateReqDTO;
    }

    public PersonalTodoCreateReq toPersonalTodoCreateReq(ChatbotTodoRespDTO chatbotTodoRespDTO, UUID categoryId) {
        PersonalTodoCreateReq personalTodoCreateReq = PersonalTodoCreateReq.builder()
                .title(chatbotTodoRespDTO.getTitle())
                .date(chatbotTodoRespDTO.getDate())
                .startTime(chatbotTodoRespDTO.getTime())
                .categoryId(categoryId)
                .build();

        return personalTodoCreateReq;
    }

    private String extractJson(String content) {
//        System.out.println("extractJson");
//        System.out.println("content: "+content);
        // 정규식을 이용하여 JSON 객체 추출
        Pattern pattern = Pattern.compile("\\{.*\\}", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(content);

        if (matcher.find()) {
//            System.out.println("matcher: "+matcher);
//            System.out.println("matcher group: "+matcher.group());
            return matcher.group(); // 첫 번째 JSON 객체 반환
        }

        return null; // JSON 객체가 없는 경우 null 반환
    }

}

