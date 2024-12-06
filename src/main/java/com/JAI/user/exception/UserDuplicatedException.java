package com.JAI.user.exception;

public class UserDuplicatedException extends RuntimeException {
    private Object data;

    public UserDuplicatedException(String message) {
        super(message);
    }

    public UserDuplicatedException(String message, Object data) {
        super(message);
        this.data = data;
    }

    public Object getData() {
        return data;
    }
}
