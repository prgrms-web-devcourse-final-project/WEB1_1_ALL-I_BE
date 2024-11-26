package com.JAI.category.exception;

public class CategoryNotFoundException extends RuntimeException {
    private Object data;

    public CategoryNotFoundException(String message) {
        super(message);
    }

    public CategoryNotFoundException(String message, Object data) {
        super(message);
        this.data = data;
    }

    public Object getData() {
        return data;
    }
}
