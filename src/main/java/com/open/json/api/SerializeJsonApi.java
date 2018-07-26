package com.open.json.api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 序列化JSON API
 *
 * @author KEVIN LUAN
 *
 */
public interface SerializeJsonApi extends SerializeApi<String> {
  public static final Logger LOGGER = LoggerFactory.getLogger(SerializeApi.class);

  /**
   * 序列化
   */
  public String encode(Object t);

  /**
   * 反序列化
   */
  public <T> T decode(String json, Class<T> type);

  public SerializeJsonApi getInstance();
}
