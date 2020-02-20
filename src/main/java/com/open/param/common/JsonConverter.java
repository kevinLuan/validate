package com.open.param.common;

import com.open.json.api.JsonUtils;
import com.sun.tools.corba.se.idl.constExpr.Not;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.open.param.Param;
import com.open.param.ParamAny;
import com.open.param.ParamArray;
import com.open.param.ParamBase;
import com.open.param.ParamBoolean;
import com.open.param.ParamNumber;
import com.open.param.ParamObject;
import com.open.param.ParamPrimitive;
import com.open.param.ParamString;

/**
 * JSON转换器
 */
public class JsonConverter {

  public static JsonConverter INSTANCE = new JsonConverter();

  /**
   * 从JSON协议解析出参数定义对象
   */
  public Param convert(String jsonData) {
    JsonElement element = new JsonParser().parse(jsonData);
    Param param = null;
    if (element.isJsonArray()) {
      param = parserArray("", element.getAsJsonArray());
    } else if (element.isJsonObject()) {
      param = parserObject("", element.getAsJsonObject());
    } else if (element.isJsonPrimitive()) {
      param = parserPrimitive("", element.getAsJsonPrimitive());
    } else if (element.isJsonNull()) {
      param = ParamAny.create();
    } else {
      throw NotSupportException.of("不支持的类型->" + element);
    }
    return ParamSerializable.INSTANCE.adjust(param);
  }

  private ParamBase parserObject(String name, JsonObject jsonObject) {
    Param values[] = new Param[jsonObject.size()];
    Iterator<Map.Entry<String, JsonElement>> iterator = jsonObject.entrySet().iterator();
    int index = 0;
    while (iterator.hasNext()) {
      Entry<String, JsonElement> entry = iterator.next();
      String key = entry.getKey();
      JsonElement val = entry.getValue();
      if (val.isJsonObject()) {
        values[index] = parserObject(key, val.getAsJsonObject());
      } else if (val.isJsonArray()) {
        values[index] = parserArray(key, val.getAsJsonArray());
      } else if (val.isJsonPrimitive()) {
        values[index] = parserPrimitive(key, val.getAsJsonPrimitive());
      } else if (val.isJsonNull()) {
        values[index] = ParamAny.create().name(key);
      } else {
        throw NotSupportException.of("不支持的类型 key:" + key + "->" + val);
      }
      index++;
    }
    if (name.length() == 0) {
      return ParamObject.of(values);
    } else {
      return ParamObject.of(name, null, values);
    }
  }

  private ParamArray parserArray(String name, JsonArray array) {
    if (array.size() > 0) {
      JsonElement element = array.get(0);
      if (element.isJsonObject()) {
        return ParamArray.of(name, null, parserObject("", element.getAsJsonObject()));
      } else if (element.isJsonPrimitive()) {
        return ParamArray.of(name, null, parserPrimitive("", element.getAsJsonPrimitive()));
      } else if (element.isJsonNull()) {
        return ParamArray.of(name, null, ParamAny.create());
      } else {
        throw NotSupportException.of("不支持的类型:" + element);
      }
    }
    return ParamArray.of(name, null);
  }

  private ParamPrimitive parserPrimitive(String name, JsonPrimitive element) {
    if (element.isNumber()) {
      return ParamNumber.of(name, null).setExampleValue(element.getAsNumber());
    } else if (element.isBoolean()) {
      return ParamBoolean.of(name, null).setExampleValue(element.getAsBoolean());
    } else {
      return ParamString.of(name, null).setExampleValue(element.getAsString());
    }
  }
}
