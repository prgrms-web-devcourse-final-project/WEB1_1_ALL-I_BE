package com.JAI.chatbot.service;

import com.JAI.chatbot.controller.dto.ChatGPTMessageDTO;
import com.JAI.chatbot.controller.dto.request.TokenReqDTO;
import com.JAI.chatbot.controller.dto.response.ChatGPTRespDTO;

import java.util.List;


public interface ChatGPTService {

    /**
     * ChatGPT에 텍스트 생성 요청
     *
     * @param message : ChatGPT에 넣을 요청
     * @param token : 레디스에 저장된 데이터 key값
     *
     * @return : ChatGPT가 반환하는 응답
     */
    public ChatGPTRespDTO postMessage(List<ChatGPTMessageDTO> message, TokenReqDTO token);

    /**
     * ChatGPT에 텍스트 의도 분석 요청
     *
     * 텍스트 의도 분석 결과 레디스에 저장
     *
     * @param message : ChatGPT에 넣을 요청
     * @param token : 레디스에 저장된 데이터 key값
     *
     * @return : ChatGPT가 반환하는 응답 -> Intention
     */
    public String findIntention(List<ChatGPTMessageDTO> message, TokenReqDTO token);

}
