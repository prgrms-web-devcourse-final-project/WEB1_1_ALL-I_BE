package com.JAI.chatbot.service;

import com.JAI.chatbot.controller.dto.ChatbotRedisDataDTO;
import com.JAI.chatbot.controller.dto.request.ChatbotReqDTO;
import com.JAI.chatbot.controller.dto.request.TokenReqDTO;
import com.JAI.chatbot.controller.dto.response.ChatbotEventRespDTO;
import com.JAI.chatbot.controller.dto.response.ChatbotResponseWrapper;
import com.JAI.chatbot.controller.dto.response.ChatbotTodoRespDTO;

public interface ChatbotService {

    /**
     * intention, 카테고리, 프롬프트 레디스에 저장
     *
     * @param request : ChatbotReq(intention, 카테고리, 프롬프트 입력)
     * @return : 사용자 입력 데이터 저장된 레디스 토큰값 반환
    */
    public TokenReqDTO saveRequest(ChatbotReqDTO request);


    /**
     * 의도에 따라 JSON 포맷 삽입 후 ChatGPT한테 응답 요청
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
     * @param token : 레디스 키 토큰
     * @return : ChatbotEventRespDTO 혹은 ChatbotTodoRespDTO 반환
     */
    public ChatbotResponseWrapper createResponseJson(TokenReqDTO token);


    /**
     * 의도 분석 텍스트 삽입
     *
     * token으로 레디스에 저장된 prompt 불러와서 작업
     * findIntention() - ChatGPT에 텍스트 의도 분석 요청하는 메서드 호출
     *
     * 의도 추가됐다면 레디스에 저장
     *
     * @param token : 레디스 키 토큰
     */
    public void analyzeIntention(TokenReqDTO token);


    /**
     * 수락, 거절 여부 / 알람 On, Off 여부 판별
     * 수락 -> saveEvent 혹은 saveTodo 호출
     * 거절 -> 레디스에서 데이터 삭제
     *
     * @param accept : 수락, 거절 여부
     * @param alarm : 알람 on, off 여부
     * @param token : 레디스에 저장된 데이터 key값
     */
    public void validateAcceptAlarm(Boolean accept, Boolean alarm, TokenReqDTO token);

    /**
     * 일정 DB에 저장
     *
     * @param chatbotRedisDataDTO : ChatGPT 응답 레디스에 저장한 거
     * @param alarm : 알람 on, off 여부
     * @param token : 레디스에 저장된 데이터 key값
     *
     * @return : 일정 저장 결과 반환
     */
    public ChatbotEventRespDTO saveEvent(ChatbotRedisDataDTO chatbotRedisDataDTO, Boolean alarm, TokenReqDTO token);

    /**
     * 투두 DB에 저장
     *
     * @param chatbotRedisDataDTO : ChatGPT 응답 레디스에 저장한 거
     * @param token : 레디스에 저장된 데이터 key값
     *
     * @return : 투두 저장 결과 반환
     */
    public ChatbotTodoRespDTO saveTodo(ChatbotRedisDataDTO chatbotRedisDataDTO, TokenReqDTO token);
}
