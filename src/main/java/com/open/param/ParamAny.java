package com.open.param;

import com.fasterxml.jackson.databind.JsonNode;
import com.open.json.api.JsonUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Any类型参数（当Value为null时无法推断出具体类型，因此使用Any来定义）
 */
public class ParamAny extends ParamBase {

  protected ParamAny() {
  }

  public static ParamAny create() {
    ParamAny any = new ParamAny();
    any.dataType = DataType.Any;
    any.name = "";
    return any;
  }

  public static ParamAny create(boolean required) {
    ParamAny any = new ParamAny();
    any.dataType = DataType.Any;
    any.name = "";
    any.required = required;
    return any;
  }

  public ParamAny name(String name) {
    super.setName(name);
    return this;
  }

  public ParamAny required() {
    super.required = true;
    return this;
  }

  public ParamAny description(String desc) {
    super.description = desc;
    return this;
  }

  public final String toParamAnyCode() {
    StringBuilder builder = new StringBuilder();
    if (isRequired()) {
      builder.append("ParamAny.create(true)");
    } else {
      builder.append("ParamAny.create()");
    }
    appendNameAndDesc(builder);
    return builder.toString();
  }

  private void appendNameAndDesc(StringBuilder builder) {
    if (StringUtils.isNotBlank(name)) {
      builder.append(".name(\"" + name + "\")");
    }
    if (StringUtils.isNotBlank(description)) {
      builder.append(".description(\"" + description + "\")");
    }
  }

  public String toParamApiCode() {
    StringBuilder builder = new StringBuilder();
    if (isRequired()) {
      builder.append("ParamApi.any(true)");
    } else {
      builder.append("ParamApi.any()");
    }
    appendNameAndDesc(builder);
    return builder.toString();
  }

  @Override
  protected Object parseAndCheck(JsonNode value) {
    if (required) {
      if (JsonUtils.isNull(value)) {
        throw new IllegalArgumentException("`" + getPath() + "`参数不能为空");
      }
    }
    return value;
  }

  @Override
  public final ParamAny asAny() {
    return this;
  }

  @Override
  public final boolean isAny() {
    return true;
  }

}
