package com.open.param.v2;

import com.open.param.DataType;
import com.open.param.ParamNumber;

/**
 * 原子参数（参数的最小单位）
 *
 * @author KEVIN LUAN
 */
public class NumberType extends ParamNumber {

  public NumberType() {
  }

  public static NumberType create() {
    NumberType primitive = new NumberType();
    primitive.dataType = DataType.String;
    primitive.name = "";
    return primitive;
  }

  public NumberType name(String name) {
    super.setName(name);
    return this;
  }

  public NumberType required() {
    super.required = true;
    return this;
  }

  public NumberType description(String desc) {
    super.description = desc;
    return this;
  }

  public NumberType exampleValue(Object exampleValue) {
    super.setExampleValue(exampleValue);
    return this;
  }

  public NumberType min(Number min) {
    super.setMin(min);
    return this;
  }

  public NumberType max(Number max) {
    super.setMax(max);
    return this;
  }
}
