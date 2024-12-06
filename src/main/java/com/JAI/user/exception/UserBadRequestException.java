package com.JAI.user.exception;

public class UserBadRequestException extends RuntimeException {
    private Object data;

    public UserBadRequestException(String message) {
        super(message);
    }

    public UserBadRequestException(String message, Object data) {
        super(message);
        this.data = data;
    }

    public Object getData() {
        return data;
    }
}
