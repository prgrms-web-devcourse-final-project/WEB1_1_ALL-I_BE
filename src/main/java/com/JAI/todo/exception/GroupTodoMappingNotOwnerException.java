package com.JAI.todo.exception;

public class GroupTodoMappingNotOwnerException extends RuntimeException {
    private Object data;

    public GroupTodoMappingNotOwnerException(String message) {
        super(message);
    }

    public GroupTodoMappingNotOwnerException(String message, Object data){
        super(message);
        this.data = data;
    }

    public Object getData() {
        return data;
    }
}
