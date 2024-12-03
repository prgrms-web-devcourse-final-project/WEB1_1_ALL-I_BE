package com.JAI.group.exception;

public class GroupBadRequestException extends RuntimeException {
    private Object data;

    public GroupBadRequestException(String message) {
        super(message);
    }

    public GroupBadRequestException(String message, Object data) {
        super(message);
        this.data = data;
    }

    public Object getData() {
        return data;
    }
}
