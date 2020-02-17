package com.open.param.convert;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.open.datatype.Numberx;
import com.open.param.DataType;
import com.open.param.Param;
import com.open.param.ParamArray;
import com.open.param.ParamObject;
import com.open.param.ParamPrimitive;

/**
 * Param到json数据协议转换(注意不是序列化Param协议格式)
 * 
 * @author KEVIN LUAN
 */
public class DataParser {
  /***
   * 将Param转化到json数据协议
   * 
   * @param param
   * @return
   */
  public static JsonElement fromParamAsJsonData(Param param) {
    JsonElement element = null;
    if (param.isArray()) {
      element = array(param.asArray());
    } else if (param.isObject()) {
      element = object(param.asObject());
    } else if (param.isPrimitive()) {
      element = primitive(param.asPrimitive());
    } else {
      throw new IllegalArgumentException("不支持的类型:" + param);
    }
    return element;
  }

  private static JsonObject object(ParamObject object) {
    JsonObject jsonObject = new JsonObject();
    Param[] params = object.getChildren();
    for (int i = 0; i < params.length; i++) {
      Param param = params[i];
      if (param.isArray()) {
        JsonArray value = array(param.asArray());
        jsonObject.add(param.getName(), value);
      } else if (param.isObject()) {
        JsonObject value = object(param.asObject());
        jsonObject.add(param.getName(), value);
      } else if (param.isPrimitive()) {
        jsonObject.add(param.getName(), primitive(param.asPrimitive()));
      }
    }
    return jsonObject;
  }

  private static JsonArray array(ParamArray array) {
    JsonArray jsonArray = new JsonArray();
    Param[] params = array.getChildren();
    for (int i = 0; i < params.length; i++) {
      Param param = params[i];
      if (param.isArray()) {
        JsonArray value = array(param.asArray());
        jsonArray.add(value);
      } else if (param.isObject()) {
        JsonObject value = object(param.asObject());
        jsonArray.add(value);
      } else if (param.isPrimitive()) {
        JsonElement value = primitive(param.asPrimitive());
        jsonArray.add(value);
      }
    }
    return jsonArray;
  }

  private static JsonElement primitive(ParamPrimitive primitive) {
    if (primitive.getDataType() == DataType.Number) {
      if (primitive.getExampleValue() != null) {
        Numberx numberx = Numberx.parser(primitive.getExampleValue(), false);
        return new JsonPrimitive(numberx.value);
      }
      return JsonNull.INSTANCE;
    } else {
      if (primitive.getExampleValue() != null) {
        return new JsonPrimitive(primitive.getExampleValue());
      } else {
        return JsonNull.INSTANCE;
      }
    }
  }
}
