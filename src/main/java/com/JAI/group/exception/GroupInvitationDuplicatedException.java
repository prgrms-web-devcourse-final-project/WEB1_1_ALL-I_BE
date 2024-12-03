package com.JAI.group.exception;

public class GroupInvitationDuplicatedException extends RuntimeException {
    private Object data;

    public GroupInvitationDuplicatedException(String message) {
        super(message);
    }

    public GroupInvitationDuplicatedException(String message, Object data) {
        super(message);
        this.data = data;
    }

    public Object getData() {
        return data;
    }
}
