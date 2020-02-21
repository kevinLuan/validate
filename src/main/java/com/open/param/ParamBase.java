package com.open.param;

import com.open.param.core.AdjustParamInstance;
import com.open.param.core.ParentReference;
import java.util.Arrays;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.open.json.api.GsonSerialize;
import com.open.param.validate.Validate;
import com.open.utils.ErrorUtils;

@SuppressWarnings("rawtypes")
public class ParamBase implements Param {

  protected String name;
  protected boolean required;
  protected DataType dataType;
  protected String description;
  @JsonIgnore
  // 父亲节点
  protected transient ParamBase parentNode;

  // 子节点(ParamArray,ParamObject)
  protected ParamBase[] children = new ParamBase[0];
  // 限制最小输入值(ParamPrimitive)
  Number min;
  // 限制最大输入值(ParamPrimitive)
  Number max;
  /**
   * 示例值(只有ParamPrimitive类型节点才会有效)
   */
  String exampleValue;
  // 任意匹配
  protected Validate[] anyMatchs = new Validate[0];
  // 全部匹配
  protected Validate[] allMatchs = new Validate[0];
  @JsonIgnore
  // 优化状态
  private transient boolean optimizeStatus = false;

  @Override
  public Param anyMatch(Validate... rules) {
    Objects.requireNonNull(rules, "`rules`不能为空");
    this.anyMatchs = rules;
    dataType.assertValidate(rules);
    return this;
  }

  @Override
  public Param allMatch(Validate... rules) {
    Objects.requireNonNull(rules, "`rules`不能为空");
    this.allMatchs = rules;
    dataType.assertValidate(rules);
    return this;
  }

  @Override
  public Validate[] getAnyMatchRules() {
    return anyMatchs;
  }

  @Override
  public Validate[] getAllMatchRule() {
    return allMatchs;
  }

  public ParamBase() {}

  public ParamBase(String name, boolean required, DataType dataType, String description) {
    this.name = name;
    this.required = required;
    this.dataType = dataType;
    this.description = description;
  }

  public ParamBase(String name, boolean required, DataType dataType) {
    this.name = name;
    this.required = required;
    this.dataType = dataType;
  }

  public ParamBase setDescription(String description) {
    this.description = description;
    return this;
  }

  @Override
  public final Param setParentNode(ParamBase parentNode) {
    this.parentNode = parentNode;
    return this;
  }

  @Override
  public final String getPath() {
    return NodeHelper.parser(this).getPath();
  }

  @Override
  public final boolean isRootNode() {
    return parentNode == null;
  }

  @Override
  public boolean isPrimitive() {
    return dataType.isPrimitive();
  }

  @Override
  public boolean isArray() {
    return dataType.isArray();
  }

  @Override
  public boolean isObject() {
    return dataType.isObject();
  }

