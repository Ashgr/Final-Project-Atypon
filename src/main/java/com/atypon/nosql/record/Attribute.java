package com.atypon.nosql.record;

import java.util.Objects;

public class Attribute {
  private String attributeKey;
  private Object attributeValue;

  public Attribute(String key, Object value) {
    this.attributeKey = key;
    this.attributeValue = value;
  }

  public String getKey() {
    return this.attributeKey;
  }

  public void setKey(String key) {
    this.attributeKey = key;
  }

  public Object getValue() {
    return this.attributeValue;
  }

  public void setValue(Object value) {
    this.attributeValue = value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Attribute attribute = (Attribute) o;
    return Objects.equals(attributeKey, attribute.attributeKey)
        && Objects.equals(attributeValue, attribute.attributeValue);
  }

  @Override
  public int hashCode() {
    return Objects.hash(attributeKey, attributeValue);
  }
}
