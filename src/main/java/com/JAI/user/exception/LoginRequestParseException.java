package com.JAI.user.exception;

public class LoginRequestParseException extends RuntimeException {
    private Object data;

    public LoginRequestParseException(String message) {
        super(message);
    }

    public LoginRequestParseException(String message, Object data) {
        super(message);
        this.data = data;
    }

    public Object getData() {
        return data;
    }
}
