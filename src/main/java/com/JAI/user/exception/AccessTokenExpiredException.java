package com.JAI.user.exception;

public class AccessTokenExpiredException extends RuntimeException {
    private Object data;

    public AccessTokenExpiredException(String message) {
        super(message);
    }

    public AccessTokenExpiredException(String message, Object data) {
        super(message);
        this.data = data;
    }

    public Object getData() {
        return data;
    }
}
