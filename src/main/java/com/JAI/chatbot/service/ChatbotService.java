package com.JAI.chatbot.service;

import com.JAI.chatbot.controller.request.ChatbotReq;
import com.JAI.chatbot.controller.request.TokenReq;
import com.JAI.chatbot.controller.response.ChatGPTResp;
import com.JAI.chatbot.controller.response.ChatbotEventResp;
import com.JAI.chatbot.controller.response.ChatbotTodoResp;
import com.JAI.chatbot.domain.Intention;

public interface ChatbotService {

    /**
     * 일정/투두, 카테고리, 프롬프트 입력 validation
     *
     * 프롬프트 null -> 예외 처리
     * 일정 -> 의도 : 일정 자동 기입 -> addJsonFormat() 호출
     * 투두 -> 의도 : 투두 자동 기입 -> addJsonFormat() 호출
     * 일정, 투두 입력X -> addIntentionText() 호출
     *
     * 의도, 카테고리, 프롬프트 레디스에 저장
     *
     * @param request : ChatbotReq(일정/투두, 카테고리, 프롬프트 입력)
    */
    public void validateRequest(ChatbotReq request);


    /**
     * 의도에 따라 JSON 포맷 삽입
     *
     * token으로 레디스에 저장된 prompt 불러와서 작업
     *
     * 일정 자동 기입 -> 일정 JSON 포맷 삽입
     * 투두 자동 기입 -> 투두 JSON 포맷 삽입
     * 계획 추천 -> 투두 JSON 포맷 삽입
     * 그외 -> 예외 처리
     *
     * postMessage() - ChatGPT에 응답 생성 요청하는 메서드 호출
     *
     * @param intention : 텍스트 의도
     * @param token : 레디스 키 토큰화
     */
    public void createResponseJson(Intention intention, TokenReq token);

    /**
     * 의도 분석 텍스트 삽입
     *
     * token으로 레디스에 저장된 prompt 불러와서 작업
     * postMessage() - ChatGPT에 응답 생성 요청하는 메서드 호출
     *
     * 의도 추가됐다면 레디스에 저장
     *
     * @param token : 레디스 키 토큰화
     */
    public void analyzeIntention(TokenReq token);

    /**
     * 수락, 거절 여부 / 알람 On, Off 여부 판별
     * 수락 -> saveEvent 혹은 saveTodo 호출
     *
     * @param accept : 수락, 거절 여부
     * @param alarm : 알람 on, off 여부
     * @param token : 레디스에 저장된 데이터 key값
     */
    public void validateAcceptAlarm(Boolean accept, Boolean alarm, TokenReq token);

    /**
     * 일정 DB에 저장
     *
     * @param response : ChatGPT의 응답
     * @param alarm : 알람 on, off 여부
     * @param token : 레디스에 저장된 데이터 key값
     * @return : 일정 저장 결과 반환
     */
    public ChatbotEventResp saveEvent(ChatGPTResp response, Boolean alarm, TokenReq token);

    /**
     * 투두 DB에 저장
     *
     * @param response : ChatGPT의 응답
     * @param token : 레디스에 저장된 데이터 key값
     * @return : 투두 저장 결과 반환
     */
    public ChatbotTodoResp saveTodo(ChatGPTResp response, TokenReq token);
}
