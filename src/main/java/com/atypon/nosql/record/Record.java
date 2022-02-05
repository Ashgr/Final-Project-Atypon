package com.atypon.nosql.record;

import com.atypon.nosql.data.RecordProxy;
import org.json.JSONArray;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.List;

public class Record implements RecordBuilder {
  protected List<Attribute> data;
  private String recordID = "", createdAt;

  public Record(List<Attribute> attributeList) {
    this.data = attributeList;
    this.recordID = RecordProxy.validateUUID(attributeList);
    this.createdAt = RecordProxy.validateTime(attributeList);
  }

  public List<Attribute> getAttributes() {
    return this.data;
  }

  public String getRecordID() {
    return this.recordID;
  }

  public String getCreatedAt() {
    return this.createdAt;
  }

  @Override
  public JSONObject toJson() {
    JSONObject jsonObject = new JSONObject();
    for (Attribute attribute : data) {
      if (attribute.getValue() instanceof List) {
        List<Attribute> nestedAttribute = (List<Attribute>) attribute.getValue();
        JSONArray jsonArray = new JSONArray();
        if (nestedAttribute.size() > 0 && nestedAttribute.get(0) instanceof Attribute) {
          for (Attribute attribute1 : nestedAttribute) {
            JSONObject tempJson = new JSONObject();
            tempJson.put(attribute1.getKey(), attribute1.getValue());
            jsonArray.put(tempJson);
          }
          jsonObject.put(attribute.getKey(), jsonArray);
        } else jsonObject.put(attribute.getKey(), attribute.getValue());
      } else jsonObject.put(attribute.getKey(), attribute.getValue());
    }
    jsonObject.put("createdAt", createdAt);
    jsonObject.put("_id", recordID);
    HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
    return jsonObject;
  }

  @Override
  public boolean isNull() {
    return false;
  }
}
