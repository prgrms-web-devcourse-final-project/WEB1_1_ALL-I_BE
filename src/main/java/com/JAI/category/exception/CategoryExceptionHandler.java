package com.JAI.category.exception;

import com.JAI.global.controller.ApiResponse;
import com.JAI.global.status.ErrorStatus;
import com.JAI.user.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = {"com.JAI.event"})
public class CategoryExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(CategoryNotFoundException.class)
    public ApiResponse<Object> handleUserNotFound(CategoryNotFoundException e) {
        return ApiResponse.onFailure(
                ErrorStatus.CATEGORY_NOT_FOUND,
                e.getMessage(),
                e.getData());
    }
}
