package com.atypon.nosql.record;

import org.json.simple.JSONObject;

public interface AbstractRecord {

  public JSONObject toJson();

  public boolean isNull();
}
