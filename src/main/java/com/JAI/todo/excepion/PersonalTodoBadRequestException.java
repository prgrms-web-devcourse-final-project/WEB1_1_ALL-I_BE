package com.JAI.todo.excepion;

public class PersonalTodoBadRequestException extends RuntimeException {
    private Object data;

    public PersonalTodoBadRequestException(String message) {
        super(message);
    }

    public PersonalTodoBadRequestException(String message, Object data){
        super(message);
        this.data = data;
    }

    public Object getData() {
        return data;
    }
}
