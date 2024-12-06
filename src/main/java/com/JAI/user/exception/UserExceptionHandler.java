package com.JAI.user.exception;

import com.JAI.global.controller.ApiResponse;
import com.JAI.global.status.ErrorStatus;
import com.JAI.group.exception.GroupBadRequestException;
import com.JAI.group.exception.GroupSettingDuplicatedException;
import com.JAI.group.exception.GroupSettingNotOwnerException;
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

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(UserDuplicatedException.class)
    public ApiResponse<Object> handleUserDuplicated(UserDuplicatedException e){
        return ApiResponse.onFailure(
                ErrorStatus.USER_DUPLICATED,
                e.getMessage(),
                e.getData());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UserBadRequestException.class)
    public ApiResponse<Object> handleUserBadRequestException(UserBadRequestException e){
        return ApiResponse.onFailure(
                ErrorStatus.USER_BAD_REQUEST,
                e.getMessage(),
                e.getData());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(UserNotOwnerException.class)
    public ApiResponse<Object> handleUserNotOwnerException(UserNotOwnerException e){
        return ApiResponse.onFailure(
                ErrorStatus.USER_NOT_OWNER_FOUND,
                e.getMessage(),
                e.getData());
    }
}
