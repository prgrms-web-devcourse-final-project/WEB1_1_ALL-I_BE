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
import com.JAI.chatbot.exception.ChatbotBadRequestException;
import com.JAI.chatbot.exception.ChatbotForbiddenException;
import com.JAI.chatbot.mapper.ChatbotMapper;
import com.JAI.event.DTO.request.PersonalEventCreateReqDTO;
import com.JAI.event.service.PersonalEventService;
import com.JAI.user.service.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatbotServiceImpl implements ChatbotService {

    private final ChatGPTService chatGPTService;

    private final PersonalEventService personalEventService;

    private final RedisChatbotUtil redisChatbotUtil;

    private final ChatbotMapper chatbotMapper;



    @Override
    public UUID validateUser(CustomUserDetails user, ChatbotRedisDataDTO chatbotRedisDataDTO) {
        // 레디스에 저장된 userId와 현재 로그인한 userId가 동일해야 접근 가능
        UUID userId = chatbotRedisDataDTO.getUserId(); // 레디스에 저장되어 있는 userId(데이터 입력한 유저)
        UUID currUserId = user.getUser().getUserId();  // 현재 로그인한 userId
        if ( currUserId != userId ) {
            throw new ChatbotForbiddenException("해당 유저는 접근할 수 없는 데이터입니다.");
        }

        return currUserId;
    }

    @Override
    public TokenReqDTO saveRequest (CustomUserDetails user, ChatbotReqDTO request) {
        System.out.println("saveRequest");

        // userId 불러오기
        UUID userId = user.getUser().getUserId();

        // 레디스 키, 토큰 생성
        String redisKey = UUID.randomUUID().toString();  // 레디스 키 생성
        String redisToken = UUID.randomUUID().toString();  // 레디스 토큰 생성
        TokenReqDTO token = TokenReqDTO.builder().token(redisToken).build(); // 레디스 토큰 DTO에 담기

        redisChatbotUtil.saveChatbotData(redisKey, redisToken, userId, request);
        return token;
    }

    @Override
    public ChatbotResponseWrapper<?> createResponseJson(CustomUserDetails user,  TokenReqDTO token) throws ChatbotForbiddenException{

        System.out.println("createResponseJson");
        System.out.println("token: "+token.getToken());

        ChatbotRedisDataDTO chatbotRedisDataDTO = redisChatbotUtil.getChatbotData(token.getToken());

        // 레디스에 저장된 userId와 현재 로그인한 userId가 동일해야 접근 가능
        validateUser(user, chatbotRedisDataDTO);

        // 레디스에서 key 이용해서 prompt, intention 가져오기
        String prompt = chatbotRedisDataDTO.getChatbotReqDTO().getPrompt();
        String intention = chatbotRedisDataDTO.getChatbotReqDTO().getIntention();

        prompt += JsonConstants.DATE_INFO;

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

        List<ChatGPTMessageDTO> messages = new ArrayList<>();
        ChatGPTMessageDTO message = new ChatGPTMessageDTO("user", prompt);
        messages.add(message);

        // ChatGPT에 응답 요청
        ChatGPTRespDTO chatGPTRespDTO = chatGPTService.postMessage(messages, token);

        if (intention.equals("EVENT")) {
            // ChatGPT 응답 -> Chatbot 응답
            List<ChatbotEventRespDTO> chatbotEventRespList = chatbotMapper.toChatbotEventResp(chatGPTRespDTO);
            ChatbotResponseWrapper<ChatbotEventRespDTO> chatbotEventRespWrapper =
                    ChatbotResponseWrapper.<ChatbotEventRespDTO>builder()
                            .responses(chatbotEventRespList)
                            .tokenReqDTO(token)
                            .build();

            // Chatbot 응답 레디스에 저장
            redisChatbotUtil.saveChatbotEventResp(token.getToken(), chatbotEventRespList);

            // Chatbot 응답 반환
            return chatbotEventRespWrapper;
        }
        else if(intention.equals("TODO") || intention.equals("PLAN_RECOMMENDATION")) {
            // ChatGPT 응답 -> Chatbot 응답
            List<ChatbotTodoRespDTO> chatbotTodoRespList = chatbotMapper.toChatbotTodoResp(chatGPTRespDTO);
            ChatbotResponseWrapper<ChatbotTodoRespDTO> chatbotTodoRespWrapper =
                    ChatbotResponseWrapper.<ChatbotTodoRespDTO>builder()
                            .responses(chatbotTodoRespList)
                            .tokenReqDTO(token)
                            .build();

            // Chatbot 응답 레디스에 저장
            redisChatbotUtil.saveChatbotTodoResp(token.getToken(), chatbotTodoRespList);

            // Chatbot 응답 반환
            return chatbotTodoRespWrapper;
        }
        else {
            throw new ChatbotBadRequestException("일정 / 투두 자동 기입, 계획 추천과 관련된 명령을 해주세요");
        }
    }

    @Override
    public void analyzeIntention(CustomUserDetails user, TokenReqDTO token) {

        System.out.println("analyzeIntention");
        System.out.println("token: "+token.getToken());


        ChatbotRedisDataDTO chatbotRedisDataDTO = redisChatbotUtil.getChatbotData(token.getToken());

        // 레디스에 저장된 userId와 현재 로그인한 userId가 동일해야 접근 가능
        validateUser(user, chatbotRedisDataDTO);

        // 레디스에서 prompt 가져오기
        String prompt = chatbotRedisDataDTO.getChatbotReqDTO().getPrompt();

        // prompt에 텍스트 의도 분석 요청 텍스트 붙이기
        prompt += JsonConstants.INTENTION;

        List<ChatGPTMessageDTO> messages = new ArrayList<>();
        ChatGPTMessageDTO message = new ChatGPTMessageDTO("user", prompt);
        messages.add(message);

        // 텍스트 의도 분석 요청
        String intention = chatGPTService.findIntention(messages, token);

        // 레디스에 intention 저장하기
        ChatbotReqDTO newChatbotReqDTO = ChatbotReqDTO.builder()
                .intention(intention)
                .categoryId(chatbotRedisDataDTO.getChatbotReqDTO().getCategoryId())
                .prompt(chatbotRedisDataDTO.getChatbotReqDTO().getPrompt())
                .build();
        redisChatbotUtil.saveChatbotReq(token.getToken(), newChatbotReqDTO);
    }

    @Override
    public void validateAcceptAlarm(CustomUserDetails user, Boolean accept, Boolean alarm, TokenReqDTO token) {

        System.out.println("validateAcceptAlarm");
        System.out.println("token: "+token.getToken());

        ChatbotRedisDataDTO chatbotRedisDataDTO = redisChatbotUtil.getChatbotData(token.getToken());

        // 레디스에 저장된 userId와 현재 로그인한 userId가 동일해야 접근 가능
        validateUser(user, chatbotRedisDataDTO);

        // 수락했을 경우 -> token으로 레디스에 저장된 ChatGPT 응답 객체, Intention 찾고 saveEvent() 혹은 saveTodo() 호출
        if(accept) {
            if (chatbotRedisDataDTO.getChatbotReqDTO().getIntention().equals("EVENT")) {
                saveEvent(user, chatbotRedisDataDTO, alarm, token);
            } else {
                saveTodo(user, chatbotRedisDataDTO, alarm, token);
            }
        }
        // 거절했을 경우 -> token으로 해당 데이터 레디스에서 삭제
        else {
            redisChatbotUtil.deleteChatbotData(token.getToken());
        }
    }

    @Override
    public void saveEvent(CustomUserDetails user, ChatbotRedisDataDTO chatbotRedisDataDTO, Boolean alarm, TokenReqDTO token) {

        // 레디스에 저장된 userId와 현재 로그인한 userId가 동일해야 접근 가능
        UUID currUserId = validateUser(user, chatbotRedisDataDTO);

        System.out.println("사용자 접근 가능");

        // 레디스에 저장되어 있던 Chatbot 응답 데이터 불러오기
        ChatbotRedisDataDTO chatbotData = redisChatbotUtil.getChatbotData(token.getToken());
        UUID categoryId = chatbotData.getChatbotReqDTO().getCategoryId();
        List<ChatbotEventRespDTO> chatbotEventRespDTOs = chatbotData.getChatbotEventRespDTO();

        // 개인 일정 DB에 삽입
        for (ChatbotEventRespDTO chatbotEventRespDTO : chatbotEventRespDTOs) {
            PersonalEventCreateReqDTO personalEventCreateReqDTO =
                    chatbotMapper.toPersonalEventCreateReqDTO(currUserId, categoryId, alarm, chatbotEventRespDTO);

            personalEventService.createPersonalEvent(personalEventCreateReqDTO, currUserId);
        }

    }

    @Override
    public void saveTodo(CustomUserDetails user, ChatbotRedisDataDTO chatbotRedisDataDTO, Boolean alarm, TokenReqDTO token) {

        // 레디스에 저장되어 있던 Chatbot 응답 데이터 불러오기
        ChatbotRedisDataDTO chatbotData = redisChatbotUtil.getChatbotData(token.getToken());
        UUID userId = chatbotData.getUserId();
        UUID categoryId = chatbotData.getChatbotReqDTO().getCategoryId();
        List<ChatbotTodoRespDTO> chatbotTodoRespDTOs = chatbotData.getChatbotTodoRespDTO();

        // 개인 투두 DB에 삽입
        for (ChatbotTodoRespDTO chatbotTodoRespDTO : chatbotTodoRespDTOs) {

        }

        // 개인 투두 DB에 저장하는 함수 호출

    }

}
