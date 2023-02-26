package com.open.param;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

/**
 * 将JSON协议转到到Param
 *
 * @author KEVIN LUAN
 */
public class JsonToParamUtils {
    public static String DESCRIPTION = null;

    /**
     * 根据json数据生成Param验证对象
     *
     * @param json {"name":"IPhone7","price":99.98,"skus":[{"id":100,"name":"移动版","code":[{"id":12345,"title":"土黄金色"}]}]}
     * @return
     */
    public static Param parseJsonToParam(String json) {
        JsonElement element = new JsonParser().parse(json);
        Param param = null;
        if (element.isJsonArray()) {
            param = parserArray("", element.getAsJsonArray());
        } else if (element.isJsonObject()) {
            param = parserObject("", element.getAsJsonObject());
        } else if (element.isJsonPrimitive()) {
            param = parserPrimitive("", element.getAsJsonPrimitive());
        } else {
            System.out.println("不支持的类型->" + element);
        }
        return param;
    }

    private static ParamBase parserObject(String name, JsonObject jsonObject) {

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
            } else {
                System.out.println("不支持的类型 key:" + key + "->" + val);
            }
            index++;
        }
        if (name.length() == 0) {
            return ParamObject.optional(values);
        } else {
            return ParamObject.optional(name, DESCRIPTION, values);
        }
    }

    private static ParamArray parserArray(String name, JsonArray array) {
        if (array.size() > 0) {
            JsonElement element = array.get(0);
            if (element.isJsonObject()) {
                return ParamArray.optional(name, DESCRIPTION, parserObject("", element.getAsJsonObject()));
            } else if (element.isJsonPrimitive()) {
                return ParamArray.optional(name, DESCRIPTION, parserPrimitive("", element.getAsJsonPrimitive()));
            } else {
                throw new IllegalArgumentException("不支持的类型:" + element);
            }
        }
        return ParamArray.optional(name, DESCRIPTION);
    }

    private static Primitive parserPrimitive(String name, JsonPrimitive element) {
        if (element.isNumber()) {
            if (name.length() == 0) {
                return Primitive.optional(DataType.Number).setExampleValue(element.getAsNumber());
            } else {
                return Primitive.optional(name, DataType.Number, DESCRIPTION).setExampleValue(element.getAsNumber());
            }
        } else {
            if (name.length() == 0) {
                return Primitive.optional(DataType.String).setExampleValue(element.getAsString());
            } else {
                return Primitive.optional(name, DataType.String, DESCRIPTION).setExampleValue(element.getAsString());
            }
        }
    }
}
