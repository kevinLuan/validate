package com.open.param.validate;

import java.util.Map;
import com.fasterxml.jackson.databind.JsonNode;
import com.open.json.api.JsonUtils;
import com.open.param.Param;
import com.open.param.core.ApiCheck;
import com.open.param.core.ApiResponse;
import com.open.param.core.UnknownNodeFilter;

public class JsonValidate {

  private ApiCheck<JsonNode> apiCheck;

  public JsonValidate(Param param) {
    this.apiCheck = ApiResponse.make(param);
  }

  public static JsonValidate of(Param param) {
    return new JsonValidate(param);
  }

  /**
   * 设置未知Node节点过滤器
   */
  public JsonValidate setUnknownNodeFilter(UnknownNodeFilter filter) {
    this.apiCheck.setUnknownNodeFilter(filter);
    return this;
  }

  /**
   * 根据Param定义对data数据做合法性验证
   * 
   * @param jsonNode
   * @return
   */
  public JsonValidate check(JsonNode jsonNode) {
    apiCheck.check(jsonNode);
    return this;
  }

  /**
   * 根据Param定义对data数据做合法性验证
   * 
   * @param data
   * @return
   */
  public JsonValidate check(Object data) {
    String json = JsonUtils.stringify(data);
    JsonNode jsonNode = JsonUtils.parser(json);
    apiCheck.check(jsonNode);
    return this;
  }

  /**
   * 根据Param定义提取合法数据
   * 
   * @param jsonNode
   * @return
   */
  public Map<String, Object> extract(JsonNode jsonNode) {
    return apiCheck.extract(jsonNode);
  }

}
