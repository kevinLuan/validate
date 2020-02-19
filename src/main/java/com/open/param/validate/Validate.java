package com.open.param.validate;

import com.open.param.Param;

/**
 * 数据验证API
 * 
 * @author SHOUSHEN LUAN
 *
 * @param <T> 验证类型
 */
public interface Validate<P extends Param, T> {
  /**
   * 验证数据合法性
   * 
   * @param value
   */
  boolean test(P p, T value);

  /**
   * 验证自定义规则
   * 
   * @param p
   * @param value
   */
  void assertValue(P p, T value);
}
