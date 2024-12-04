package com.JAI.group.exception;

public class GroupSettingNotFoundException extends RuntimeException {
  private Object data;

  public GroupSettingNotFoundException(String message) {
    super(message);
  }

  public GroupSettingNotFoundException(String message, Object data) {
    super(message);
    this.data = data;
  }

  public Object getData() {
    return data;
  }
}
