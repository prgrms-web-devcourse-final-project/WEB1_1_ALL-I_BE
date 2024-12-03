package com.JAI.chatbot.exception;

import com.JAI.global.controller.ApiResponse;
import com.JAI.global.status.ErrorStatus;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = {"com.JAI.chatbot"})
public class ChatbotExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ChatbotBadRequestException.class)
    public ApiResponse<Object> handleChatbotBadRequestException(ChatbotBadRequestException e) {
        return ApiResponse.onFailure(
                ErrorStatus.CHATBOT_BAD_REQUEST,
                e.getMessage(),
                e.getData());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(ChatbotForbiddenException.class)
    public ApiResponse<Object> handleChatbotForbiddenException(ChatbotForbiddenException e) {
        return ApiResponse.onFailure(
                ErrorStatus.CHATBOT_FORBIDDEN,
                e.getMessage(),
                e.getData());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ChatbotNotFoundException.class)
    public ApiResponse<Object> handleChatbotNotFoundException(ChatbotNotFoundException e) {
        return ApiResponse.onFailure(
                ErrorStatus.CHATBOT_NOT_FOUND,
                e.getMessage(),
                e.getData());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ChatbotUnprocessableEntityException.class)
    public ApiResponse<Object> handleChatbotUnprocessableEntityException(ChatbotUnprocessableEntityException e) {
        return ApiResponse.onFailure(
                ErrorStatus.CHATBOT_UNPROCESSABLE_ENTITY,
                e.getMessage(),
                e.getData());
    }
}
