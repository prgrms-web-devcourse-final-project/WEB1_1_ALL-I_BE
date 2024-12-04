package com.JAI.group.exception;

public class GroupSettingDuplicatedException extends RuntimeException {
  private Object data;

  public GroupSettingDuplicatedException(String message) {
    super(message);
  }

  public GroupSettingDuplicatedException(String message, Object data) {
    super(message);
    this.data = data;
  }

  public Object getData() {
    return data;
  }
}
