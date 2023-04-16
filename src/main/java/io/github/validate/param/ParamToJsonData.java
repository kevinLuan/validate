package io.github.validate.param;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.github.validate.datatype.NumberParser;

/**
 * Param到json数据协议转换(注意不是序列化Param协议格式)
 *
 * @author KEVIN LUAN
 */
public class ParamToJsonData {
    /***
     * 将参数定义转到到JSON示例数据
     * private static Param product = ParamObject.required("product", "商品对象", //
     *             Primitive.required("name", DataType.String, "商品名称").setExampleValue("IPhone7"),
     *             Primitive.required("price", DataType.Number, "商品价格").setExampleValue(99.98),
     *             ParamArray.required("skus", "商品SKU属性列表", //
     *                     ParamObject.required(//
     *                             Primitive.required("id", DataType.Number, "参数描述").setExampleValue(100), //
     *                             Primitive.required("name", DataType.String, "参数描述").setExampleValue("移动版"), //
     *                             ParamArray.required("code", "参数描述", //
     *                                     ParamObject.optional(//
     *                                             Primitive.optional("id", DataType.Number, "id").setExampleValue(12345), //
     *                                             Primitive.optional("title", DataType.String, "标题").setExampleValue("土黄金色")//
     *                                     )//
     *                             )//
     *                     )//
     *             )//
     *     );
     *
     * 返回示例：{"name":"IPhone7","price":99.98,"skus":[{"id":100,"name":"移动版","code":[{"id":12345,"title":"土黄金色"}]}]}
     * @param param
     * @return
     */
    public static String toJsonDataExample(Param param) {
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

    private static JsonElement primitive(Primitive primitive) {
        if (primitive.getDataType() == DataType.Number) {
            if (primitive.getExampleValue() != null) {
                NumberParser numberParser = NumberParser.parse(primitive.getExampleValue(), false);
                return new JsonPrimitive(numberParser.value);
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
