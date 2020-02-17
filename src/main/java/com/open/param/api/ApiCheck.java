package com.open.param.api;

import java.util.Map;

/**
 * API 传输协议检测
 * 
 * @author KEVIN LUAN
 * @param <P>
 */
public interface ApiCheck<P> {

  /**
   * 传输数据合法性验证
   * 
   * @param p
   * @return
   */
  public ApiCheck<P> check(P p);

  /**
   * 根据传输数据定义提取数据明细
   * 
   * @return
   */
  public Map<String, Object> extract(P p);

  /**
   * 设置未知Node节点过滤器
   * 
   * @param filter
   */
  public void setUnknownNodeFilter(UnknownNodeFilter filter);
}
