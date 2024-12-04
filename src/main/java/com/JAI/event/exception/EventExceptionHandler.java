package com.JAI.event.exception;

import com.JAI.global.controller.ApiResponse;
import com.JAI.global.status.ErrorStatus;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
}
