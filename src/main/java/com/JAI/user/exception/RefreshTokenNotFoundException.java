package com.JAI.user.exception;

public class RefreshTokenNotFoundException extends RuntimeException {
  private Object data;

  public RefreshTokenNotFoundException(String message) {
    super(message);
  }

  public RefreshTokenNotFoundException(String message, Object data) {
    super(message);
    this.data = data;
  }

  public Object getData() {
    return data;
  }
}
