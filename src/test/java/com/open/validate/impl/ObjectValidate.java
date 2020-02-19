package com.open.validate.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.open.json.api.JsonUtils;
import com.open.param.Param;
import com.open.param.ParamObject;
import com.open.param.validate.Validate;

public class ObjectValidate implements Validate<ParamObject, ObjectNode> {

  public static ObjectValidate INSTANCE = new ObjectValidate();

  @Override
  public boolean test(ParamObject p, ObjectNode value) {
    System.out.println("path:" + p.getPath() + "|value:" + value.toString());
    if (p != null) {
      for (Param children : p.getChildren()) {
        if (children.isRequired()) {
          if (JsonUtils.isNull(value.get(children.getName()))) {
            return false;
          }
        }
      }
    }
    value.put("check", "成功");
    return true;
  }

  @Override
  public void assertValue(ParamObject p, ObjectNode value) {
    if (!test(p, value)) {
      throw new IllegalArgumentException("`" + p.getPath() + "`参数错误");
    }
  }

}
