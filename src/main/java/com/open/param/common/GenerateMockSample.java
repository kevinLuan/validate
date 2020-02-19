package com.open.param.common;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.open.param.DataType;
import com.open.param.Param;
import com.open.param.ParamArray;
import com.open.param.ParamObject;
import com.open.param.ParamPrimitive;

/**
 * 生成mock示例
 */
public class GenerateMockSample {

  /***
   * 获取param对象json数据格式
   *
   * @param param
   * @return
   */
  public static String getMockData(Param param) {
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
    return element.toString();
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
        Number number = (Number) primitive.asNumber().parseRawValue(primitive.getExampleValue());
        return new JsonPrimitive(number);
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
