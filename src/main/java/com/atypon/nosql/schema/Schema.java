package com.atypon.nosql.schema;

import com.atypon.nosql.record.Attribute;
import com.atypon.nosql.record.Record;
import com.atypon.nosql.utils.ResourcesPath;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Schema {
  private JSONObject schema;

  private Schema(String schemaName) throws IOException, ParseException {
    this.schema = parseJson(schemaName);
  }

  public static Schema getInstance(String schemaName) throws IOException, ParseException {
    return new Schema(schemaName);
  }

  private JSONObject parseJson(String schemaName) throws IOException, ParseException {
    FileReader fileReader =
        new FileReader(ResourcesPath.getDevelopmentSchemaResource() + schemaName + ".json");
    JSONParser jsonParser = new JSONParser();
    Object json = jsonParser.parse(fileReader);
    return (JSONObject) json;
  }

  public Boolean isValidRecord(Record record) {
    ArrayList<Attribute> attributes = (ArrayList<Attribute>) record.getAttributes();
    for (Attribute attribute : attributes) {
      if (!schema.containsKey(attribute.getKey())) {
        return false;
      } else {
        String type = (String) schema.get(attribute.getKey());
        if (Objects.equals(type, "String")) {
          if (!(attribute.getValue() instanceof String)) return false;
        }
        if (Objects.equals(type, "Integer")) {
          if (!(attribute.getValue() instanceof Integer)) return false;
        }
        if (Objects.equals(type, "List")) {
          if (!(attribute.getValue() instanceof List)) return false;
        }
      }
    }
    return true;
  }
}
