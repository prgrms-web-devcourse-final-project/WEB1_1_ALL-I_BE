package com.JAI.todo.exception;

public class GroupTodoBadRequestException extends RuntimeException{
    private Object data;

    public GroupTodoBadRequestException(String message) {
        super(message);
    }

    public GroupTodoBadRequestException(String message, Object data){
        super(message);
        this.data = data;
    }

    public Object getData() {
        return data;
    }
}
