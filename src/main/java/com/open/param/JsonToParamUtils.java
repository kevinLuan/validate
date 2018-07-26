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
	 * 从JSON协议解析出参数定义对象
	 * 
	 * @param json
	 * @return
	 */
	public static Param fromJsonAsParam(String json) {
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
			return ParamObject.noRequired(values);
		} else {
			return ParamObject.noRequired(name, DESCRIPTION, values);
		}
	}

	private static ParamArray parserArray(String name, JsonArray array) {
		if (array.size() > 0) {
			JsonElement element = array.get(0);
			if (element.isJsonObject()) {
				return ParamArray.noRequired(name, DESCRIPTION, parserObject("", element.getAsJsonObject()));
			} else if (element.isJsonPrimitive()) {
				return ParamArray.noRequired(name, DESCRIPTION, parserPrimitive("", element.getAsJsonPrimitive()));
			} else {
				throw new IllegalArgumentException("不支持的类型:" + element);
			}
		}
		return ParamArray.noRequired(name, DESCRIPTION);
	}

	private static ParamPrimitive parserPrimitive(String name, JsonPrimitive element) {
		if (element.isNumber()) {
			if (name.length() == 0) {
				return ParamPrimitive.noRequired(DataType.Number).setExampleValue(element.getAsNumber());
			} else {
				return ParamPrimitive.noRequired(name, DataType.Number, DESCRIPTION).setExampleValue(element.getAsNumber());
			}
		} else {
			if (name.length() == 0) {
				return ParamPrimitive.noRequired(DataType.String).setExampleValue(element.getAsString());
			} else {
				return ParamPrimitive.noRequired(name, DataType.String, DESCRIPTION).setExampleValue(element.getAsString());
			}
		}
	}
}
