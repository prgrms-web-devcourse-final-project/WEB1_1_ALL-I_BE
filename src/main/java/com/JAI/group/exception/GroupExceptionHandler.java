package com.JAI.group.exception;

import com.JAI.global.controller.ApiResponse;
import com.JAI.global.status.ErrorStatus;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = {"com.JAI.category"})
public class GroupExceptionHandler {
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(GroupNotFoundException.class)
    public ApiResponse<Object> handleGroupNotFound(GroupNotFoundException e){
        return ApiResponse.onFailure(
                ErrorStatus.GROUP_NOT_FOUND,
                e.getMessage(),
                e.getData());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(GroupNotOwnerException.class)
    public ApiResponse<Object> handleGroupNotOwner(GroupNotOwnerException e){
        return ApiResponse.onFailure(
                ErrorStatus.GROUP_NOT_OWNER_FOUND,
                e.getMessage(),
                e.getData());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(GroupBadRequestException.class)
    public ApiResponse<Object> handleGroupBadRequest(GroupBadRequestException e){
        return ApiResponse.onFailure(
                ErrorStatus.GROUP_BAD_REQUEST,
                e.getMessage(),
                e.getData());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(GroupSettingNotOwnerException.class)
    public ApiResponse<Object> handleGroupSettingNotOwner(GroupSettingNotOwnerException e){
        return ApiResponse.onFailure(
                ErrorStatus.GROUP_SETTING_NOT_OWNER_FOUND,
                e.getMessage(),
                e.getData());
    }
}
