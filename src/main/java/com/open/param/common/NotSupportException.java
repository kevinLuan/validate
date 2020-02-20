package com.open.param.common;

public class NotSupportException extends RuntimeException {
  private static final long serialVersionUID = -921327770065206522L;

  public NotSupportException(String msg) {
    super(msg);
  }

  public static NotSupportException of(String msg) {
    return new NotSupportException(msg);
  }
}
