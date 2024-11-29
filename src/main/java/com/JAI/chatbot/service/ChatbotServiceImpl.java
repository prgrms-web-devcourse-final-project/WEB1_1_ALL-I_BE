package com.JAI.chatbot.service;

import com.JAI.chatbot.RedisChatbotUtil;
import com.JAI.chatbot.controller.dto.ChatGPTMessageDTO;
import com.JAI.chatbot.controller.dto.ChatbotRedisDataDTO;
import com.JAI.chatbot.controller.dto.request.ChatbotReqDTO;
import com.JAI.chatbot.controller.dto.request.TokenReqDTO;
import com.JAI.chatbot.controller.dto.response.ChatGPTRespDTO;
import com.JAI.chatbot.controller.dto.response.ChatbotEventRespDTO;
import com.JAI.chatbot.controller.dto.response.ChatbotResponseWrapper;
import com.JAI.chatbot.controller.dto.response.ChatbotTodoRespDTO;
import com.JAI.chatbot.domain.JsonConstants;
import com.JAI.chatbot.mapper.ChatbotMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatbotServiceImpl implements ChatbotService {

    @Autowired
    ChatGPTService chatGPTService;

    @Autowired
    RedisChatbotUtil redisChatbotUtil;

    @Autowired
    ChatbotMapper chatbotMapper;

    @Override
    public TokenReqDTO saveRequest (ChatbotReqDTO request) {

        // userId 불러오는 로직으로 수정
        UUID userId = UUID.randomUUID();

        // 레디스 키, 토큰 생성
        String redisKey = UUID.randomUUID().toString();  // 레디스 키 생성
        String redisToken = UUID.randomUUID().toString();  // 레디스 토큰 생성
        TokenReqDTO token = TokenReqDTO.builder().token(redisToken).build(); // 레디스 토큰 DTO에 담기

        redisChatbotUtil.saveChatbotData(redisKey, redisToken, userId, request);
        return token;
    }

    @Override
    public ChatbotResponseWrapper<?> createResponseJson(TokenReqDTO token) {

        List<ChatGPTMessageDTO> messages = new ArrayList<>();

        // 현재 유저의 userId가 레디스에 저장된 userId와 동일할 경우에만 레디스에 접근 가능하도록 로직 수정

        // 레디스에서 key 이용해서 prompt, intention 가져오기
        System.out.println("createResponseJson");
        System.out.println("token: "+token.getToken());

        ChatbotRedisDataDTO chatbotRedisDataDTO = redisChatbotUtil.getChatbotData(token.getToken());
        String prompt = chatbotRedisDataDTO.getChatbotReqDTO().getPrompt();
        String intention = chatbotRedisDataDTO.getChatbotReqDTO().getIntention();

        // intention에 따라 prompt 수정
        switch (intention) {
            case "EVENT":
                prompt += JsonConstants.EVENT; // 일정 자동 기입 포맷 추가
                break;
            case "TODO":
            case "PLAN_RECOMMENDATION":
                prompt += JsonConstants.TODO; // 투두 자동 기입 포맷 추가
                break;
            default:
                break;
        }

        ChatGPTMessageDTO message = new ChatGPTMessageDTO("user", prompt);
        messages.add(message);

        // ChatGPT에 응답 요청
        ChatGPTRespDTO chatGPTRespDTO = chatGPTService.postMessage(messages, token);

        if (intention.equals("EVENT")) {
            // ChatGPT 응답 -> Chatbot 응답
            List<ChatbotEventRespDTO> chatbotEventResp = chatbotMapper.toChatbotEventResp(chatGPTRespDTO);
            ChatbotResponseWrapper<ChatbotEventRespDTO> chatbotEventRespWrapper =
                    new ChatbotResponseWrapper<>(chatbotEventResp);


            // Chatbot 응답 레디스에 저장
            redisChatbotUtil.saveChatbotEventResp(token.getToken(), chatbotEventResp);

            // Chatbot 응답 반환
            return chatbotEventRespWrapper;
        }
        else if(intention.equals("TODO") || intention.equals("PLAN_RECOMMENDATION")) {
            // ChatGPT 응답 -> Chatbot 응답
            List<ChatbotTodoRespDTO> chatbotTodoResp = chatbotMapper.toChatbotTodoResp(chatGPTRespDTO);

            ChatbotResponseWrapper<ChatbotTodoRespDTO> chatbotTodoRespWrapper =
                    new ChatbotResponseWrapper<>(chatbotTodoResp);


            // Chatbot 응답 레디스에 저장
            redisChatbotUtil.saveChatbotTodoResp(token.getToken(), chatbotTodoResp);

            // Chatbot 응답 반환
            return chatbotTodoRespWrapper;
        }
        else {
            throw new IllegalArgumentException("Unsupported intention: " + intention);
        }
    }

    @Override
    public void analyzeIntention(TokenReqDTO token) {

        List<ChatGPTMessageDTO> messages = new ArrayList<>();

        // 현재 유저의 userId가 레디스에 저장된 userId와 동일할 경우에만 레디스에 접근 가능하도록 로직 수정
        // 레디스에서 key 이용해서 prompt 가져오기
        System.out.println("analyzeIntention");
        System.out.println("token: "+token.getToken());
        ChatbotRedisDataDTO chatbotRedisDataDTO = redisChatbotUtil.getChatbotData(token.getToken());
        String prompt = chatbotRedisDataDTO.getChatbotReqDTO().getPrompt();

        // prompt에 텍스트 의도 분석 요청 텍스트 붙이기
        prompt += JsonConstants.INTENTION;

        ChatGPTMessageDTO message = new ChatGPTMessageDTO("user", prompt);
        messages.add(message);

        // 텍스트 의도 분석 요청
        String intention = chatGPTService.findIntention(messages, token);

        // 레디스에 intention 저장하기
        ChatbotReqDTO newChatbotReqDTO = ChatbotReqDTO.builder()
                .intention(intention)
                .category(chatbotRedisDataDTO.getChatbotReqDTO().getCategory())
                .prompt(chatbotRedisDataDTO.getChatbotReqDTO().getPrompt())
                .build();
        redisChatbotUtil.saveChatbotReq(token.getToken(), newChatbotReqDTO);
    }

    @Override
    public void validateAcceptAlarm(Boolean accept, Boolean alarm, TokenReqDTO token) {
        System.out.println("validateAcceptAlarm");
        System.out.println("token: "+token.getToken());
        // 수락했을 경우
        // token으로 레디스에 저장된 ChatGPT 응답 객체, Intention 찾고 saveEvent() 혹은 saveTodo() 호출
        if(accept) {
            ChatbotRedisDataDTO chatbotRedisDataDTO = redisChatbotUtil.getChatbotData(token.getToken());
            if (chatbotRedisDataDTO.getChatbotReqDTO().getIntention().equals("EVENT")) {
                saveEvent(chatbotRedisDataDTO, alarm, token);
            } else {
                saveTodo(chatbotRedisDataDTO, token);
            }
        }

        // 거절했을 경우
        // token으로 해당 데이터 레디스에서 삭제
        else {
            redisChatbotUtil.deleteChatbotData(token.getToken());
        }
    }

    @Override
    public ChatbotEventRespDTO saveEvent(ChatbotRedisDataDTO chatbotRedisDataDTO, Boolean alarm, TokenReqDTO token) {

        // 레디스에 저장되어 있던 Chatbot 응답 데이터 불러오기
        ChatbotRedisDataDTO chatbotData = redisChatbotUtil.getChatbotData(token.getToken());
        List<ChatbotEventRespDTO> chatbotEventRespDTO = chatbotData.getChatbotEventRespDTO();

        // 개인 일정 DB에 저장하는 함수 호출
        return null;
    }

    @Override
    public ChatbotTodoRespDTO saveTodo(ChatbotRedisDataDTO chatbotRedisDataDTO, TokenReqDTO token) {

        // 레디스에 저장되어 있던 Chatbot 응답 데이터 불러오기
        ChatbotRedisDataDTO chatbotData = redisChatbotUtil.getChatbotData(token.getToken());
        List<ChatbotTodoRespDTO> chatbotTodoRespDTO = chatbotData.getChatbotTodoRespDTO();

        // 개인 투두 DB에 저장하는 함수 호출
        return null;

    }
}
