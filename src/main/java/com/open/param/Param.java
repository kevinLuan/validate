package com.open.param;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 参数定义
 * 
 * @author KEVIN LUAN
 */
public interface Param {
	Logger LOG = LoggerFactory.getLogger(Param.class);

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
	Primitive asPrimitive();

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
	void setParentNode(Param parentNode);

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
	boolean isRequire();

	String getPath();
}
