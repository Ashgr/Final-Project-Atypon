package com.atypon.nosql.utils;

public class Hash {
  public static String hashAll(String schema) {
    return schema;
  }

  public static String hashRecord(String schema, String id) {
    return schema + "." + id;
  }
}
