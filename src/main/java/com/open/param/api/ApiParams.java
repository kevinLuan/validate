package com.open.param.api;

import com.open.json.api.JsonUtils;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.open.param.Param;

/**
 * API 请求参数定义验证
 * 
 * @author KEVIN LUAN
 */
public class ApiParams extends ApiBase<HttpServletRequest> {
	public List<Param> paramList = new LinkedList<>();
	private CheckParam checkParam = CheckParam.getInstance(this);

	private ApiParams(Param... params) {
		for (Param p : params) {
			paramList.add(p);
		}
	}

	public static ApiParams make(Param... params) {
		return new ApiParams(params).setReference();
	}

	@Override
	public ApiParams setReference() {
		AdjustParamInstance.adjust(paramList);
		ParentReference.refreshParentReference(paramList);
		return this;
	}



	/**
	 * 请求参数合法性验证
	 * 
	 * @param request
	 * @return
	 */
	@Override
	public ApiParams check(HttpServletRequest request) {
		Param[] params = new Param[paramList.size()];
		paramList.toArray(params);
		checkParam.checkParams(request, params);
		return this;
	}

	/**
	 * 根据参数定义extract参数(只会拷贝定义的参数)
	 * 
	 * @return
	 */
	@Override
	public Map<String, Object> extract(HttpServletRequest request) {
		Map<String, Object> data = new HashMap<>(paramList.size());
		for (Param param : paramList) {
			String value = request.getParameter(param.getName());
			if (value == null) {
				continue;
			}
			if (param.isPrimitive()) {
				data.put(param.getName(), value);
			} else {
				JsonNode node = JsonUtils.parser(value);
				if (param.isArray()) {
					if (node.isArray()) {
						array((ArrayNode) node, param.asArray());
					} else {
						throw new IllegalArgumentException(getTipError(param.getPath()));
					}

				} else if (param.isObject()) {
					object((ObjectNode) node, param.asObject());
				} else {
					throw new IllegalArgumentException("不支持的操作" + param);
				}
				data.put(param.getName(), node);
			}
		}
		return data;
	}

	@Override
	public String getTipError(String path) {
		return "`" + path + "`参数错误";
	}

	@Override
	public String getTipMissing(String path) {
		return "`" + path + "`参数缺失";
	}
}
