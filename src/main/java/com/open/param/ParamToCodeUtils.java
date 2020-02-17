package com.open.param;

/**
 * 将Param转换到JavaCode
 *
 * @author KEVIN LUAN
 */
public class ParamToCodeUtils {

  private final static String NEW_LINE = "\n";

  public static String fromParamAsJavaCode(Param param) {
    StringBuilder builder = new StringBuilder();
    if (param.isArray()) {
      parserArray(param.asArray(), builder);
    } else if (param.isObject()) {
      parserObject(param.asObject(), builder);
    } else if (param.isPrimitive()) {
      parserPrimitive(param.asPrimitive(), builder);
    } else {
      throw new IllegalArgumentException("不支持的参数:`" + param + "`");
    }
    String result = builder.toString();
    if (result.endsWith(NEW_LINE)) {
      result = result.substring(0, result.length() - NEW_LINE.length());
    }
    return result + ";";
  }

  private static void parserArray(ParamArray array, StringBuilder builder) {
    String name = array.getName();
    String description = array.getDescription();
    ParamBase children = (ParamBase) array.getChildrenAsParam();
    if (children.isObject()) {
      StringBuilder stringBuilder = new StringBuilder();
      parserObject(children.asObject(), stringBuilder);
      if (array.isRequired()) {
        builder.append(
            "ParamArray.required(" + formatParam(name) + "," + formatParam(description) + ",");
      } else {
        builder.append("ParamArray.of(" + formatParam(name) + "," + formatParam(description) + ",");
      }
      builder.append(NEW_LINE);
      builder.append(stringBuilder + ")");
    } else if (children.isPrimitive()) {
      // 子节点
      String childrenCode=children.asPrimitive().toJavaCode();
      // 子节点的父级节点
      String arrayNode;
      {
        if (array.isRequired()) {
          arrayNode =
              "ParamArray.required(" + formatParam(name) + "," + formatParam(description) + ",";
        } else {
          arrayNode = "ParamArray.of(" + formatParam(name) + "," + formatParam(description) + ",";
        }
        arrayNode += NEW_LINE + childrenCode + ")";
      }
      builder.append(arrayNode);
    } else {
      throw new IllegalArgumentException("不支持的类型:" + children);
    }
  }

  private static void parserObject(ParamObject object, StringBuilder builder) {
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
        nodeBuilder.append(",");
        newLine(nodeBuilder);
      } else if (param.isObject()) {
        StringBuilder childrenObjectBuilder = new StringBuilder();
        parserObject(param.asObject(), childrenObjectBuilder);
        nodeBuilder.append(childrenObjectBuilder.toString());
        if(i<childrens.length-1){
          nodeBuilder.append(",");
          newLine(nodeBuilder);
        }
      } else if (param.isPrimitive()) {
        StringBuilder stringBuilder = new StringBuilder();
        parserPrimitive(param.asPrimitive(), stringBuilder);
        newLine(nodeBuilder);
        nodeBuilder.append(stringBuilder.toString());
        nodeBuilder.append(",");
        newLine(nodeBuilder);
      }
    }
    if (name != null && name.length() > 0)

    {
      if (object.isRequired()) {
        builder.append(
            "ParamObject.required(" + formatParam(name) + "," + formatParam(description) + ","
                + remoteLastComma(newLine(nodeBuilder)) + NEW_LINE + ")");
      } else {
        builder.append("ParamObject.of(" + formatParam(name) + "," + formatParam(description) + ","
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

  private static String remoteLastComma(String str) {
    int lastCommaIndex = str.lastIndexOf(",");
    String endWith = str.substring(lastCommaIndex);
    if (endWith.equals(",") || endWith.equals("," + NEW_LINE)) {
      str = str.substring(0, lastCommaIndex);
    }
    return str;
  }

  private static String newLine(StringBuilder builder) {
    if (!builder.toString().endsWith(NEW_LINE)) {
      builder.append(NEW_LINE);
    }
    return builder.toString();
  }

  private static void parserPrimitive(ParamPrimitive primitive, StringBuilder builder) {
    builder.append(primitive.toJavaCode());
  }

  public static String buildExampleValue(ParamPrimitive primitive) {
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

  public static String formatParam(String description) {
    if (description != null) {
      return "\"" + description + "\"";
    }
    return description;
  }

  public static String getType(DataType type) {
    return "DataType." + type;
  }
}
