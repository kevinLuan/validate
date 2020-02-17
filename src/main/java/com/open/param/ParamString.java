package com.open.param;

import com.fasterxml.jackson.databind.JsonNode;
import com.open.param.parser.GenerateCode;
import org.apache.commons.lang3.StringUtils;

/**
 * 原子参数（参数的最小单位）
 *
 * @author KEVIN LUAN
 */
public class ParamString extends ParamPrimitive {

  public ParamString(String name, boolean required, String description) {
    super(name, required, DataType.String, description);
  }

  static ParamString make(String name, boolean required, String description) {
    return new ParamString(name, required, description);
  }

  /**
   * 创建一个必须参数
   */
  public static ParamString required(String name, String description) {
    return ParamString.make(name, true, description);
  }

  /**
   * 创建一个必须参数 <p> 当前基本类型只能用在父节点是Array的情况例如：array[0,1,2]
   */
  public static ParamString required(String description) {
    return ParamString.make("", true, description);
  }

  /**
   * 创建一个非必须参数
   */
  public static ParamString of(String name, String description) {
    return ParamString.make(name, false, description);
  }

  public static ParamString of(String description) {
    return ParamString.make("", false, description);
  }

  @Override
  public String getTipMsg() {
    return this.getTipMsg(getPath());
  }

  @Override
  public String getTipMsg(String path) {
    if (this.min != null && this.max != null) {
      return "`" + path + "`长度限制在" + min + "~" + max;
    } else if (this.min != null) {
      return "`" + path + "`长度必须大于等于" + min;
    } else if (this.max != null) {
      return "`" + path + "`长度必须小于等于" + this.max;
    }
    return "`" + getPath() + "`参数错误";
  }

  @Override
  public final String toJavaCode() {
    StringBuilder builder = new StringBuilder();
    String nameStr = GenerateCode.formatParam(name);
    String descStr = GenerateCode.formatParam(description);
    if (required) {
      if (StringUtils.isBlank(name) && StringUtils.isBlank(description)) {
        builder.append("ParamString.required(" + descStr + ")");
      } else {
        builder.append("ParamString.required(" + nameStr + "," + descStr + ")");
      }
    } else {
      if (StringUtils.isBlank(name)) {
        builder.append("ParamString.of(" + descStr + ")");
      } else {
        builder.append("ParamString.of(" + nameStr + "," + descStr + ")");
      }
    }
    if (max != null && min != null) {
      builder.append(".between(" + min + ", " + max + ")");
    } else if (min != null) {
      builder.append(".setMin(" + min + ")");
    } else if (max != null) {
      builder.append(".setMax(" + max + ")");
    }
    builder.append(GenerateCode.buildExampleValue(this));
    return builder.toString();
  }

  public String parseString(JsonNode node) {
    if (node.isValueNode()) {
      return node.toString();
    } else if (node.isMissingNode()) {
      return null;
    }
    throw new IllegalArgumentException("`" + this.getPath() + "`参数错误");
  }
}
