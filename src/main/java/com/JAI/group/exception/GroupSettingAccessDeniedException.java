package com.JAI.group.exception;

public class GroupSettingAccessDeniedException extends RuntimeException {
    private Object data;

    public GroupSettingAccessDeniedException(String message) {
        super(message);
    }

    public GroupSettingAccessDeniedException(String message, Object data) {
        super(message);
        this.data = data;
    }

    public Object getData() {
        return data;
    }
}
