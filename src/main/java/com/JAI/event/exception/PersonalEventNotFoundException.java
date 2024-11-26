package com.JAI.event.exception;

public class PersonalEventNotFoundException extends RuntimeException {
    private Object data;

    public PersonalEventNotFoundException(String message) {
        super(message);
    }

    public PersonalEventNotFoundException(String message, Object data) {
        super(message);
        this.data = data;
    }

    public Object getData() {
        return data;
    }
}
