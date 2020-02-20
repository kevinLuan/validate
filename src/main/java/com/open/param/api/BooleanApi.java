package com.open.param.api;

import com.open.param.DataType;
import com.open.param.ParamBoolean;

/**
 * 原子参数（参数的最小单位）
 *
 * @author KEVIN LUAN
 */
public final class BooleanApi extends ParamBoolean {

  public BooleanApi() {}

  public static BooleanApi create() {
    BooleanApi primitive = new BooleanApi();
    primitive.dataType = DataType.Boolean;
    primitive.name = "";
    return primitive;
  }

  public static BooleanApi create(boolean require) {
    BooleanApi primitive = new BooleanApi();
    primitive.dataType = DataType.Boolean;
    primitive.name = "";
    primitive.required = require;
    return primitive;
  }

  public BooleanApi name(String name) {
    super.setName(name);
    return this;
  }

  public BooleanApi description(String desc) {
    super.description = desc;
    return this;
  }

  public BooleanApi exampleValue(Object exampleValue) {
    super.setExampleValue(exampleValue);
    return this;
  }
}