  @Override
  public boolean isObjectValue() {
    if (isObject() && StringUtils.isBlank(getName())) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  public ParamArray asArray() {
    if (isArray()) {
      Param param = null;
      if (children != null && children.length > 0) {
        param = children[0];
      }
      return new ParamArray(name, required, description, param)
          .anyMatch(this.getAnyMatchRules())
          .allMatch(this.getAllMatchRule())
          .setParentNode(this.parentNode).asArray();
    }
    throw ErrorUtils.newClassCastException(this.getClass(), ParamArray.class);
  }

  @Override
  public ParamObject asObject() {
    if (isObject()) {
      return new ParamObject(name, required, description, children)
          .setParentNode(this.parentNode)
          .anyMatch(this.getAnyMatchRules())
          .allMatch(this.getAllMatchRule())
          .asObject();
    }
    throw ErrorUtils.newClassCastException(this.getClass(), ParamObject.class);
  }

  @Override
  public ParamPrimitive asPrimitive() {
    if (isPrimitive()) {
      return new ParamPrimitive(name, required, dataType, description)
          .between(min, max).setExampleValue(exampleValue)
          .setParentNode(this.parentNode)
          .anyMatch(this.getAnyMatchRules())
          .allMatch(this.getAllMatchRule()).asPrimitive();
    }
    throw ErrorUtils.newClassCastException(this.getClass(), ParamPrimitive.class);
  }

  @Override
  public boolean equals(Object obj) {
    String this_json = GsonSerialize.INSTANCE.encode(this);
    String input_json = GsonSerialize.INSTANCE.encode(obj);
    return this_json.equals(input_json);
  }

  /**
   * 验证数据
   */
  @SuppressWarnings("unchecked")
  protected final boolean check(JsonNode node) {
    boolean ok = true;
    Object value = parseAndCheck(node);
    if (allMatchs.length > 0) {
      ok = Arrays.asList(allMatchs).stream().allMatch(validate -> validate.test(this, value));
    }
    if (ok & anyMatchs.length > 0) {
      ok = Arrays.asList(anyMatchs).stream().anyMatch(validate -> validate.test(this, value));
    }
    return ok;
  }

  public final void asserValue(JsonNode node) {
    if (!check(node)) {
      throw new IllegalArgumentException("`" + getPath() + "`参数无效");
    }
  }

  /**
   * 解析并转化到目标类型，并完基础验证逻辑(this.min,this.max),验证是否必传
   */
  protected Object parseAndCheck(JsonNode value) {
    return value;
  }

  public Param getParentNode() {
    return parentNode;
  }

  public String getDescription() {
    return description;
  }

  public DataType getDataType() {
    return dataType;
  }

  public void setDataType(DataType dataType) {
    this.dataType = dataType;
  }

  public boolean isRequired() {
    return required;
  }

  public void setRequired(boolean required) {
    this.required = required;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public boolean isNumber() {
    return dataType.isNumber();
  }

  @Override
  public boolean isString() {
    return dataType.isString();
  }

  @Override
  public ParamNumber asNumber() {
    if (isNumber()) {
      return ParamNumber.make(name, required, description)
          .between(min, max).setExampleValue(exampleValue)
          .setParentNode(this.parentNode)
          .anyMatch(this.getAnyMatchRules())
          .allMatch(this.getAllMatchRule()).asNumber();
    }
    throw ErrorUtils.newClassCastException(this.getClass(), ParamNumber.class);
  }

  @Override
  public ParamString asString() {
    if (isString()) {
      return ParamString.make(name, required, description)
          .between(min, max).setExampleValue(exampleValue)
          .setParentNode(this.parentNode)
          .anyMatch(this.getAnyMatchRules())
          .allMatch(this.getAllMatchRule()).asString();
    }
    throw ErrorUtils.newClassCastException(this.getClass(), ParamString.class);
  }

  @Override
  public boolean isAny() {
    return this.dataType.isAny();
  }

  @Override
  public ParamAny asAny() {
    if (isAny()) {
      return ParamAny.create(required)
          .name(name)
          .description(description)
          .setParentNode(parentNode)
          .anyMatch(getAnyMatchRules())
          .allMatch(getAllMatchRule())
          .asAny();
    }
    throw ErrorUtils.newClassCastException(this, ParamAny.class);
  }

  @Override
  public boolean isBoolean() {
    return dataType.isBoolean();
  }

  @Override
  public ParamBoolean asBoolean() {
    if (isBoolean()) {
      return ParamBoolean.make(name, required, description)
          .between(min, max)
          .setExampleValue(exampleValue)
          .setParentNode(this.parentNode)
          .anyMatch(this.getAnyMatchRules())
          .allMatch(this.getAllMatchRule())
          .asBoolean();
    }
    throw ErrorUtils.newClassCastException(this.getClass(), ParamBoolean.class);
  }

  @Override
  public Param optimize() {
    if (this.optimizeStatus == false) {
      ParamBase param = (ParamBase) AdjustParamInstance.adjust(this);
      ParentReference.refreshParentReference(param);
      param.optimizeStatus = true;
      return param;
    }
    return this;
  }
}
