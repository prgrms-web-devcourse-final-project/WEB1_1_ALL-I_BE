package com.JAI.user.exception;

import com.JAI.global.controller.ApiResponse;
import com.JAI.global.status.ErrorStatus;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = {"com.JAI"})
public class JWTExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(RefreshTokenNotFoundException.class)
    public ApiResponse<Object> handleRefreshTokenNotFound(RefreshTokenNotFoundException e) {
        return ApiResponse.onFailure(
                ErrorStatus.REFRESH_TOKEN_NOT_FOUND,
                e.getMessage(),
                e.getData());
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(RefreshTokenExpiredException.class)
    public ApiResponse<Object> handleRefreshTokenExpired(RefreshTokenExpiredException e) {
        return ApiResponse.onFailure(
                ErrorStatus.REFRESH_TOKEN_UNAUTHORIZED,
                e.getMessage(),
                e.getData());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidTokenTypeException.class)
    public ApiResponse<Object> handleInvalidTokenType(InvalidTokenTypeException e) {
        return ApiResponse.onFailure(
                ErrorStatus.REFRESH_TOKEN_BAD_REQUEST,
                e.getMessage(),
                e.getData());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(RefreshTokenMismatchException.class)
    public ApiResponse<Object> handleRefreshTokenMismatch(RefreshTokenMismatchException e) {
        return ApiResponse.onFailure(
                ErrorStatus.REFRESH_TOKEN_FORBIDDEN,
                e.getMessage(),
                e.getData());
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AccessTokenExpiredException.class)
    public ApiResponse<Object> handleAccessTokenExpired(AccessTokenExpiredException e) {
        return ApiResponse.onFailure(
                ErrorStatus.ACCESS_TOKEN_UNAUTHORIZED,
                e.getMessage(),
                e.getData());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(LoginRequestParseException.class)
    public ApiResponse<Object> handleLoginRequestParse(LoginRequestParseException e) {
        return ApiResponse.onFailure(
                ErrorStatus.LOGIN_REQUEST_BAD_REQUEST,
                e.getMessage(),
                e.getData());
    }
}
