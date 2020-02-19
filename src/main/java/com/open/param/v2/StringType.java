package com.open.param.v2;

import com.open.param.DataType;
import com.open.param.ParamNumber;

/**
 * 原子参数（参数的最小单位）
 *
 * @author KEVIN LUAN
 */
public class StringType extends ParamNumber {

  public StringType() {
  }

  public static StringType create() {
    StringType primitive = new StringType();
    primitive.dataType = DataType.String;
    primitive.name="";
    return primitive;
  }

  public StringType name(String name) {
    super.setName(name);
    return this;
  }

  public StringType required() {
    super.required = true;
    return this;
  }

  public StringType description(String desc) {
    super.description = desc;
    return this;
  }

  public StringType exampleValue(Object exampleValue) {
    super.setExampleValue(exampleValue);
    return this;
  }

  public StringType min(Number min) {
    super.setMin(min);
    return this;
  }

  public StringType max(Number max) {
    super.setMax(max);
    return this;
  }
}
