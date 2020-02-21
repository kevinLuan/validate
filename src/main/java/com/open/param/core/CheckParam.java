package com.open.param.core;

import com.open.param.common.NotSupportException;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
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
      param.asPrimitive().asserValue(jsonNode);
    } else if (param.isArray()) {
      check_array(param, jsonNode);
    } else if (param.isObject()) {
      check_object(param, jsonNode);
    } else if (param.isAny()) {
      param.asAny().asserValue(jsonNode);
    } else {
      throw NotSupportException.of("不支持的类型:" + param);
    }
  }

  public void checkParams(HttpServletRequest request, Param... params) {
    if (request == null) {
      throw new IllegalArgumentException("request must be not null");
    }
    for (int i = 0; i < params.length; i++) {
      Param param = params[i];
      String name = param.getName();
      if (StringUtils.isBlank(name)) {
        throw new IllegalArgumentException("定义参数params[" + i + "].name 不能为空");
      }
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
        TextNode node = JsonNodeFactory.instance.textNode(value);
        param.asPrimitive().asserValue(node);
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
        }else if(param.isAny()){
          param.asAny().asserValue(jsonNode);
        } else {
          throw NotSupportException.of("不支持的类型:" + param);
        }
      }
    }
  }

  void check_array(Param param, JsonNode value) {
    if (param.isArray() && value.isArray()) {
      ParamArray array = param.asArray();
      if (array.existsChildrens()) {
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
            children.asPrimitive().asserValue(node);
          } else if (children.isAny()) {
            children.asAny().asserValue(node);
          } else {
            throw NotSupportException.of("不支持的类型" + children);
          }
        }
      }
      // 用户自定义的验证
      param.asArray().asserValue(value);
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
        p.asPrimitive().asserValue(value);
      } else if (p.isAny()) {
        p.asAny().asserValue(value);
      } else {
        throw NotSupportException.of("不支持的类型" + p);
      }
    }
    // 用户自定义的验证
    param.asObject().asserValue(objNode);
  }
}
