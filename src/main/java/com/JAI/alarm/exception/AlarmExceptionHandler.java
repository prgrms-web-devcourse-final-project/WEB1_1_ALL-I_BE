package com.JAI.alarm.exception;

import com.JAI.global.controller.ApiResponse;
import com.JAI.global.status.ErrorStatus;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = {"com.JAI.alarm"})
public class AlarmExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(AlarmNotFoundException.class)
    public ApiResponse<Object> handleAlarmNotFound(AlarmNotFoundException e) {
        return ApiResponse.onFailure(
                ErrorStatus.ALARM_NOT_FOUND,
                e.getMessage(),
                e.getData());
    }
}
