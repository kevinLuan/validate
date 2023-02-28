package io.github.validate.domain.api;

import java.io.Serializable;
import com.google.gson.Gson;
abstract class Result implements Serializable {

  private static final long serialVersionUID = 5471203693615077525L;

  public abstract Status getStatus();

  private static final Gson gson = new Gson();

  public String toJSON() {
    return gson.toJson(this);
  }
}
