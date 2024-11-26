package com.JAI.user.exception;

public class UserNotFoundException extends RuntimeException {
    private Object data;

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(String message, Object data) {
        super(message);
        this.data = data;
    }

    public Object getData() {
        return data;
    }
}
