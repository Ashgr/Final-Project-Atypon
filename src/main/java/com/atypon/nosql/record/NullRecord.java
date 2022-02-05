package com.atypon.nosql.record;

import org.json.simple.JSONObject;

public class NullRecord implements RecordBuilder {
  @Override
  public JSONObject toJson() {
    return null;
  }

  @Override
  public boolean isNull() {
    return true;
  }

  public String getRecordID() {
    return "";
  }

  public Object getAttributes() {
    return null;
  }
}
