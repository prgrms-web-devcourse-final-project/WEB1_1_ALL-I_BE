package com.JAI.user.exception;

import com.JAI.global.controller.ApiResponse;
import com.JAI.global.status.ErrorStatus;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = {"com.JAI"})
public class UserExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UserNotFoundException.class)
    public ApiResponse<Object> handleUserNotFound(UserNotFoundException e) {
        return ApiResponse.onFailure(
                ErrorStatus.USER_NOT_FOUND,
                e.getMessage(),
                e.getData());
    }
}
