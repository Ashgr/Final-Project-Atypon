package com.atypon.nosql.middleware;

import com.atypon.nosql.cache.LFUCache;
import com.atypon.nosql.data.SchemaDAO;
import com.atypon.nosql.record.Attribute;
import com.atypon.nosql.record.Record;
import com.atypon.nosql.utils.Hash;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;

public class AdminAuth {
  public static boolean checkAuth(String decrypted) throws IOException, ParseException {
    SchemaDAO schemaDAO = (SchemaDAO) SchemaDAO.getInstance("users");
    Attribute idAttribute = new Attribute("_id", decrypted);
    String hash = Hash.hashRecord("users", idAttribute.getKey());
    LFUCache.getInstance().remove(hash);
    ArrayList<Record> user = (ArrayList) schemaDAO.getByAttribute(idAttribute);
    Record record = user.get(0);
    for (Attribute attribute : record.getAttributes()) {
      if (attribute.getKey().equals("role") && attribute.getValue().equals("admin")) {
        return true;
      }
    }
    return false;
  }
}
