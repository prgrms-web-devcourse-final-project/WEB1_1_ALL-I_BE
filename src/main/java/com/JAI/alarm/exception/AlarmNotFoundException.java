package com.JAI.alarm.exception;

public class AlarmNotFoundException extends RuntimeException {
    private Object data;

    public AlarmNotFoundException(String message) {
        super(message);
    }

    public AlarmNotFoundException(String message, Object data) {
        super(message);
        this.data = data;
    }

    public Object getData() {
        return data;
    }
}
