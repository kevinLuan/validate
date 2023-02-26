package com.open.param;

import com.open.json.api.GsonSerialize;

public class ParamUtils {
    /**
     * 将JSON字符串解析到Param
     *
     * @param json
     * @return
     */
    public static Param fromJsonToParam(String json) {
        return JsonToParamUtils.parseJsonToParam(json);
    }

    /**
     * 将Param转换到JAVA code
     *
     * @param param
     * @return
     */
    public static String generateCode(Param param) {
        return ParamToCodeUtils.generateCode(param);
    }

    /**
     * 将JSON转换的到java code
     *
     * @param json
     * @return
     */
    public static String generateCode(String json) {
        Param param = fromJsonToParam(json);
        return generateCode(param);
    }

    /**
     * 将Param转化到json示例数据
     *
     * @param param
     * @return
     */
    public static String toJsonDataExample(Param param) {
        return ParamToJsonData.toJsonDataExample(param);
    }

    /**
     * 序列化参数定义
     *
     * @param product
     * @return
     */
    public static String serialization(Param product) {
        return GsonSerialize.INSTANCE.encode(product);
    }

    /**
     * 根据参数定义反序列化
     *
     * @param paramDefine
     * @return
     */
    public static Param deserialization(String paramDefine) {
        return GsonSerialize.INSTANCE.decode(paramDefine, ParamBase.class);
    }
}
