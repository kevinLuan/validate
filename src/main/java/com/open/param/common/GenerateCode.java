package com.open.param.common;

import com.open.param.DataType;
import com.open.param.Param;
import com.open.param.ParamPrimitive;

public class GenerateCode {

  public static String getJavaCodeV1(String jsonData) {
    Param param = JsonConverter.INSTANCE.convert(jsonData);
    return GenerateCodeV1.INSTANCE.getJavaCode(param);
  }

  /**
   * 生成Java code
   */
  public static String getJavaCodeV1(Param param) {
    return GenerateCodeV1.INSTANCE.getJavaCode(param);
  }

  public static String getJavaCodeV2(String jsonData) {
    Param param = JsonConverter.INSTANCE.convert(jsonData);
    return GenerateCodeV2.INSTANCE.getJavaCode(param);
  }

  /**
   * 生成Java code
   */
  public static String getJavaCodeV2(Param param) {
    return GenerateCodeV2.INSTANCE.getJavaCode(param);
  }

  public static String formatParam(String str) {
    return GenerateCodeV1.INSTANCE.formatParam(str);
  }

  public static String getType(DataType type) {
    return GenerateCodeV1.INSTANCE.getType(type);
  }

  public static Object buildExampleValue(ParamPrimitive primitive) {
    return GenerateCodeV1.INSTANCE.buildExampleValue(primitive);
  }
}
