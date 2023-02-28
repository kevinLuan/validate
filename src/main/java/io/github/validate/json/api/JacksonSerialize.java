package io.github.validate.json.api;

import java.io.IOException;
import java.util.TimeZone;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class JacksonSerialize implements SerializeJsonApi {
  public static final ObjectMapper mapper = new ObjectMapper();
  static {
     mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
     mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
     // mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
     // mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
     SimpleModule module = new SimpleModule();
//     module.addDeserializer(Date.class, new DateDeserializer());
     mapper.registerModule(module);
     // 解决jackson序列化时默认的时区是UTC导致Date类型慢8小时问题
     mapper.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
  }
  public static JacksonSerialize INSTANCE = new JacksonSerialize();

  public String encode(Object obj) {
    try {
      return mapper.writeValueAsString(obj);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("encode(" + obj + ")error",e);
    }
  }

  @Override
  public <T> T decode(String json, Class<T> type) {
    try {
      return mapper.readValue(json, type);
    } catch (IOException e) {
      throw new RuntimeException("decode(" + json + ")error",e);
    }
  }

  @Override
  public SerializeJsonApi getInstance() {
    return INSTANCE;
  }
}
