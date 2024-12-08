package com.JAI.user.exception;

public class InvalidTokenTypeException extends RuntimeException {
    private Object data;

    public InvalidTokenTypeException(String message) {
        super(message);
    }

    public InvalidTokenTypeException(String message, Object data) {
        super(message);
        this.data = data;
    }

    public Object getData() {
        return data;
    }
}
