package com.JAI.category.exception;

public class CategoryNotOwnerException extends RuntimeException {
    private Object data;

    public CategoryNotOwnerException(String message) {
        super(message);
    }

    public CategoryNotOwnerException(String message, Object data) {
        super(message);
        this.data = data;
    }

    public Object getData() {
        return data;
    }
}
