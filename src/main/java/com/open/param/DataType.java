package com.open.param;

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
}
