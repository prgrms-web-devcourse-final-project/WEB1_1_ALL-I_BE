package com.JAI.chatbot.controller;

import com.JAI.chatbot.RedisChatbotUtil;
import com.JAI.chatbot.approval.dto.ApprovalResult;
import com.JAI.chatbot.approval.service.ApprovalOrchestrator;
import com.JAI.chatbot.exception.ChatbotExceptionHandler;
import com.JAI.chatbot.service.ChatbotService;
import com.JAI.user.domain.User;
import com.JAI.user.service.dto.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.*;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ApprovalHttpTest {
    ApprovalOrchestrator service = mock(ApprovalOrchestrator.class);
    MockMvc mvc;
    @BeforeEach void setup() {
        CustomUserDetails principal = mock(CustomUserDetails.class);
        User user = mock(User.class);
        when(principal.getUser()).thenReturn(user); when(user.getUserId()).thenReturn(UUID.randomUUID());
        mvc = MockMvcBuilders.standaloneSetup(new ChatbotController(mock(ChatbotService.class),service,mock(RedisChatbotUtil.class)))
                .setControllerAdvice(new ChatbotExceptionHandler())
                .setCustomArgumentResolvers(new HandlerMethodArgumentResolver() {
                    public boolean supportsParameter(MethodParameter p) {return p.getParameterType()==CustomUserDetails.class;}
                    public Object resolveArgument(MethodParameter p, ModelAndViewContainer c, NativeWebRequest r, WebDataBinderFactory f) {return principal;}
                }).build();
    }
    @Test void createdReplayProcessingAndNullAlarmPreserveEnvelope() throws Exception {
        when(service.process(any(),anyString(),eq(true),eq(false))).thenReturn(
                ApprovalResult.completed(List.of()),ApprovalResult.completed(List.of()).asReplay(),ApprovalResult.processing());
        request(201); request(200); request(202);
    }
    @Test void firstRejectionIsCreated() throws Exception {
        when(service.process(any(),anyString(),eq(true),eq(false))).thenReturn(ApprovalResult.rejected());
        request(201);
    }
    @ParameterizedTest @ValueSource(ints={400,403,409,410,503})
    void actualHttpStatusMatchesErrorEnvelope(int code) throws Exception {
        when(service.process(any(),anyString(),anyBoolean(),anyBoolean()))
                .thenThrow(new ResponseStatusException(HttpStatus.valueOf(code),"test"));
        request(code);
    }
    @Test void databaseFailureReturns503() throws Exception {
        when(service.process(any(),anyString(),anyBoolean(),anyBoolean()))
                .thenThrow(new org.springframework.dao.DataAccessResourceFailureException("unavailable"));
        request(503);
    }
    void request(int status) throws Exception {
        mvc.perform(post("/chatbot/message").param("accept","true").contentType("application/json")
                .content("{\"token\":\""+UUID.randomUUID()+"\"}"))
                .andExpect(status().is(status)).andExpect(jsonPath("$.code").value(status));
    }
}
