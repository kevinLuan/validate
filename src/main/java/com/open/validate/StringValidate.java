package com.open.validate;

import com.open.param.ParamString;

public class StringValidate implements Validate<ParamString, String> {
  public static StringValidate INSTANCE = new StringValidate();

  private StringValidate() {}

  @Override
  public boolean test(ParamString p, String value) {
    System.out.println("StringValidate `" + p.getPath() + "` -> " + value);
    return value == null || "张三".equals(value) || "李四".equals(value);
  }

  @Override
  public void assertValue(ParamString p, String value) {
    if (!test(p, value)) {
      throw new IllegalArgumentException(p.getTipMsg());
    }
  }


}
