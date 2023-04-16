package io.github.validate.param;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.validate.utils.ErrorUtils;
import io.github.validate.json.api.GsonSerialize;
import org.apache.commons.lang3.StringUtils;

public class ParamBase implements Param {
	private String name;
	private boolean required;
	private DataType dataType;
	private String description;
	// 父亲节点
	public transient Param parentNode;

	// 子节点(ParamArray,ParamObject)
	ParamBase[] children = new ParamBase[0];
	// 限制最小输入值(Primitive)
	Number min;
	// 限制最大输入值(Primitive)
	Number max;
	/**
	 * 示例值(只有Primitive类型节点才会有效)
	 */
	String exampleValue;

	public ParamBase() {
	}

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
	public final void setParentNode(Param parentNode) {
		this.parentNode = parentNode;
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
		return DataType.isPrimitive(dataType);
	}

	@Override
	public boolean isArray() {
		return dataType == DataType.Array;
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
			return new ParamArray(name, required, description, param);
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
	public Primitive asPrimitive() {
		if (isPrimitive()) {
			return new Primitive(name, required, dataType, description, min, max).setExampleValue(exampleValue);
		}
		throw ErrorUtils.newClassCastException(this.getClass(), Primitive.class);
	}

	@Override
	public boolean equals(Object obj) {
		String this_json = GsonSerialize.INSTANCE.encode(this);
		String input_json = GsonSerialize.INSTANCE.encode(obj);
		return this_json.equals(input_json);
	}

}
