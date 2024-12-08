package com.JAI.user.exception;

public class RefreshTokenMismatchException extends RuntimeException {
  private Object data;

  public RefreshTokenMismatchException(String message) {
    super(message);
  }

  public RefreshTokenMismatchException(String message, Object data) {
    super(message);
    this.data = data;
  }

  public Object getData() {
    return data;
  }
}
