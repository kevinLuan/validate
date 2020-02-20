package com.open.param.common;

import com.open.json.api.GsonSerialize;
import com.open.param.Param;
import com.open.param.ParamAny;
import com.open.param.ParamArray;
import com.open.param.ParamBase;
import com.open.param.ParamObject;

/**
 * Param参数序列化及反序列化
 *
 * @author kevin LUAN
 */
public class ParamSerializable {

  public static ParamSerializable INSTANCE = new ParamSerializable();

  public String encode(Param param) {
    return GsonSerialize.INSTANCE.encode(param);
  }

  public Param decode(String json) {
    Param param = GsonSerialize.INSTANCE.decode(json, ParamBase.class);
    return adjust(param);
  }

  /**
   * 调整对象类型
   */
  public Param adjust(Param param) {
    if (param.isNumber()) {
      param = param.asNumber();
    } else if (param.isString()) {
      param = param.asString();
    } else if (param.isArray()) {
      param = param.asArray();
      array(param.asArray());
    } else if (param.isObject()) {
      param = param.asObject();
      obj(param.asObject());
    }
    return param;
  }

  private void obj(ParamObject obj) {
    Param childrens[] = obj.getChildren();
    refresh(obj, childrens);
  }

  private void refresh(Param param, Param[] childrens) {
    for (int i = 0; i < childrens.length; i++) {
      Param children = childrens[i];
      if (children.isObject()) {
        childrens[i] = children.asObject();
        obj(childrens[i].asObject());
      } else if (children.isArray()) {
        childrens[i] = children.asArray();
        array(childrens[i].asArray());
      } else if (children.isString()) {
        childrens[i] = children.asString();
      } else if (children.isNumber()) {
        childrens[i] = children.asNumber();
      } else if (children.isBoolean()) {
        childrens[i] = children.asBoolean();
      } else if (children.isAny()) {
        childrens[i] = children.asAny();
      } else {
        throw NotSupportException
            .of("不支持的类型:`" + children.getDataType() + "`，path:`" + children.getPath() + "`");
      }
    }
  }

  private void array(ParamArray array) {
    Param childrens[] = array.getChildren();
    refresh(array, childrens);
  }
}
