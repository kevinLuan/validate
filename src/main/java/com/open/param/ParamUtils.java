package com.open.param;

public class ParamUtils {
	/**
	 * 将JSON字符串解析到Param
	 * 
	 * @param json
	 * @return
	 */
	public static Param fromJsonAsParam(String json) {
		return JsonToParamUtils.fromJsonAsParam(json);
	}

	/**
	 * 将Param转换到JAVA code
	 * 
	 * @param param
	 * @return
	 */
	public static String fromParamAsJavaCode(Param param) {
		return ParamToCodeUtils.fromParamAsJavaCode(param);
	}

	/**
	 * 将JSON转换的到java code
	 * 
	 * @param json
	 * @return
	 */
	public static String fromJsonAsJavaCode(String json) {
		Param param = fromJsonAsParam(json);
		return fromParamAsJavaCode(param);
	}

	/**
	 * 将Param转化到json数据协议
	 * 
	 * @param param
	 * @return
	 */
	public static String fromParamAsJsonData(Param param) {
		return ParamToJsonData.fromParamAsJsonData(param);
	}
}
