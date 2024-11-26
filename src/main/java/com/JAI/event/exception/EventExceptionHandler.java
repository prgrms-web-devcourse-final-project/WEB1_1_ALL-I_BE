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
    public ApiResponse<Object> handleUserNotFound(PersonalEventNotFoundException e) {
        return ApiResponse.onFailure(
                ErrorStatus.CATEGORY_NOT_FOUND,
                e.getMessage(),
                e.getData());
    }
}
