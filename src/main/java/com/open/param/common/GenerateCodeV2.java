package com.open.param.common;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.node.TextNode;
import org.apache.commons.lang3.StringUtils;
import com.open.param.Param;
import com.open.param.ParamArray;
import com.open.param.ParamObject;
import com.open.param.ParamPrimitive;

class GenerateCodeV2 {

  private final static String NEW_LINE = "\n";
  public static GenerateCodeV2 INSTANCE = new GenerateCodeV2();

  /**
   * 生成Java code
   */
  public String getJavaCode(Param param) {
    String result = null;
    if (param.isArray()) {
      result = parserArray(param.asArray());
    } else if (param.isObject()) {
      result = parserObject(param.asObject());
    } else if (param.isPrimitive()) {
      result = parserPrimitive(param.asPrimitive());
    } else if (param.isAny()) {
      result = param.asAny().toParamApiCode();
    } else {
      throw new IllegalArgumentException("不支持的参数:`" + param + "`");
    }
    if (result != null && result.endsWith("\n")) {
      result = result.substring(0, result.length() - 1);
    }
    return result + ";";
  }

  private String parserArray(ParamArray array) {
    StringBuilder builder = new StringBuilder();
    if (array.isRequired()) {
      builder.append("ParamApi.array(true)");
    } else {
      builder.append("ParamApi.array()");
    }
    builder.append(appendNameAndDesc(array.getName(), array.getDescription()));
    builder.append(childrenParam(array, array.getChildren()));
    return builder.toString();
  }

  protected static String appendNameAndDesc(String name, String description) {
    StringBuilder builder = new StringBuilder();
    if (StringUtils.isNotBlank(name)) {
      builder.append(".name(\"" + name + "\")");
    }
    if (StringUtils.isNotBlank(description)) {
      builder.append(".description(\"" + description + "\")");
    }
    if (builder.length() > 0) {
      return builder.toString() + "\n";
    }
    return "";
  }

  private String parserObject(ParamObject object) {
    StringBuilder builder = new StringBuilder();
    if (object.isRequired()) {
      builder.append("ParamApi.object(true)");
    } else {
      builder.append("ParamApi.object()");
    }
    builder.append(appendNameAndDesc(object.getName(), object.getDescription()));
    builder.append(childrenParam(object, object.getChildren()));
    newLine(builder);
    return builder.toString();
  }

  private String childrenParam(Param parent, Param[] childrens) {
    StringBuilder builder = new StringBuilder();
    if (childrens.length > 0) {
      builder.append(".children(").append("\n");
      for (int i = 0; i < childrens.length; i++) {
        Param param = childrens[i];
        if (param.isArray()) {
          newLine(builder);
          builder.append(parserArray(param.asArray()));
        } else if (param.isObject()) {
          builder.append(parserObject(param.asObject()));
        } else if (param.isPrimitive()) {
          builder.append(parserPrimitive(param.asPrimitive()));
        } else if (param.isAny()) {
          builder.append(param.asAny().toParamApiCode());
        } else {
          throw NotSupportException.of("不支持的类型`" + param.getDataType() + "`");
        }
        if (i < childrens.length - 1) {
          builder.append(",");
          newLine(builder);
        }
      }
      builder.append(")\n");
    }
    return builder.toString();
  }

  private String newLine(StringBuilder builder) {
    if (!builder.toString().endsWith(NEW_LINE)) {
      builder.append(NEW_LINE);
    }
    return builder.toString();
  }

  private String parserPrimitive(ParamPrimitive primitive) {
    StringBuilder sb = new StringBuilder();
    if (primitive.isNumber()) {
      if (primitive.isRequired()) {
        sb.append("ParamApi.number(true)");
      } else {
        sb.append("ParamApi.number()");
      }
    } else if (primitive.isBoolean()) {
      if (primitive.isRequired()) {
        sb.append("ParamApi.bool(true)");
      } else {
        sb.append("ParamApi.bool()");
      }
    } else if (primitive.isString()) {
      if (primitive.isRequired()) {
        sb.append("ParamApi.string(true)");
      } else {
        sb.append("ParamApi.string()");
      }
    } else {
      throw NotSupportException.of("不支持的类型`" + primitive.getDataType() + "`");
    }
    sb.append(appendNameAndDesc(primitive.getName(), primitive.getDescription()));
    sb.append(buildExampleValue(primitive));
    return sb.toString() + "\n";
  }

  private String buildExampleValue(ParamPrimitive primitive) {
    if (primitive.getExampleValue() != null) {
      if (primitive.getDataType().isNumber()) {
        Number number = primitive.asNumber()
            .parseAndCheck(TextNode.valueOf(primitive.getExampleValue()));
        if (number.longValue() > Integer.MAX_VALUE) {
          return ".exampleValue(" + primitive.getExampleValue() + "L)";
        } else {
          return ".exampleValue(" + primitive.getExampleValue() + ")";
        }
      } else if (primitive.isBoolean()) {
        return ".exampleValue(" + primitive.getExampleValue() + ")";
      } else if (primitive.isAny()) {
        return "";
      } else if (primitive.isString()) {
        return ".exampleValue(\"" + primitive.getExampleValue() + "\")";
      } else {
        throw NotSupportException.of("不支持的类型`" + primitive.getDataType() + "`");
      }
    } else {
      return "";
    }
  }

}
