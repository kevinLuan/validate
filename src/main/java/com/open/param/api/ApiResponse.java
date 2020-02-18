package com.open.param.api;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import com.fasterxml.jackson.databind.JsonNode;
import com.open.param.Param;

/**
 * API请求响应验证处理
 *
 * @author KEVIN LUAN
 */
public class ApiResponse extends ApiBase<JsonNode> {

  public Param responseParam;
  private CheckParam checkParam = CheckParam.getInstance(this);

  private ApiResponse(Param responseParam) {
    this.responseParam = responseParam;
  }

  @Override
  public ApiResponse setReference() {
    responseParam = AdjustParamInstance.adjust(responseParam);
    ParentReference.refreshParentReference(responseParam);
    return this;
  }

  /**
   * 根据API返回协议格式定义进行合法性验证
   */
  @Override
  public ApiResponse check(JsonNode jsonNode) {
    checkParam.checkResponse(responseParam, jsonNode);
    return this;
  }

  /**
   * 根据返回数据格式定义extract数据
   */
  @Override
  public Map<String, Object> extract(JsonNode jsonNode) {
    if (responseParam == null) {
      throw new IllegalArgumentException("responseParam must be not null");
    }
    object(jsonNode, responseParam);
    Iterator<String> iterator = jsonNode.fieldNames();
    Map<String, Object> data = new HashMap<>(responseParam.asObject().getChildren().length);
    while (iterator.hasNext()) {
      String key = iterator.next();
      data.put(key, jsonNode.get(key));
    }
    return data;
  }

  public static ApiCheck<JsonNode> make(Param param) {
    return new ApiResponse(param).setReference();
  }

  @Override
  public String getTipError(String path) {
    return "下游服务返回数据错误->`" + path + "`";
  }

  @Override
  public String getTipMissing(String path) {
    return "下游服务返回数据缺失->`" + path + "`";
  }
}
