package com.open.param.common;

import com.open.param.Param;
import com.open.param.ParamAny;
import com.open.param.ParamArray;
import com.open.param.ParamBase;
import com.open.param.ParamObject;
import com.open.param.ParamPrimitive;

class GenerateCodeV1 {

  private final static String NEW_LINE = "\n";
  public static GenerateCodeV1 INSTANCE = new GenerateCodeV1();

  /**
   * 生成Java code
   */
  public String getJavaCode(Param param) {
    StringBuilder builder = new StringBuilder();
    if (param.isArray()) {
      parserArray(param.asArray(), builder);
    } else if (param.isObject()) {
      parserObject(param.asObject(), builder);
    } else if (param.isPrimitive()) {
      parserPrimitive(param.asPrimitive(), builder);
    } else if (param.isAny()) {
      builder.append(param.asAny().toParamAnyCode());
    } else {
      throw new IllegalArgumentException("不支持的参数:`" + param + "`");
    }
    String result = builder.toString();
    if (result.endsWith(NEW_LINE)) {
      result = result.substring(0, result.length() - NEW_LINE.length());
    }
    return result.replace("\n\n", "\n") + ";";
  }

  private void parserArray(ParamArray array, StringBuilder builder) {
    String name = array.getName();
    String description = array.getDescription();
    ParamBase children = (ParamBase) array.getChildrenAsParam();
    String showName = formatParam(name), showDesc = formatParam(description);
    if (children == null) {
      if (array.isRequired()) {
        builder.append(
            "ParamArray.required(" + showName + "," + formatParam(description) + ")");
      } else {
        builder.append("ParamArray.of(" + showName + "," + formatParam(description) + ")");
      }
    } else {
      if (children.isObject()) {
        StringBuilder stringBuilder = new StringBuilder();
        parserObject(children.asObject(), stringBuilder);
        if (children.isRequired()) {
          builder.append("ParamArray.required(" + showName + "," + showDesc + ",");
        } else {
          builder.append("ParamArray.of(" + showName + "," + showDesc + ",");
        }
        builder.append(NEW_LINE);
        builder.append(stringBuilder + ")");
      } else if (children.isPrimitive()) {
        // 子节点
        String childrenCode = children.asPrimitive().toJavaCode();
        // 子节点的父级节点
        String arrayNode;
        {
          if (array.isRequired()) {
            arrayNode = "ParamArray.required(" + showName + "," + showDesc + ",";
          } else {
            arrayNode = "ParamArray.of(" + showName + "," + showDesc + ",";
          }
          arrayNode += NEW_LINE + childrenCode + ")";
        }
        builder.append(arrayNode);
      } else if (children.isAny()) {
        builder.append(children.asAny().toParamAnyCode());
      } else {
        throw NotSupportException.of("不支持的类型:" + children);
      }
    }
  }

  private void parserObject(ParamObject object, StringBuilder builder) {
    String name = object.getName();
    String description = object.getDescription();
    Param[] childrens = object.getChildren();
    StringBuilder nodeBuilder = new StringBuilder();
    for (int i = 0; i < childrens.length; i++) {
      Param param = childrens[i];
      if (param.isArray()) {
        newLine(nodeBuilder);
        StringBuilder stringBuilder = new StringBuilder();
        parserArray(param.asArray(), stringBuilder);
        nodeBuilder.append(stringBuilder.toString());
      } else if (param.isObject()) {
        StringBuilder childrenObjectBuilder = new StringBuilder();
        parserObject(param.asObject(), childrenObjectBuilder);
        nodeBuilder.append(childrenObjectBuilder.toString());
      } else if (param.isPrimitive()) {
        StringBuilder sb = new StringBuilder();
        parserPrimitive(param.asPrimitive(), sb);
        newLine(nodeBuilder);
        nodeBuilder.append(sb.toString());
      } else if (param.isAny()) {
        nodeBuilder.append(param.asAny().toParamAnyCode());
      } else {
        throw NotSupportException.of("不支持的类型`" + param.getDataType() + "`");
      }
      if (i < childrens.length - 1) {
        nodeBuilder.append(",");
        newLine(nodeBuilder);
      }
    }
    if (name != null && name.length() > 0) {
      String showName = formatParam(name);
      String showDesc = formatParam(description);
      if (object.isRequired()) {
        builder.append(
            "ParamObject.required(" + showName + "," + showDesc + "," + remoteLastComma(
                newLine(nodeBuilder)) + NEW_LINE + ")");
      } else {
        builder.append("ParamObject.of(" + showName + "," + showDesc + ","
            + remoteLastComma(newLine(nodeBuilder)) + NEW_LINE + ")");
      }
    } else {
      if (object.isRequired()) {
        builder.append(
            "ParamObject.required(" + remoteLastComma(newLine(nodeBuilder)) + NEW_LINE + ")");
      } else {
        builder.append("ParamObject.of(" + remoteLastComma(newLine(nodeBuilder)) + NEW_LINE + ")");
      }
    }
    newLine(builder);
  }

  private String remoteLastComma(String str) {
    int lastCommaIndex = str.lastIndexOf(",");
    String endWith = str.substring(lastCommaIndex);
    if (endWith.equals(",") || endWith.equals("," + NEW_LINE)) {
      str = str.substring(0, lastCommaIndex);
    }
    return str;
  }

  private String newLine(StringBuilder builder) {
    if (!builder.toString().endsWith(NEW_LINE)) {
      builder.append(NEW_LINE);
    }
    return builder.toString();
  }

  private void parserPrimitive(ParamPrimitive primitive, StringBuilder builder) {
    builder.append(primitive.toJavaCode());
  }

  public String buildExampleValue(ParamPrimitive primitive) {
    if (primitive.getExampleValue() != null) {
      if (primitive.getDataType().isNumber()) {
        return ".setExampleValue(" + primitive.getExampleValue() + ")";
      } else {
        return ".setExampleValue(\"" + primitive.getExampleValue() + "\")";
      }
    } else {
      return "";
    }
  }

  public String formatParam(String description) {
    if (description != null) {
      return "\"" + description + "\"";
    }
    return description;
  }
}
