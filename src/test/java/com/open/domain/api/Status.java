package com.open.domain.api;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Status implements Serializable {

  private static final long serialVersionUID = -8847081762490398492L;
  @JsonProperty("statusCode")
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private int statusCode = 0;
  @JsonProperty("statusReason")
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String statusReason = "";

  public Status() {
  }

  public Status(int statusCode, String message) {
    this.statusCode = statusCode;
    this.statusReason = message;
  }

  @Override
  public String toString() {
    return "{\"statusCode\":" + statusCode + ",\"statusReason\":\"" + statusReason + "\"}";
  }

  public int getStatusCode() {
    return statusCode;
  }

  @SuppressWarnings("unused")
  private void setStatusCode(int statusCode) {
    this.statusCode = statusCode;
  }

  public String getStatusReason() {
    return statusReason;
  }

  public void setStatusReason(String statusReason) {
    this.statusReason = statusReason;
  }
}
