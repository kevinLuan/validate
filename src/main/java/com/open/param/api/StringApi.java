package com.open.param.api;

import com.open.param.DataType;
import com.open.param.ParamNumber;
import com.open.param.ParamString;

/**
 * 原子参数（参数的最小单位）
 *
 * @author KEVIN LUAN
 */
public final class StringApi extends ParamString {

  public StringApi() {
  }

  public static StringApi create() {
    StringApi primitive = new StringApi();
    primitive.dataType = DataType.String;
    primitive.name="";
    return primitive;
  }

  public StringApi name(String name) {
    super.setName(name);
    return this;
  }

  public StringApi required() {
    super.required = true;
    return this;
  }

  public StringApi description(String desc) {
    super.description = desc;
    return this;
  }

  public StringApi exampleValue(Object exampleValue) {
    super.setExampleValue(exampleValue);
    return this;
  }

  public StringApi min(Number min) {
    super.setMin(min);
    return this;
  }

  public StringApi max(Number max) {
    super.setMax(max);
    return this;
  }
}
