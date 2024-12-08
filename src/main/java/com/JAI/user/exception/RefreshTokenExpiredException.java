package com.JAI.user.exception;

public class RefreshTokenExpiredException extends RuntimeException {
  private Object data;

  public RefreshTokenExpiredException(String message) {
    super(message);
  }

  public RefreshTokenExpiredException(String message, Object data) {
    super(message);
    this.data = data;
  }

  public Object getData() {
    return data;
  }
}
