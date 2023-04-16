package io.github.validate.param;

import org.apache.commons.lang3.StringUtils;

/**
 * 数组参数类型
 * 
 * @author KEVIN LUAN
 */
public class ParamArray extends ParamBase {

	public ParamArray() {
	}

	public ParamArray(String name, boolean required, String description, ParamBase childrens) {
		super(name, required, DataType.Array, description);
		check(childrens);
		if (childrens != null) {
			this.children = new ParamBase[] { childrens };
		}
	}

	private void check(Param childrens) {
		if (childrens != null) {
			if (childrens.getDataType() == DataType.Array) {
				throw new IllegalArgumentException("无效的数据格式(数组不应该直接嵌套数组)");
			} else{
				if (StringUtils.isNotBlank(childrens.getName())) {
					throw new IllegalArgumentException("ParamArray节点的子节点不应该存在节点名称");
				}
			}
		}
	}

	/**
	 * 创建一个必须参数
	 * 
	 * @param name
	 * @return
	 */
	public static ParamArray required(String name, String description, ParamBase childrens) {
		return new ParamArray(name, true, description, childrens);
	}

	/**
	 * 创建一个必须的Array节点，任意类型的子节点
	 * 
	 * @param name
	 * @param description
	 * @return
	 */
	public static ParamArray required(String name, String description) {
		return new ParamArray(name, true, description, null);
	}

	public static ParamArray optional(String name, String description, ParamBase childrens) {
		return new ParamArray(name, false, description, childrens);
	}

	/**
	 * 创建一个非必须的Array节点，任意类型的子节点
	 * 
	 * @param name
	 * @param description
	 * @return
	 */
	public static ParamArray optional(String name, String description) {
		return new ParamArray(name, false, description, null);
	}

	@Override
	public ParamArray asArray() {
		return this;
	}

	public Param[] getChildren() {
		return children;
	}
	
	public final Param getChildrenAsParam() {
		if (children != null && children.length > 0) {
			return children[0];
		}
		return null;
	}

	public final boolean existsChildren() {
		return this.children != null && children.length > 0;
	}
}
