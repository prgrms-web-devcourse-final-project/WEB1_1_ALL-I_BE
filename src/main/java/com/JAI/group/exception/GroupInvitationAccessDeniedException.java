package com.JAI.group.exception;

public class GroupInvitationAccessDeniedException extends RuntimeException {
    private Object data;

    public GroupInvitationAccessDeniedException(String message) {
        super(message);
    }

    public GroupInvitationAccessDeniedException(String message, Object data) {
        super(message);
        this.data = data;
    }

    public Object getData() {
        return data;
    }
}
