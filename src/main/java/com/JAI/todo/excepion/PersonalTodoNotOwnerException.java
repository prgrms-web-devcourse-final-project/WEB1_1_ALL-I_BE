package com.JAI.todo.excepion;

public class PersonalTodoNotOwnerException extends RuntimeException {
    private Object data;

    public PersonalTodoNotOwnerException(String message) {
        super(message);
    }

    public PersonalTodoNotOwnerException(String message, Object data){
        super(message);
        this.data = data;
    }

    public Object getData() {
        return data;
    }
}
