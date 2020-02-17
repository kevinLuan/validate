package com.open.param;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.JsonElement;
import com.open.param.convert.DataParser;
import com.open.param.convert.ParamConvert;

/**
 * 参数定义
 * 
 * @author KEVIN LUAN
 */
public interface Param {
  Logger LOGGER = LoggerFactory.getLogger(Param.class);

  /**
   * 获取参数名称
   * 
   * @return
   */
  String getName();

  /**
   * 基础原子参数类型
   * 
   * @return
   */
  boolean isPrimitive();

  /**
   * 类型转化
   * 
   * @return
   */
  ParamPrimitive asPrimitive();

  /**
   * 验证是否是{@link ParamArray}类型实现
   * 
   * @return
   */
  boolean isArray();

  /**
   * 验证是否是ObjectParam类型实现
   * 
   * @return
   */
  boolean isObject();

  /**
   * 类型转化
   * 
   * @return
   */
  ParamArray asArray();

  /**
   * 类型转化
   * 
   * @return
   */
  ParamObject asObject();

  /**
   * 设置当前节点的父亲节点
   * 
   * @param parentNode
   */
  Param setParentNode(Param parentNode);

  /**
   * 获取父亲节点
   * 
   * @return
   */
  public Param getParentNode();

  /**
   * 跟节点
   * 
   * @return
   */
  public boolean isRootNode();

  /**
   * 是ObjectNode值类型(如：Array[{ObjectNode},{ObjectNode}])
   * 
   * @return
   */
  boolean isObjectValue();

  /**
   * 获取数据类型
   * 
   * @return
   */
  DataType getDataType();

  /**
   * 参数是否为必须的
   * 
   * @return
   */
  boolean isRequired();

  String getPath();

  /**
   * 将当前Param对象转换到java代码形式的定义
   * 
   * @return
   */
  default String asJavaCode() {
    return ParamConvert.getJavaCode(this);
  }

  /**
   * 获取当前Param定义的目标数据
   * 
   * @return
   */
  default JsonElement asJsonData() {
    return DataParser.fromParamAsJsonData(this);
  }
}
