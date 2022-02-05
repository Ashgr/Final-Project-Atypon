package com.atypon.nosql.schema;

import com.atypon.nosql.utils.ResourcesPath;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SchemaBuilder {
  public static boolean createSchema(String schemaName, JSONObject json) {
    try {
      File file = new File(ResourcesPath.getProductionSchemaResource() + schemaName + ".json");
      FileWriter fileWriter =
          new FileWriter(ResourcesPath.getProductionSchemaResource() + schemaName + ".json");
      if (!isValidTypes(json)) {
        return false;
      }
      if (file.createNewFile()) System.out.println("File created: " + file.getName());
      else System.out.println("File already exists.");
      fileWriter.write(json.toJSONString());
      fileWriter.close();
      return true;
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
  }

  private static boolean isValidTypes(JSONObject json) {
    for (Object key : json.values()) {
      boolean isCorrectType = false;
      for (SchemaDataTypes dataTypes : SchemaDataTypes.values()) {
        if (dataTypes.toString().equalsIgnoreCase(key.toString())) {
          isCorrectType = true;
        }
      }
      if (!isCorrectType) return false;
    }
    return true;
  }
}
