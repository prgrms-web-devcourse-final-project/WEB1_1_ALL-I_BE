package com.JAI.group.exception;

public class GroupNotOwnerException extends RuntimeException {
    private Object data;

    public GroupNotOwnerException(String message) {
        super(message);
    }

    public GroupNotOwnerException(String message, Object data) {
        super(message);
        this.data = data;
    }

    public Object getData() {
        return data;
    }
}
