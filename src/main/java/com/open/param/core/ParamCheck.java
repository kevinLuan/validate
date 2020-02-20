package com.open.param.core;

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
public class ParamCheck extends ApiBase<JsonNode> {

  public Param param;
  private CheckParam checkParam = CheckParam.getInstance(this);

  private ParamCheck(Param param) {
    this.param = param;
  }

  @Override
  public ParamCheck setReference() {
    param = param.optimize();
    return this;
  }

  /**
   * 根据API返回协议格式定义进行合法性验证
   */
  @Override
  public ParamCheck check(JsonNode jsonNode) {
    checkParam.checkResponse(param, jsonNode);
    return this;
  }

  /**
   * 根据返回数据格式定义extract数据
   */
  @Override
  public Map<String, Object> extract(JsonNode jsonNode) {
    if (param == null) {
      throw new IllegalArgumentException("param must be not null");
    }
    object(jsonNode, param);
    Iterator<String> iterator = jsonNode.fieldNames();
    Map<String, Object> data = new HashMap<>(param.asObject().getChildren().length);
    while (iterator.hasNext()) {
      String key = iterator.next();
      data.put(key, jsonNode.get(key));
    }
    return data;
  }

  public static ApiCheck<JsonNode> make(Param param) {
    return new ParamCheck(param).setReference();
  }

  @Override
  public String getTipError(String path) {
    return "`" + path + "`数据缺失";
  }

  @Override
  public String getTipMissing(String path) {
    return "`" + path + "`数据缺失";
  }
}
