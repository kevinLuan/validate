package com.open.param;

import org.apache.commons.lang3.StringUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.open.param.common.GenerateCode;

/**
 * 原子参数（参数的最小单位）
 *
 * @author KEVIN LUAN
 */
public class ParamNumber extends ParamPrimitive {
  protected ParamNumber(){
  }
  public ParamNumber(String name, boolean required, String description) {
    super(name, required, DataType.Number, description);
  }

  static ParamNumber make(String name, boolean required, String description) {
    return new ParamNumber(name, required, description);
  }

  /**
   * 创建一个必须参数
   */
  public static ParamNumber required(String name, String description) {
    return ParamNumber.make(name, true, description);
  }

  /**
   * 创建一个必须参数 <p> 当前基本类型只能用在父节点是Array的情况例如：array[0,1,2]
   */
  public static ParamNumber required(String description) {
    return ParamNumber.make("", true, description);
  }

  /**
   * 创建一个非必须参数
   */
  public static ParamNumber of(String name, String description) {
    return ParamNumber.make(name, false, description);
  }

  /**
   * 创建一个非必须参数 <p> 当前基本类型只能用在父节点是Array的情况例如：array[0,1,2]
   */
  public static ParamNumber of(String description) {
    return ParamNumber.make("", false, description);
  }

  @Override
  public String getTipMsg() {
    return this.getTipMsg(getPath());
  }

  @Override
  public String getTipMsg(String path) {
    if (this.min != null && this.max != null) {
      return "`" + path + "`限制范围" + min + "~" + max;
    } else if (this.min != null) {
      return "`" + path + "`必须大于等于" + min;
    } else if (this.max != null) {
      return "`" + path + "`必须小于等于" + this.max;
    } else {
      return "`" + path + "`必须是一个数字";
    }
  }

  @Override
  public final String toJavaCode() {
    StringBuilder builder = new StringBuilder();
    String nameStr = GenerateCode.formatParam(name);
    String descStr = GenerateCode.formatParam(description);
    if (required) {
      if (StringUtils.isBlank(name)) {
        builder.append("ParamNumber.required(" + descStr + ")");
      } else {
        builder.append("ParamNumber.required(" + nameStr + "," + descStr + ")");
      }
    } else {
      if (StringUtils.isBlank(name) && StringUtils.isBlank(description)) {
        builder.append("ParamNumber.of(" + descStr + ")");
      } else {
        builder.append("ParamNumber.of(" + nameStr + "," + descStr + ")");
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

  private Number parseNumber(JsonNode node) {
    if (node.isValueNode()) {
      if (node.isNumber()) {
        if (node.isDouble() || node.isFloat()) {
          return node.asDouble();
        } else if (node.isBigInteger() || node.isInt() || node.isLong() || node.isShort() || node
            .isBigDecimal()) {
          return node.asLong();
        }
      } else if (node.isTextual()) {
        return parseNumber(node.asText());
      }
    } else if (node.isMissingNode()) {
      return null;
    }
    throw new IllegalArgumentException("`" + this.getPath() + "`参数错误");
  }

  /**
   * 解析Value
   */
  private Number parseNumber(String value) {
    try {
      if (value.indexOf(".") != -1) {
        return Double.parseDouble(value);
      } else {
        return Long.parseLong(value);
      }
    } catch (NumberFormatException e) {
      if (this.getParentNode() != null) {
        if (this.getParentNode().isArray()) {
          if (this.asPrimitive().existBetweenCheck()) {
            String msg = this.asPrimitive().getTipMsg(this.getPath() + "[]");
            throw new IllegalArgumentException(msg);
          } else {
            throw new IllegalArgumentException("`" + this.getPath() + "[]`只能包含数字");
          }
        }
      }
      if (this.asPrimitive().existBetweenCheck()) {
        String msg = this.asPrimitive().getTipMsg(this.getPath());
        throw new IllegalArgumentException(msg);
      } else {
        throw new IllegalArgumentException("`" + this.getPath() + "`必须是一个数字");
      }
    }
  }

  @Override
  public Object parseAndCheck(JsonNode node) {
    Number number = asNumber().parseNumber(node);
    if (number == null) {
      if (this.required) {
        throw new IllegalArgumentException("`" + this.getPath() + "`参数缺失");
      } else {
        return null;
      }
    }
    if (this.min != null) {
      if (number.longValue() < min.longValue() || number.doubleValue() < min.doubleValue()) {
        throw new IllegalArgumentException(getTipMsg());
      }
    }
    if (this.max != null) {
      if (number.longValue() > max.longValue() || number.doubleValue() > max.doubleValue()) {
        throw new IllegalArgumentException(getTipMsg());
      }
    }
    return number;
  }

  @Override
  public final ParamNumber asNumber() {
    return this;
  }


}
