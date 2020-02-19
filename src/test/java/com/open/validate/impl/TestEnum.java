package com.open.validate.impl;

import com.open.param.ParamNumber;
import com.open.param.validate.Validate;

public enum TestEnum implements Validate<ParamNumber, Number> {
  A(1), B(2), C(3);
  private int code;

  private TestEnum(int code) {
    this.code = code;
  }

  @Override
  public boolean test(ParamNumber p, Number value) {
    if (this.code == value.intValue()) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  public void assertValue(ParamNumber p, Number value) {
    if (!test(p, value)) {
      throw new IllegalArgumentException(p.getTipMsg());
    }
  }
}
