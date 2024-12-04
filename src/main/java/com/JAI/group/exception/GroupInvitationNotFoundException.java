package com.JAI.group.exception;

public class GroupInvitationNotFoundException extends RuntimeException {
    private Object data;

    public GroupInvitationNotFoundException(String message) {
        super(message);
    }

    public GroupInvitationNotFoundException(String message, Object data) {
        super(message);
        this.data = data;
    }

    public Object getData() {
        return data;
    }
}
