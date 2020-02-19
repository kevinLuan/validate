package com.open.validate.impl;

import com.open.param.ParamNumber;
import com.open.param.validate.Validate;

public class NumberValidate implements Validate<ParamNumber, Number> {
  public static NumberValidate INSTANCE = new NumberValidate();

  private NumberValidate() {}

  @Override
  public boolean test(ParamNumber p, Number value) {
    if (value == null) {
      if (p.isRequired()) {
        throw new IllegalArgumentException(p.getName() + "参数不能为空");
      } else {
        return true;
      }
    }
    if (p.getMin() != null) {
      if (p.getMin().longValue() > value.longValue()) {
        return false;
      }
    }
    if (p.getMax() != null) {
      if (value.longValue() > p.getMax().longValue()) {
        return false;
      }
    }
    return true;
  }

  @Override
  public void assertValue(ParamNumber p, Number value) {
    if (!test(p, value)) {
      throw new IllegalArgumentException(p.getTipMsg());
    }
  }


  public static void throwError(ParamNumber param) {
    if (param.getParentNode() != null) {
      if (param.getParentNode().isArray()) {
        if (param.asPrimitive().existBetweenCheck()) {
          String msg = param.asPrimitive().getTipMsg(param.getPath() + "[]");
          throw new IllegalArgumentException(msg);
        } else {
          throw new IllegalArgumentException("`" + param.getPath() + "[]`只能包含数字");
        }
      }
    }
    if (param.asPrimitive().existBetweenCheck()) {
      String msg = param.asPrimitive().getTipMsg(param.getPath());
      throw new IllegalArgumentException(msg);
    } else {
      throw new IllegalArgumentException("`" + param.getPath() + "`必须是一个数字");
    }
  }
}

