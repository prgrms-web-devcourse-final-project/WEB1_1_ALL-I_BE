package com.JAI.todo.exception;

public class GroupTodoNotFoundException extends RuntimeException {
    private Object data;

    public GroupTodoNotFoundException(String message) {
        super(message);
    }

    public GroupTodoNotFoundException(String message, Object data){
        super(message);
        this.data = data;
    }

    public Object getData() {
        return data;
    }
}
