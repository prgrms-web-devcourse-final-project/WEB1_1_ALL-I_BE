package com.JAI.chatbot.exception;

import com.JAI.global.controller.ApiResponse;
import com.JAI.global.status.ErrorStatus;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = {"com.JAI.chatbot"})
public class ChatbotExceptionHandler {
    @ExceptionHandler(org.springframework.web.server.ResponseStatusException.class)
    public org.springframework.http.ResponseEntity<ApiResponse<Object>> handleApprovalStatus(
            org.springframework.web.server.ResponseStatusException e) {
        HttpStatus status = HttpStatus.valueOf(e.getStatusCode().value());
        return org.springframework.http.ResponseEntity.status(status)
                .body(new ApiResponse<>(status, e.getReason(), null));
    }

    @ExceptionHandler({org.springframework.dao.DataAccessException.class,
            org.springframework.transaction.TransactionException.class})
    public org.springframework.http.ResponseEntity<ApiResponse<Object>> handleStorageFailure(RuntimeException e) {
        return org.springframework.http.ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ApiResponse<>(HttpStatus.SERVICE_UNAVAILABLE,
                        "저장소 오류입니다. 같은 토큰으로 다시 요청해 주세요", null));
    }


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
