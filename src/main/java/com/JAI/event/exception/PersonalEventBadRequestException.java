package com.JAI.event.exception;

public class PersonalEventBadRequestException extends RuntimeException {
    private Object data;

    public PersonalEventBadRequestException(String message) {
        super(message);
    }

    public PersonalEventBadRequestException(String message, Object data) {
        super(message);
        this.data = data;
    }

    public Object getData() {
        return data;
    }
}
