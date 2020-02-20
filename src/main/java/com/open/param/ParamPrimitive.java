package com.open.param;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.TextNode;
import com.open.param.common.GenerateCode;
import com.open.param.common.NotSupportException;
import com.open.utils.ErrorUtils;

/**
 * 基本类型参数
 * 
 */
public class ParamPrimitive extends ParamBase {

  protected ParamPrimitive() {}

  public ParamPrimitive(String name, boolean required, DataType dataType, String description) {
    super(name, required, dataType, description);
    if (!dataType.isPrimitive()) {
      throw new IllegalArgumentException("无效的数据类型:" + dataType);
    }
  }

  public static ParamPrimitive make(String name, boolean required, DataType dataType,
      String description, Number min,
      Number max) {
    return new ParamPrimitive(name, required, dataType, description).between(min, max);
  }

  /**
   * 创建一个必须参数
   */
  public static ParamPrimitive required(String name, DataType dataType, String description) {
    return ParamPrimitive.make(name, true, dataType, description, null, null);
  }

  /**
   * 创建一个必须参数
   * <p>
   * 当前基本类型只能用在父节点是Array的情况例如：array[0,1,2]
   */
  public static ParamPrimitive required(DataType dataType, String description) {
    return ParamPrimitive.make("", true, dataType, null, null, null);
  }

  /**
   * 创建一个非必须参数
   */
  public static ParamPrimitive of(String name, DataType dataType, String description) {
    return ParamPrimitive.make(name, false, dataType, description, null, null);
  }

  /**
   * 创建一个非必须参数
   * <p>
   * 当前基本类型只能用在父节点是Array的情况例如：array[0,1,2]
   */
  public static ParamPrimitive of(DataType dataType, String description) {
    return ParamPrimitive.make("", false, dataType, description, null, null);
  }

  @Override
  public final ParamPrimitive asPrimitive() {
    return this;
  }

  @Override
  public boolean isPrimitive() {
    return true;
  }

  public Number getMin() {
    return min;
  }

  public Number getMax() {
    return max;
  }

  public ParamPrimitive setMin(Number min) {
    this.min = min;
    this.check();
    return this;
  }

  private void check() {
    if (min != null && max != null) {
      if (String.valueOf(min).indexOf(".") != -1 || String.valueOf(max).indexOf(".") != -1) {
        if (min.doubleValue() > max.doubleValue()) {
          throw new IllegalArgumentException("`" + max + "`必须大于`" + min + "`");
        }
      } else {
        if (min.longValue() > max.longValue()) {
          throw new IllegalArgumentException("`" + max + "`必须大于`" + min + "`");
        }
      }
    }
  }

  public ParamPrimitive between(Number min, Number max) {
    this.setMin(min);
    this.setMax(max);
    return this;
  }

  public ParamPrimitive setMax(Number max) {
    this.max = max;
    this.check();
    return this;
  }

  public String getTipMsg() {
    return this.getTipMsg(getPath());
  }

  public String getTipMsg(String path) {
    if (getDataType().isNumber()) {
      if (this.min != null && this.max != null) {
        return "`" + path + "`限制范围" + min + "~" + max;
      } else if (this.min != null) {
        return "`" + path + "`必须大于等于" + min;
      } else if (this.max != null) {
        return "`" + path + "`必须小于等于" + this.max;
      } else {
        return "`" + path + "`必须是一个数字";
      }
    } else if (getDataType().isString()) {
      if (this.min != null && this.max != null) {
        return "`" + path + "`长度限制在" + min + "~" + max;
      } else if (this.min != null) {
        return "`" + path + "`长度必须大于等于" + min;
      } else if (this.max != null) {
        return "`" + path + "`长度必须小于等于" + this.max;
      }
    }
    return "`" + getPath() + "`参数错误";
  }

  /**
   * 存在范围验证
   */
  public boolean existBetweenCheck() {
    return max != null || min != null;
  }

  /**
   * 获取示例原始值
   */
  public String getExampleValue() {
    return exampleValue;
  }

  /**
   * 设置示例值(可以用户mock数据)
   */
  public ParamPrimitive setExampleValue(Object exampleValue) {
    super.exampleValue = String.valueOf(exampleValue);
    return this;
  }

  public ParamNumber asNumber() {
    if (this.dataType == DataType.Number) {
      return ParamNumber.make(name, required, description)
          .between(min, max)
          .setParentNode(parentNode)
          .anyMatch(this.getAnyMatchRules())
          .allMatch(this.getAllMatchRule())
          .asPrimitive().setExampleValue(exampleValue).asNumber();


    }
    throw ErrorUtils.newClassCastException(this.getClass(), ParamNumber.class);
  }

  public ParamString asString() {
    if (this.dataType == DataType.String) {
      return ParamString.make(name, required, description)
          .between(min, max)
          .setParentNode(parentNode)
          .anyMatch(this.getAnyMatchRules())
          .allMatch(this.getAllMatchRule())
          .asPrimitive().setExampleValue(exampleValue).asString();
    }
    throw ErrorUtils.newClassCastException(this.getClass(), ParamString.class);
  }

  /**
   * 解析目标类型的Value
   */
  @Override
  public Object parseAndCheck(JsonNode node) {
    if (dataType.isNumber()) {
      return asNumber().parseAndCheck(node);
    } else if (dataType.isBoolean()) {
      return asBoolean().parseAndCheck(node);
    } else if (dataType.isString()) {
      return asString().parseAndCheck(node);
    } else {
      throw NotSupportException.of("不支持的类型`" + dataType + "`");
    }
  }

  public Object parseRawValue(String value) {
    TextNode node = JsonNodeFactory.instance.textNode(value);
    return parseAndCheck(node);
  }

  public String toJavaCode() {
    StringBuilder builder = new StringBuilder();
    String desc = GenerateCode.formatParam(description);
    if (name.length() > 0) {
      if (isRequired()) {
        ParamPrimitive.required(name, dataType, description);
        builder.append("ParamPrimitive.required(" + GenerateCode.formatParam(name) + ","
            + dataType.toJavaCode() + ","
            + desc + ")");
      } else {
        builder.append("ParamPrimitive.of(" + GenerateCode.formatParam(name) + ","
            + dataType.toJavaCode() + ", "
            + desc + ")");
      }
    } else {
      if (isRequired()) {
        builder
            .append("ParamPrimitive.required(" + dataType.toJavaCode() + "," + desc + ")");
      } else {
        builder.append("ParamPrimitive.of(" + dataType.toJavaCode() + "," + desc + ")");
      }
    }
    builder.append(GenerateCode.buildExampleValue(this));
    return builder.toString();
  }
}
