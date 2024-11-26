package com.JAI.chatbot.service;

import com.JAI.chatbot.controller.ChatGPTMessage;
import com.JAI.chatbot.controller.request.ChatbotReq;
import com.JAI.chatbot.controller.request.TokenReq;
import com.JAI.chatbot.controller.response.ChatGPTResp;
import com.JAI.chatbot.controller.response.ChatbotEventResp;
import com.JAI.chatbot.controller.response.ChatbotTodoResp;
import com.JAI.chatbot.domain.Intention;
import com.JAI.chatbot.domain.JsonConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatbotServiceImpl implements ChatbotService {

    ChatGPTService chatGPTService;

    @Override
    public void validateRequest (ChatbotReq request) {

        Intention intention;

        if(request.prompt() == null){ throw new RuntimeException("잘못된 요청입니다"); }
        else {
            if(request.intention().equals("EVENT")) {
                intention = Intention.AUTOMATIC_EVENT_ENTRY;

                // 레디스 저장 로직 추가 - 의도, 카테고리, 사용자 입력 텍스트
                TokenReq token = new TokenReq(UUID.randomUUID());

                createResponseJson(intention, token);
            }
            else if(request.intention().equals("TODO")) {
                intention = Intention.AUTOMATIC_TODO_ENTRY;

                // 레디스 저장 로직 추가 - 의도, 카테고리, 사용자 입력 텍스트
                TokenReq token = new TokenReq(UUID.randomUUID());

                createResponseJson(intention, token);
            }
            else {
                // 레디스 저장 로직 추가 - 카테고리, 사용자 입력 텍스트
                TokenReq token = new TokenReq(UUID.randomUUID());
                analyzeIntention(token);
            }
        }

    }

    @Override
    public void createResponseJson(Intention intention, TokenReq token) {
        List<ChatGPTMessage> messages = new ArrayList<>();

        // 레디스에서 token 이용해서 prompt 가져와서 작업하기

//        if (intention == Intention.AUTOMATIC_EVENT_ENTRY) {
//            prompt += JsonConstants.EVENT; // 일정 자동 기입 포맷 추가
//        }
//        else if (intention == Intention.AUTOMATIC_TODO_ENTRY || intention == Intention.PLAN_RECOMMENDATION ) {
//            prompt += JsonConstants.TODO; // 투두 자동 기입 포맷 추가
//        }
//
//        ChatGPTMessage message = new ChatGPTMessage("user", prompt);
//        messages.add(message);

//        chatGPTService.postMessage(messages, token);

        // converter

    }

    @Override
    public void analyzeIntention(TokenReq token) {

        // 레디스에서 token 이용해서 prompt 가져와서 작업하기

//        prompt += JsonConstants.INTENTION;
//
//        List<ChatGPTMessage> messages = new ArrayList<>();
//
//        ChatGPTMessage message = new ChatGPTMessage("user", prompt);
//        messages.add(message);
//
//        chatGPTService.postMessage(messages, token);
    }

    @Override
    public void validateAcceptAlarm(Boolean accept, Boolean alarm, TokenReq token) {
        // 수락했을 경우
        // token으로 레디스에 저장된 ChatGPT 응답 객체, Intention 찾고 saveEvent() 혹은 saveTodo() 호출

        // 거절했을 경우
        // token으로 해당 데이터 레디스에서 삭제
    }

    @Override
    public ChatbotEventResp saveEvent(ChatGPTResp response, Boolean alarm, TokenReq token) {
        return null;
        // 개인 일정 DB에 저장하는 함수 호출
    }

    @Override
    public ChatbotTodoResp saveTodo(ChatGPTResp response, TokenReq token) {
        return null;
        // 개인 투두 DB에 저장하는 함수 호출
    }
}
