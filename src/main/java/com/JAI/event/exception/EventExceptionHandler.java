package com.JAI.event.exception;

import com.JAI.global.controller.ApiResponse;
import com.JAI.global.status.ErrorStatus;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.stream.Collectors;

@RestControllerAdvice(basePackages = {"com.JAI.event"})
public class EventExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(PersonalEventNotFoundException.class)
    public ApiResponse<Object> handlePersonalEventNotFound(PersonalEventNotFoundException e) {
        return ApiResponse.onFailure(
                ErrorStatus.PERSONAL_EVENT_NOT_FOUND,
                e.getMessage(),
                e.getData());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(PersonalEventNotOwnerException.class)
    public ApiResponse<Object> handlePersonalEventNotOwner(PersonalEventNotOwnerException e) {
        return ApiResponse.onFailure(
                ErrorStatus.PERSONAL_EVENT_NOT_OWNER,
                e.getMessage(),
                e.getData());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(PersonalEventBadRequestException.class)
    public ApiResponse<Object> handlePersonalEventBadRequest(PersonalEventBadRequestException e) {
        return ApiResponse.onFailure(
                ErrorStatus.PERSONAL_EVENT_BAD_REQUEST,
                e.getMessage(),
                e.getData());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(GroupEventNotFoundException.class)
    public ApiResponse<Object> handleGroupEventNotFound(GroupEventNotFoundException e) {
        return ApiResponse.onFailure(
                ErrorStatus.GROUP_EVENT_NOT_FOUND,
                e.getMessage(),
                e.getData());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ApiResponse<Object> handleHandlerMethodValidationException(HandlerMethodValidationException e) {
        String message = e.getAllErrors().stream()
                .map(MessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));

        return ApiResponse.onFailure(
                ErrorStatus.BAD_REQUEST,
                message,
                null
        );
    }
}
