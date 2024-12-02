package com.JAI.todo.exception;

public class PersonalTodoNotFoundException extends RuntimeException {

    private Object data;

    public PersonalTodoNotFoundException(String message) {
        super(message);
    }

    public PersonalTodoNotFoundException(String message, Object data){
        super(message);
        this.data = data;
    }

    public Object getData() {
        return data;
    }
}
