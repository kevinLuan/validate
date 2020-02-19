package com.open.param.v2;

import com.open.param.DataType;
import com.open.param.Param;
import com.open.param.ParamBase;
import com.open.param.ParamObject;

/**
 * 对象ObjectNode参数
 *
 * @author KEVIN LUAN
 */
public class ObjectType extends ParamObject {
  ObjectType() {
    super();
  }

  public static ObjectType create() {
    ObjectType obj = new ObjectType();
    obj.dataType = DataType.Object;
    obj.name="";
    return obj;
  }

  public ObjectType name(String name) {
    super.setName(name);
    return this;
  }

  public ObjectType required() {
    super.required = true;
    return this;
  }

  public ObjectType description(String desc) {
    super.description = desc;
    return this;
  }

  public ObjectType children(Param... childrens) {
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
