package com.open.param.api;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import com.fasterxml.jackson.databind.JsonNode;
import com.open.json.api.JsonUtils;
import com.open.param.Param;

public class ApiHelper {
  private ApiCheck<HttpServletRequest> requestCheck;
  private ApiCheck<JsonNode> responseCheck;

  public static ApiHelper make(Param[] request, Param response) {
    ApiHelper helper = new ApiHelper();
    helper.responseCheck = ApiResponse.make(response);
    helper.requestCheck = ApiParams.make(request);
    return helper;
  }

  /**
   * 设置未知Node节点过滤器
   * 
   * @param filter
   */
  public ApiHelper setUnknownNodeFilter(UnknownNodeFilter filter) {
    if (this.requestCheck != null) {
      this.requestCheck.setUnknownNodeFilter(filter);
    }
    if (this.responseCheck != null) {
      this.responseCheck.setUnknownNodeFilter(filter);
    }
    return this;
  }

  public static ApiHelper request(Param... params) {
    ApiHelper helper = new ApiHelper();
    helper.requestCheck = ApiParams.make(params);
    return helper;
  }

  public static ApiHelper response(Param response) {
    ApiHelper helper = new ApiHelper();
    helper.responseCheck = ApiResponse.make(response);
    return helper;
  }

  public ApiHelper checkRequest(HttpServletRequest request) {
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

  public ApiHelper checkResponse(JsonNode jsonNode) {
    responseCheck.check(jsonNode);
    return this;
  }

  public ApiHelper checkResponse(Object data) {
    String json = JsonUtils.stringify(data);
    JsonNode jsonNode = JsonUtils.parser(json);
    responseCheck.check(jsonNode);
    return this;
  }

  /**
   * 提取数据
   * 
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
