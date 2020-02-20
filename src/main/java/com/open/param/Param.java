package com.open.param;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.open.param.validate.Validate;

/**
 * 参数定义
 *
 * @author KEVIN LUAN
 */
@SuppressWarnings("rawtypes")
public interface Param {

  Logger LOGGER = LoggerFactory.getLogger(Param.class);

  /**
   * 获取参数名称
   */
  String getName();

  /**
   * 基础原子参数类型
   */
  boolean isPrimitive();

  /**
   * 类型转化
   */
  ParamPrimitive asPrimitive();

  /**
   * 验证是否是{@link ParamArray}类型实现
   */
  boolean isArray();

  /**
   * 验证是否是ObjectParam类型实现
   */
  boolean isObject();

  /**
   * 类型转化
   */
  ParamArray asArray();

  /**
   * 类型转化
   */
  ParamObject asObject();

  boolean isAny();

  ParamAny asAny();

  boolean isBoolean();

  ParamBoolean asBoolean();

  /**
   * 设置当前节点的父亲节点
   */
  Param setParentNode(ParamBase parentNode);

  /**
   * 获取父亲节点
   */
  public Param getParentNode();

  /**
   * 跟节点
   */
  public boolean isRootNode();

  /**
   * 是ObjectNode值类型(如：Array[{ObjectNode},{ObjectNode}])
   */
  boolean isObjectValue();

  /**
   * 获取数据类型
   */
  DataType getDataType();

  /**
   * 参数是否为必须的
   */
  boolean isRequired();

  String getPath();

  /**
   * 验证参数只要有任意一个匹配即可
   *
   * @param rules 验证规则
   */

  Param anyMatch(Validate... rules);

  /**
   * 获取任意匹配规则
   */
  Validate[] getAnyMatchRules();

  /**
   * 所有验证规则均需要匹配成功
   *
   * @param rules 验证规则
   */
  Param allMatch(Validate... rules);

  /**
   * 获取所有匹配规则
   */
  Validate[] getAllMatchRule();

  boolean isString();

  boolean isNumber();

  ParamNumber asNumber();

  ParamString asString();

  /**
   * 性能优化并返回优化后的对象实例
   */
  Param optimize();
}
