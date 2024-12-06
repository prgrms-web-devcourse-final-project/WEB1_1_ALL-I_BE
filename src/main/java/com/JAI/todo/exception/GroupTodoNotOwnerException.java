package com.JAI.todo.exception;

public class GroupTodoNotOwnerException extends RuntimeException {
  private Object data;

  public GroupTodoNotOwnerException(String message) {
    super(message);
  }

  public GroupTodoNotOwnerException(String message, Object data){
    super(message);
    this.data = data;
  }

  public Object getData() {
    return data;
  }
}
