package com.open.param;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.open.utils.ErrorUtils;
import com.open.json.api.GsonSerialize;
import org.apache.commons.lang3.StringUtils;

public class ParamBase implements Param {

  protected String name;
  protected boolean required;
  protected DataType dataType;
  protected String description;
  // 父亲节点
  public transient Param parentNode;

  // 子节点(ParamArray,ParamObject)
  ParamBase[] children = new ParamBase[0];
  // 限制最小输入值(ParamPrimitive)
  Number min;
  // 限制最大输入值(ParamPrimitive)
  Number max;
  /**
   * 示例值(只有ParamPrimitive类型节点才会有效)
   */
  String exampleValue;

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

  public String getName() {
    return name;
  }

  public boolean isRequired() {
    return required;
  }

  public DataType getDataType() {
    return dataType;
  }

  public String getDescription() {
    return description;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setRequired(boolean required) {
    this.required = required;
  }

  public void setDataType(DataType dataType) {
    this.dataType = dataType;
  }

  public ParamBase setDescription(String description) {
    this.description = description;
    return this;
  }

  @Override
  public final Param setParentNode(Param parentNode) {
    this.parentNode = parentNode;
    return this;
  }

  @Override
  @JsonIgnore
  public final Param getParentNode() {
    return parentNode;
  }

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
    return dataType == DataType.Object;
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
      ParamBase param = null;
      if (children != null && children.length > 0) {
        param = children[0];
      }
      return (ParamArray) new ParamArray(name, required, description, param)
          .setParentNode(this.parentNode);
    }
    throw ErrorUtils.newClassCastException(this.getClass(), ParamArray.class);
  }

  @Override
  public ParamObject asObject() {
    if (isObject()) {
      return new ParamObject(name, required, description, children);
    }
    throw ErrorUtils.newClassCastException(this.getClass(), ParamObject.class);
  }

  @Override
  public ParamPrimitive asPrimitive() {
    if (isPrimitive()) {
      return (ParamPrimitive) new ParamPrimitive(name, required, dataType, description)
          .between(min, max)
          .setExampleValue(exampleValue).setParentNode(this.parentNode);
    }
    throw ErrorUtils.newClassCastException(this.getClass(), ParamPrimitive.class);
  }

  @Override
  public boolean equals(Object obj) {
    String this_json = GsonSerialize.INSTANCE.encode(this);
    String input_json = GsonSerialize.INSTANCE.encode(obj);
    return this_json.equals(input_json);
  }

}
