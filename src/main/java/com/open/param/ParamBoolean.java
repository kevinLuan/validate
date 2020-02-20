package com.open.param;

import org.apache.commons.lang3.StringUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.open.param.common.GenerateCode;
import com.open.param.common.NotSupportException;

/**
 * Boolean类型参数
 */
public class ParamBoolean extends ParamPrimitive {
  protected ParamBoolean() {}

  public ParamBoolean(String name, boolean required, String description) {
    super(name, required, DataType.Boolean, description);
  }

  static ParamBoolean make(String name, boolean required, String description) {
    return new ParamBoolean(name, required, description);
  }

  /**
   * 创建一个必须参数
   */
  public static ParamBoolean required(String name, String description) {
    return ParamBoolean.make(name, true, description);
  }

  /**
   * 创建一个必须参数
   * <p>
   * 当前基本类型只能用在父节点是Array的情况例如：array[true,false]
   */
  public static ParamBoolean required(String description) {
    return ParamBoolean.make("", true, description);
  }

  /**
   * 创建一个非必须参数
   */
  public static ParamBoolean of(String name, String description) {
    return ParamBoolean.make(name, false, description);
  }

  /**
   * 创建一个非必须参数
   * <p>
   * 当前基本类型只能用在父节点是Array的情况例如：array[0,1,2]
   */
  public static ParamBoolean of(String description) {
    return ParamBoolean.make("", false, description);
  }

  @Override
  public String getTipMsg() {
    return this.getTipMsg(getPath());
  }

  @Override
  public String getTipMsg(String path) {
    return "`" + path + "`取值范围:[true,false]";
  }

  @Override
  public final String toJavaCode() {
    StringBuilder builder = new StringBuilder();
    String nameStr = GenerateCode.formatParam(name);
    String descStr = GenerateCode.formatParam(description);
    if (required) {
      if (StringUtils.isBlank(name)) {
        builder.append("ParamBoolean.required(" + descStr + ")");
      } else {
        builder.append("ParamBoolean.required(" + nameStr + "," + descStr + ")");
      }
    } else {
      if (StringUtils.isBlank(name) && StringUtils.isBlank(description)) {
        builder.append("ParamBoolean.of(" + descStr + ")");
      } else {
        builder.append("ParamBoolean.of(" + nameStr + "," + descStr + ")");
      }
    }
    builder.append(GenerateCode.buildExampleValue(this));
    return builder.toString();
  }

  @Override
  public ParamPrimitive between(Number min, Number max) {
    throw NotSupportException.of("Boolean类型不支持该操作");
  }

  @Override
  public ParamPrimitive setMax(Number max) {
    throw NotSupportException.of("Boolean类型不支持该操作");
  }

  @Override
  public ParamPrimitive setMin(Number min) {
    throw NotSupportException.of("Boolean类型不支持该操作");
  }

  private Boolean parseBoolean(JsonNode node) {
    if (node.isValueNode()) {
      if (node.isBoolean()) {
        return node.asBoolean();
      } else if (node.isTextual()) {
        return Boolean.valueOf(node.asText());
      }
    } else if (node.isMissingNode()) {
      return null;
    }
    throw new IllegalArgumentException("`" + this.getPath() + "`参数错误");
  }

  @Override
  public Boolean parseAndCheck(JsonNode node) {
    Boolean value = parseBoolean(node);
    if (value == null) {
      if (this.required) {
        throw new IllegalArgumentException("`" + this.getPath() + "`参数缺失");
      } else {
        return null;
      }
    }
    return value;
  }

  @Override
  public final ParamBoolean asBoolean() {
    return this;
  }

  @Override
  public boolean isBoolean() {
    return true;
  }
}
