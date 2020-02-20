package com.open.param.api;

import com.open.param.ParamAny;

public class ParamApi {
  public static ObjectApi object(boolean require) {
    if (require) {
      return ObjectApi.create().required();
    } else {
      return ObjectApi.create();
    }
  }

  public static ObjectApi object() {
    return ObjectApi.create();
  }

  public static ArrayApi array() {
    return ArrayApi.create();
  }

  public static ArrayApi array(boolean require) {
    if (require) {
      return ArrayApi.create().required();
    } else {
      return ArrayApi.create();
    }
  }

  public static StringApi string() {
    return StringApi.create();
  }

  public static StringApi string(boolean require) {
    if (require) {
      return StringApi.create().required();
    } else {
      return StringApi.create();
    }
  }

  public static NumberApi number() {
    return NumberApi.create();
  }

  public static NumberApi number(boolean require) {
    if (require) {
      return NumberApi.create().required();
    } else {
      return NumberApi.create();
    }
  }

  public static BooleanApi bool() {
    return BooleanApi.create();
  }

  public static BooleanApi bool(boolean require) {
    return BooleanApi.create(require);
  }

  public static ParamAny any() {
    return ParamAny.create();
  }

  public static ParamAny any(boolean require) {
    return ParamAny.create(require);
  }
}
