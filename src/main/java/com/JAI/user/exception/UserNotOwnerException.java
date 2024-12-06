package com.JAI.user.exception;

public class UserNotOwnerException extends RuntimeException {
    private Object data;

    public UserNotOwnerException(String message) {
        super(message);
    }

    public UserNotOwnerException(String message, Object data) {
        super(message);
        this.data = data;
    }

    public Object getData() {
        return data;
    }
}
