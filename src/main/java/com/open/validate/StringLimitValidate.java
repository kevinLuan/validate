package com.open.validate;

import com.open.param.ParamString;

public class StringLimitValidate implements Validate<ParamString, String> {

  @Override
  public boolean test(ParamString p, String value) {
    if (value == null) {
      if (p.isRequired()) {
        throw new IllegalArgumentException(p.getName() + "参数不能为空");
      } else {
        return true;
      }
    }
    if (p.getMin() != null) {
      if (value.length() < p.getMin().intValue()) {
        return false;
      }
    }
    if (p.getMax() != null) {
      return value.length() > p.getMax().intValue();
    }
    return true;
  }

  @Override
  public void assertValue(ParamString p, String value) {
    if (!test(p, value)) {
      throw new IllegalArgumentException(p.getTipMsg());
    }
  }


}
