package com.JAI.group.exception;

import com.JAI.global.controller.ApiResponse;
import com.JAI.global.status.ErrorStatus;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = {"com.JAI.category", "com.JAI.event", "com.JAI.group"})
public class GroupExceptionHandler {
    //Group
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

    //GroupSetting
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(GroupSettingNotFoundException.class)
    public ApiResponse<Object> handleGroupSettingNotFound(GroupSettingNotFoundException e){
        return ApiResponse.onFailure(
                ErrorStatus.GROUP_SETTING_NOT_FOUND,
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
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(GroupSettingDuplicatedException.class)
    public ApiResponse<Object> handleGroupSettingDuplicated(GroupSettingDuplicatedException e){
        return ApiResponse.onFailure(
                ErrorStatus.GROUP_INVITATION_DUPLICATED,
                e.getMessage(),
                e.getData());
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(GroupSettingAccessDeniedException.class)
    public ApiResponse<Object> handleGroupSettingAccessDenied(GroupSettingAccessDeniedException e){
        return ApiResponse.onFailure(
                ErrorStatus.GROUP_SETTING_ACCESS_DENIED,
                e.getMessage(),
                e.getData());
    }

    //GroupInvitation
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(GroupInvitationAccessDeniedException.class)
    public ApiResponse<Object> handleGroupInvitationAccessDenied(GroupInvitationAccessDeniedException e){
        return ApiResponse.onFailure(
                ErrorStatus.GROUP_INVITATION_ACCESS_DENIED,
                e.getMessage(),
                e.getData());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(GroupInvitationDuplicatedException.class)
    public ApiResponse<Object> handleGroupInvitationDuplicated(GroupInvitationDuplicatedException e){
        return ApiResponse.onFailure(
                ErrorStatus.GROUP_INVITATION_DUPLICATED,
                e.getMessage(),
                e.getData());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(GroupInvitationNotFoundException.class)
    public ApiResponse<Object> handleGroupInvitationNotFound(GroupInvitationNotFoundException e){
        return ApiResponse.onFailure(
                ErrorStatus.GROUP_INVITATION_NOT_FOUND,
                e.getMessage(),
                e.getData());
    }
}
