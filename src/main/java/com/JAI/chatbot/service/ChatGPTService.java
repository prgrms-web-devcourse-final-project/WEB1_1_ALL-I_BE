package com.JAI.chatbot.service;

import com.JAI.chatbot.controller.ChatGPTMessage;
import com.JAI.chatbot.controller.request.ChatGPTReq;
import com.JAI.chatbot.controller.request.TokenReq;
import com.JAI.chatbot.controller.response.ChatGPTResp;
import com.JAI.chatbot.domain.Intention;

import java.util.List;


public interface ChatGPTService {

    /**
     * ChatGPT에 텍스트 생성 요청
     *
     * @param message : ChatGPT에 넣을 요청
     * @param token : 레디스에 저장된 데이터 key값
     * @return : ChatGPT가 반환하는 응답
     */
    public ChatGPTResp postMessage(List<ChatGPTMessage> message, TokenReq token);

    /**
     * ChatGPT에 텍스트 의도 분석 요청
     *
     * 텍스트 의도 분석 결과 레디스에 저장
     *
     * @param message : ChatGPT에 넣을 요청
     * @param token : 레디스에 저장된 데이터 key값
     * @return : ChatGPT가 반환하는 응답 -> Intention enum으로 변경
     */
    public Intention findIntention(List<ChatGPTMessage> message, TokenReq token);

}
