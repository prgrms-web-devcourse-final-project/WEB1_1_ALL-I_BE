package com.JAI.chatbot.service;

import com.JAI.chatbot.RedisChatbotUtil;
import com.JAI.chatbot.approval.service.ApprovalOrchestrator;
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
import com.JAI.user.service.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatbotServiceImpl implements ChatbotService {

    private final ChatGPTService chatGPTService;

    private final RedisChatbotUtil redisChatbotUtil;

    private final ChatbotMapper chatbotMapper;
    private final ApprovalOrchestrator approvalOrchestrator;



    @Override
    public UUID validateUser(CustomUserDetails user, ChatbotRedisDataDTO chatbotRedisDataDTO) {
//        System.out.println("validateUser");

        // 레디스에 저장된 userId와 현재 로그인한 userId가 동일해야 접근 가능
        UUID userId = chatbotRedisDataDTO.getUserId(); // 레디스에 저장되어 있는 userId(데이터 입력한 유저)
        UUID currUserId = user.getUser().getUserId();  // 현재 로그인한 userId

//        System.out.println("userId: "+userId);
//        System.out.println("currUserId: "+currUserId);

        if (!currUserId.toString().equals(userId.toString())) {
            throw new ChatbotForbiddenException("해당 유저는 접근할 수 없는 데이터입니다");
        }

        return currUserId;
    }

    @Override
    public TokenReqDTO saveRequest (CustomUserDetails user, ChatbotReqDTO request) {
//        System.out.println("saveRequest");

        if (request.getPrompt() == null) {
            throw new ChatbotBadRequestException("프롬프트를 입력해주세요");
        }

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
        System.out.println("intention: "+intention);

        // intention이 '그외'인 경우 예외 처리
        if(intention.equals("EXCEPTION")){
            throw new ChatbotBadRequestException("일정, 투두 자동 기입 혹은 계획 추천과 관련 메세지를 입력해주세요");
        }

        else {
            if(chatbotRedisDataDTO.getChatbotReqDTO().getIntention() == null){
                // 레디스에 intention 저장하기
                ChatbotReqDTO newChatbotReqDTO = ChatbotReqDTO.builder()
                        .intention(intention)
                        .categoryId(chatbotRedisDataDTO.getChatbotReqDTO().getCategoryId())
                        .prompt(chatbotRedisDataDTO.getChatbotReqDTO().getPrompt())
                        .build();
                redisChatbotUtil.saveChatbotReq(token.getToken(), newChatbotReqDTO);
            }
        }
    }

    @Override
    public void validateAcceptAlarm(CustomUserDetails user, Boolean accept, Boolean alarm, TokenReqDTO token) {
        if (token == null) throw new ChatbotBadRequestException("토큰을 넣어주세요");
        approvalOrchestrator.process(user.getUser().getUserId(), token.getToken(),
                Boolean.TRUE.equals(accept), Boolean.TRUE.equals(alarm));
    }

    @Override
    public void saveEvent(CustomUserDetails user, ChatbotRedisDataDTO ignored,
                          Boolean alarm, TokenReqDTO token) {
        if (token == null) throw new ChatbotBadRequestException("토큰을 넣어주세요");
        approvalOrchestrator.process(user.getUser().getUserId(), token.getToken(), true,
                Boolean.TRUE.equals(alarm));
    }

    @Override
    public void saveTodo(CustomUserDetails user, ChatbotRedisDataDTO ignored,
                         Boolean alarm, TokenReqDTO token) {
        if (token == null) throw new ChatbotBadRequestException("토큰을 넣어주세요");
        approvalOrchestrator.process(user.getUser().getUserId(), token.getToken(), true,
                Boolean.TRUE.equals(alarm));
    }

}
