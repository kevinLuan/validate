package io.github.validate.param.api;

import io.github.validate.domain.api.DataResult;
import io.github.validate.json.api.JsonUtils;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import io.github.validate.param.Param;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.databind.JsonNode;

public class Validation {
	private ApiCheck<HttpServletRequest> requestCheck;
	private ApiCheck<JsonNode> responseCheck;

	public static Validation make(Param[] request, Param response) {
		Validation helper = new Validation();
		helper.responseCheck = ApiResponse.make(response);
		helper.requestCheck = ApiParams.make(request);
		return helper;
	}

	/**
	 * 设置未知Node节点过滤器
	 * 
	 * @param filter
	 */
	public Validation setUnknownNodeFilter(UnknownNodeFilter filter) {
		if (this.requestCheck != null) {
			this.requestCheck.setUnknownNodeFilter(filter);
		}
		if (this.responseCheck != null) {
			this.responseCheck.setUnknownNodeFilter(filter);
		}
		return this;
	}

	public static Validation request(Param... params) {
		Validation helper = new Validation();
		helper.requestCheck = ApiParams.make(params);
		return helper;
	}

	public static Validation response(Param response) {
		Validation helper = new Validation();
		helper.responseCheck = ApiResponse.make(response);
		return helper;
	}

	public Validation checkRequest(HttpServletRequest request) {
		requestCheck.check(request);
		return this;
	}

	/**
	 * 提取参数数据
	 * 
	 * @param request
	 * @return
	 */
	public Map<String, Object> extractRequest(HttpServletRequest request) {
		return requestCheck.extract(request);
	}

	public Validation checkResponse(JsonNode jsonNode) {
		responseCheck.check(jsonNode);
		return this;
	}

	public Validation checkResponse(DataResult<?> dataResult) {
		JsonNode jsonNode = JsonUtils.parser(dataResult.toJSON());
		responseCheck.check(jsonNode);
		return this;
	}

	/**
	 * 提取数据
	 * @return
	 */
	public Map<String, Object> extractResponse(JsonNode json) {
		return responseCheck.extract(json);
	}

	public MultiValueMap<String, String> asMultiValueMap(Map<String, Object> params) {
		MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<String, String>(params.size());
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			paramMap.add(entry.getKey(), entry.getValue().toString());
		}
		return paramMap;
	}

}
