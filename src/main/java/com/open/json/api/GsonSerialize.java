package com.open.json.api;

import com.google.gson.Gson;

public class GsonSerialize implements SerializeJsonApi {

  public static GsonSerialize INSTANCE = new GsonSerialize();

  private static Gson gson = new Gson();

  public String encode(Object t) {
    return gson.toJson(t);
  }

  public <T> T decode(String json, Class<T> clazz) {
    return (T) gson.fromJson(json, clazz);
  }

  @Override
  public SerializeJsonApi getInstance() {
    return INSTANCE;
  }
}
