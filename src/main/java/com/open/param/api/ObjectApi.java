package com.open.param.api;

import com.open.param.DataType;
import com.open.param.Param;
import com.open.param.ParamBase;
import com.open.param.ParamObject;

/**
 * 对象ObjectNode参数
 *
 * @author KEVIN LUAN
 */
public final class ObjectApi extends ParamObject {
  ObjectApi() {
    super();
  }

  public static ObjectApi create() {
    ObjectApi obj = new ObjectApi();
    obj.dataType = DataType.Object;
    obj.name = "";
    return obj;
  }

  public ObjectApi name(String name) {
    super.setName(name);
    return this;
  }

  public ObjectApi required() {
    super.required = true;
    return this;
  }

  public ObjectApi description(String desc) {
    super.description = desc;
    return this;
  }

  public ObjectApi children(Param... childrens) {
    this.children = new ParamBase[childrens.length];
    for (int i = 0; i < childrens.length; i++) {
      Param param = childrens[i];
      this.children[i] = (ParamBase) param;
      // if (param.isObjectValue()) {
      // throw new IllegalArgumentException("ParamObject子节点Name不能为空");
      // }
    }
    return this;
  }
}
