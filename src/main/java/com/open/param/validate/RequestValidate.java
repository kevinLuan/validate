package com.open.param.validate;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import com.open.param.Param;
import com.open.param.core.ApiCheck;
import com.open.param.core.ApiParams;
import com.open.param.core.UnknownNodeFilter;

public class RequestValidate {
  final ApiCheck<HttpServletRequest> apiCheck;

  public RequestValidate(Param... params) {
    this.apiCheck = ApiParams.make(params);
  }

  public static RequestValidate of(Param... params) {
    return new RequestValidate(params);
  }

  /**
   * 设置未知Node节点过滤器
   */
  public RequestValidate setUnknownNodeFilter(UnknownNodeFilter filter) {
    this.apiCheck.setUnknownNodeFilter(filter);
    return this;
  }

  /**
   * 参数合法性验证
   * 
   * @param request
   * @return
   */
  public RequestValidate check(HttpServletRequest request) {
    apiCheck.check(request);
    return this;
  }

  /**
   * 根据参数Param定义提取数据
   * 
   * @param request
   * @return
   */
  public Map<String, Object> extract(HttpServletRequest request) {
    return apiCheck.extract(request);
  }
}
