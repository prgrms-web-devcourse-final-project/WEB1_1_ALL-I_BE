package com.JAI.group.exception;

public class GroupNotFoundException extends RuntimeException {
    private Object data;

    public GroupNotFoundException(String message) {
        super(message);
    }

    public GroupNotFoundException(String message, Object data) {
        super(message);
        this.data = data;
    }

    public Object getData() {
        return data;
    }
}
