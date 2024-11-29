package com.JAI.event.exception;

public class PersonalEventNotOwnerException extends RuntimeException {
    private Object data;

    public PersonalEventNotOwnerException(String message) {
        super(message);
    }

    public PersonalEventNotOwnerException(String message, Object data) {
        super(message);
        this.data = data;
    }

    public Object getData() {
        return data;
    }
}
