package com.open.param.api;

import com.open.param.DataType;
import com.open.param.ParamNumber;

/**
 * 原子参数（参数的最小单位）
 *
 * @author KEVIN LUAN
 */
public final class NumberApi extends ParamNumber {

  public NumberApi() {}

  public static NumberApi create() {
    NumberApi primitive = new NumberApi();
    primitive.dataType = DataType.String;
    primitive.name = "";
    return primitive;
  }

  public NumberApi name(String name) {
    super.setName(name);
    return this;
  }

  public NumberApi required() {
    super.required = true;
    return this;
  }

  public NumberApi description(String desc) {
    super.description = desc;
    return this;
  }

  public NumberApi exampleValue(Object exampleValue) {
    super.setExampleValue(exampleValue);
    return this;
  }

  public NumberApi min(Number min) {
    super.setMin(min);
    return this;
  }

  public NumberApi max(Number max) {
    super.setMax(max);
    return this;
  }
}
