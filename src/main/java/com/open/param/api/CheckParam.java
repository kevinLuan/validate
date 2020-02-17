package com.open.param.api;

import javax.servlet.http.HttpServletRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.open.json.api.JsonUtils;
import com.open.param.Param;
import com.open.param.ParamArray;
import com.open.param.ParamObject;

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
      param.asPrimitive().parseRawValue(jsonNode);
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
        param.asPrimitive().parseRawValue(value);
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
      if (!array.existsChildrens()) {
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
          children.asPrimitive().parseRawValue(node);
        } else {
          throw new IllegalArgumentException("不支持的类型" + children);
        }
      }
    } else {
      throw new IllegalArgumentException(getTipError(param.getPath()));
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
        p.asPrimitive().parseRawValue(value);
      } else {
        throw new IllegalArgumentException("不支持的类型:" + p);
      }

    }
  }
}
