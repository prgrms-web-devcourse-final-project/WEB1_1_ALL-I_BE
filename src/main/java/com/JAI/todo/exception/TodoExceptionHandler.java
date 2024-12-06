package com.JAI.todo.exception;

import com.JAI.global.controller.ApiResponse;
import com.JAI.global.status.ErrorStatus;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice()
public class TodoExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(PersonalTodoNotFoundException.class)
    public ApiResponse<Object> handlePersonalTodoNotFound(PersonalTodoNotFoundException e){
        return ApiResponse.onFailure(
                ErrorStatus.PERSONAL_TODO_NOT_FOUND,
                e.getMessage(),
                e.getData());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(PersonalTodoNotOwnerException.class)
    public ApiResponse<Object> handlePersonalTodoNotOwner(PersonalTodoNotOwnerException e){
        return ApiResponse.onFailure(
                ErrorStatus.PERSONAL_TODO_NOT_OWNER_FOUND,
                e.getMessage(),
                e.getData());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(PersonalTodoBadRequestException.class)
    public ApiResponse<Object> handlePersonalTodoBadRequest(PersonalTodoBadRequestException e){
        return ApiResponse.onFailure(
                ErrorStatus.PERSONAL_TODO_BAD_REQUEST,
                e.getMessage(),
                e.getData());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(GroupTodoNotFoundException.class)
    public ApiResponse<Object> handleGroupTodoNotFound(GroupTodoNotFoundException e){
        return ApiResponse.onFailure(
                ErrorStatus.PERSONAL_TODO_NOT_FOUND,
                e.getMessage(),
                e.getData());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(GroupTodoNotOwnerException.class)
    public ApiResponse<Object> handleGroupTodoNotOwner(GroupTodoNotOwnerException e){
        return ApiResponse.onFailure(
                ErrorStatus.PERSONAL_TODO_NOT_OWNER_FOUND,
                e.getMessage(),
                e.getData());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(GroupTodoBadRequestException.class)
    public ApiResponse<Object> handleGroupTodoBadRequest(GroupTodoBadRequestException e){
        return ApiResponse.onFailure(
                ErrorStatus.PERSONAL_TODO_BAD_REQUEST,
                e.getMessage(),
                e.getData());
    }


    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(GroupTodoMappingNotOwnerException.class)
    public ApiResponse<Object> handleGroupTodoMappingNotOwner(GroupTodoMappingNotOwnerException e){
        return ApiResponse.onFailure(
                ErrorStatus.PERSONAL_TODO_NOT_OWNER_FOUND,
                e.getMessage(),
                e.getData());
    }
}
