package com.atypon.nosql.record;

import org.json.simple.JSONObject;

public interface RecordBuilder {

  public JSONObject toJson();

  public boolean isNull();
}
