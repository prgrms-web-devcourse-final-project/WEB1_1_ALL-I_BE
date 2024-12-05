package com.JAI.event.exception;

public class GroupEventNotFoundException extends RuntimeException {
    private Object data;

    public GroupEventNotFoundException(String message) {
        super(message);
    }

    public GroupEventNotFoundException(String message, Object data) {
        super(message);
        this.data = data;
    }

    public Object getData() {
        return data;
    }
}
