package io.github.validate.param;

/**
 * 对象ObjectNode参数
 * 
 * @author KEVIN LUAN
 */
public class ParamObject extends ParamBase {

	ParamObject() {
		super();
	}

	public ParamObject(String name, boolean require, String description, Param[] children) {
		super(name, require, DataType.Object, description);
		if (children != null) {
			this.children = new ParamBase[children.length];
			for (int i = 0; i < children.length; i++) {
				Param param = children[i];
				this.children[i] = (ParamBase) param;
				if (param.isObjectValue()) {
					throw new IllegalArgumentException("ParamObject子节点Name不能为空");
				}
			}
		}
	}

	public static ParamObject require(String name, String description, Param... children) {
		return new ParamObject(name, true, description, children);
	}

	public static ParamObject require(Param... children) {
		return new ParamObject("", true, null, children);
	}

	public static ParamObject optional(Param... children) {
		return new ParamObject("", false, null, children);
	}

	public static ParamObject optional(String name, String description, Param... children) {
		return new ParamObject(name, false, description, children);
	}

	public boolean isObject() {
		return true;
	}

	@Override
	public ParamObject asObject() {
		return this;
	}

	public final boolean existsChildren() {
		return this.children != null && children.length > 0;
	}

	public Param[] getChildren() {
		return children;
	}
}
