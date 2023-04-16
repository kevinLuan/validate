package io.github.validate.param.api;

import io.github.validate.json.api.JsonUtils;
import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.validate.datatype.NumberParser;
import io.github.validate.param.DataType;
import io.github.validate.param.Param;
import io.github.validate.param.ParamArray;
import io.github.validate.param.ParamObject;

class CheckParam {
	private ApiBase<?> apiBase;

	private CheckParam(ApiBase<?> apiBase) {
		this.apiBase = apiBase;
	}

	public static CheckParam getInstance(ApiBase<?> apiBase) {
		return new CheckParam(apiBase);
	}

	public String getTipError(String path) {
		return apiBase.getTipError(path);
	}

	public String getTipMissing(String path) {
		return apiBase.getTipMissing(path);
	}

	public void checkResponse(Param param, JsonNode jsonNode) {
		if (param == null) {
			throw new IllegalArgumentException("param must be not null");
		}
		if (param.isRequired()) {
			if (JsonUtils.isNull(jsonNode)) {
				throw new IllegalArgumentException(getTipMissing(param.getPath()));
			}
		} else {// 参数允许为空
			if (JsonUtils.isNull(jsonNode)) {
				return;
			}
		}
		if (param.isPrimitive()) {
			check_simple(param, jsonNode);
		} else if (param.isArray()) {
			check_array(param, jsonNode);
		} else if (param.isObject()) {
			check_object(param, jsonNode);
		} else {
			throw new IllegalArgumentException("不支持的类型:" + param);
		}
	}

	public void checkParams(HttpServletRequest request, Param... params) {
		if (request == null) {
			throw new IllegalArgumentException("request must be not null");
		}
		for (Param param : params) {
			String name = param.getName();
			String value = request.getParameter(name);
			if (param.isRequired()) {
				if (value == null) {
					throw new IllegalArgumentException(getTipMissing(param.getPath()));
				}
			} else {// 参数允许为空
				if (value == null) {
					continue;
				}
			}
			if (param.isPrimitive()) {
				check_simple(param, JsonNodeFactory.instance.textNode(value));
			} else {
				JsonNode jsonNode = null;
				try {
					jsonNode = JsonUtils.parser(value);
				} catch (Exception e) {
					throw new IllegalArgumentException(getTipError(param.getPath()));
				}
				if (param.isArray()) {
					check_array(param, jsonNode);
				} else if (param.isObject()) {
					check_object(param, jsonNode);
				} else {
					throw new IllegalArgumentException("不支持的类型:" + param);
				}
			}
		}
	}

	void check_array(Param param, JsonNode value) {
		if (param.isArray() && value.isArray()) {
			ParamArray array = param.asArray();
			if (!array.existsChildren()) {
				return;// 没有子节点
			}
			Param children = array.getChildrenAsParam();
			if (array.isRequired()) {
				if (value.size() == 0) {
					throw new IllegalArgumentException(param.getPath() + "[]不能为空");
				}
			}
			for (int i = 0; i < value.size(); i++) {
				JsonNode node = value.get(i);
				if (children.isObjectValue()) {
					check_object(children, (ObjectNode) node);
				} else if (children.isPrimitive()) {
					check_simple(children.asPrimitive(), node);
				} else {
					throw new IllegalArgumentException("不支持的类型" + children);
				}
			}
		} else {
			throw new IllegalArgumentException(getTipError(param.getPath()));
		}
	}

	void check_simple(Param param, JsonNode node) {
		if (param.isPrimitive()) {
			if (node.isObject() || node.isArray()) {
				throw new IllegalArgumentException(getTipError(param.getPath()));
			}
			String value = JsonUtils.toString(node);
			if (param.getDataType().isNumber()) {
				try {
					NumberParser.parse(value, param.isRequired()).check(param.asPrimitive());
				} catch (NumberFormatException e) {
					if (param.getParentNode() != null) {
						if (param.getParentNode().isArray()) {
							if (param.asPrimitive().existBetweenCheck()) {
								String msg = param.asPrimitive().getTipMsg(param.getPath() + "[]");
								throw new IllegalArgumentException(msg);
							} else {
								throw new IllegalArgumentException("`" + param.getPath() + "[]`只能包含数字");
							}
						}
					}
					if (param.asPrimitive().existBetweenCheck()) {
						String msg = param.asPrimitive().getTipMsg(param.getPath());
						throw new IllegalArgumentException(msg);
					} else {
						throw new IllegalArgumentException("`" + param.getPath() + "`必须是一个数字");
					}

				}
			} else if (param.getDataType().isString()) {
				DataType.String.check(param.asPrimitive(), value);
			} else {
				throw new IllegalArgumentException("不支持的类型:" + param.getDataType());
			}
		}
	}

	void check_object(Param param, JsonNode jsonNode) {
		if (!param.isObject() || !jsonNode.isObject()) {
			throw new IllegalArgumentException(getTipError(param.getPath()));
		}
		ParamObject obj = param.asObject();
		ObjectNode objNode = (ObjectNode) jsonNode;
		for (Param p : obj.getChildren()) {
			JsonNode value = objNode.get(p.getName());
			if (p.isRequired()) {
				if (JsonUtils.isNull(value)) {
					throw new IllegalArgumentException(getTipMissing(p.getPath()));
				}
			} else {
				if (JsonUtils.isNull(value)) {
					continue;
				}
			}
			if (p.isObject()) {
				check_object(p, value);
			} else if (p.isArray()) {
				check_array(p, value);
			} else if (p.isPrimitive()) {
				check_simple(p, value);
			} else {
				throw new IllegalArgumentException("不支持的类型:" + p);
			}

		}
	}
}
