package com.JAI.global.controller;

import com.JAI.global.status.ErrorStatus;
import com.JAI.global.status.SuccessStatus;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@ToString
public class ApiResponse<T> {
    private int code;
    private HttpStatus status;
    private String message;
    private T data;

    public ApiResponse(HttpStatus status, String message, T data) {
        this.code = status.value();
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public ApiResponse(HttpStatus status, String message) {
        this.code = status.value();
        this.status = status;
        this.message = message;
    }

    // 일반적인 성공
    public static <T> ApiResponse<T> onSuccess(T result){
        return new ApiResponse<>(SuccessStatus.OK.getHttpStatus() , SuccessStatus.OK.getMessage(), result);
    }

    // 생성 성공
    public static <T> ApiResponse<T> onCreateSuccess(T result){
        return new ApiResponse<>(SuccessStatus.CREATE_SUCCESS.getHttpStatus(), SuccessStatus.CREATE_SUCCESS.getMessage(), result);
    }

    public static <T> ApiResponse<T> onCreateSuccess(String message, T result){
        return new ApiResponse<>(SuccessStatus.CREATE_SUCCESS.getHttpStatus(), SuccessStatus.CREATE_SUCCESS.getMessage(), result);
    }

    // 생성 성공
    public static <T> ApiResponse<T> onCreateSuccess(){
        return new ApiResponse<>(SuccessStatus.CREATE_SUCCESS.getHttpStatus(), SuccessStatus.CREATE_SUCCESS.getMessage());
    }

    // 삭제 성공
    public static <T> ApiResponse<T> onDeleteSuccess(T result){
        return new ApiResponse<>(SuccessStatus.DELETE_SUCCESS.getHttpStatus(), SuccessStatus.DELETE_SUCCESS.getMessage(), result);
    }

    // 실패한 경우 응답 생성
    public static <T> ApiResponse<T> onFailure(ErrorStatus status, String message, T result){
        return new ApiResponse<>(status.getHttpStatus(), message, result);
    }
}
