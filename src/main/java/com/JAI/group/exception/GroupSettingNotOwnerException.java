package com.JAI.group.exception;

public class GroupSettingNotOwnerException extends RuntimeException {
    private Object data;

    public GroupSettingNotOwnerException(String message) {
        super(message);
    }

    public GroupSettingNotOwnerException(String message, Object data) {
        super(message);
        this.data = data;
    }

    public Object getData() {
        return data;
    }
}
