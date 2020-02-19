package com.open.param.v2;

public class ParamApi {
  public static ObjectType object(boolean require) {
    if (require) {
      return ObjectType.create().required();
    } else {
      return ObjectType.create();
    }
  }

  public static ObjectType object() {
    return ObjectType.create();
  }

  public static ArrayType array() {
    return ArrayType.create();
  }

  public static ArrayType array(boolean require) {
    if (require) {
      return ArrayType.create().required();
    } else {
      return ArrayType.create();
    }
  }

  public static StringType string() {
    return StringType.create();
  }

  public static StringType string(boolean require) {
    if (require) {
      return StringType.create().required();
    } else {
      return StringType.create();
    }
  }

  public static NumberType number() {
    return NumberType.create();
  }

  public static NumberType number(boolean require) {
    if (require) {
      return NumberType.create().required();
    } else {
      return NumberType.create();
    }
  }


}
