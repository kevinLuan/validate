package com.open.validate.types;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.open.param.ParamArray;
import com.open.validate.Validate;

public class ArrayValidate implements Validate<ParamArray, ArrayNode> {

  public static ArrayValidate INSTANCE = new ArrayValidate();

  @Override
  public boolean test(ParamArray p, ArrayNode value) {
    System.out.println("ArrayValidate  path:" + p.getPath() + "|value:" + value.toString());
    if (value.size() == 0) {
      return false;
    } else {
      value.add("OK");
      return true;
    }
  }

  @Override
  public void assertValue(ParamArray p, ArrayNode value) {
    if (!test(p, value)) {
      throw new IllegalArgumentException("`" + p.getPath() + "`参数错误");
    }
  }

}
