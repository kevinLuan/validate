package com.open.param;

import java.lang.reflect.Method;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.open.validate.Validate;

public enum DataType {
  String, Number, Array, Object;
  public boolean isPrimitive() {
    return this.isNumber() || this.isString();
  }

  public boolean isNumber() {
    return this == DataType.Number;
  }

  public boolean isString() {
    return this == DataType.String;
  }

  public boolean isObject() {
    return this == DataType.Object;
  }

  public boolean isArray() {
    return this == DataType.Array;
  }

  public static DataType parser(String dataType) {
    for (DataType type : values()) {
      if (type.name().equals(dataType)) {
        return type;
      }
    }
    throw new IllegalArgumentException("不支持的dataType:" + dataType);
  }

  /**
   * 验证Validate是否符当前验证类型
   * 
   * @param rules
   */
  @SuppressWarnings({"rawtypes"})
  public void assertValidate(Validate[] rules) {
    for (Validate validate : rules) {
      Method method = null;
      Class<?> targetType = null;
      if (this.isString()) {
        targetType = String.class;
      } else if (this.isNumber()) {
        targetType = Number.class;
      } else if (this.isArray()) {
        targetType = ArrayNode.class;
      } else if (this.isObject()) {
        targetType = ObjectNode.class;
      } else {
        throw new IllegalArgumentException("不支持的类型`" + this + "`");
      }
      Method[] methods = validate.getClass().getMethods();
      for (Method m : methods) {
        if (!m.isBridge() && m.getName().equals("assertValue")) {
          Class[] types = m.getParameterTypes();
          if (types.length == 2) {
            if (Param.class.isAssignableFrom(types[0]) &&
                targetType.isAssignableFrom(types[1])) {
              method = m;
              break;
            }
          }
        }
      }
      if (method == null) {
        throw new IllegalArgumentException(
            "缺少方法:`" + validate.getClass().getName() + "`.assertValue(Param,"
                + targetType.getSimpleName()
                + ")");
      }
    }
  }
}
