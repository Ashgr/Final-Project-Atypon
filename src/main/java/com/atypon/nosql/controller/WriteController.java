package com.atypon.nosql.controller;

import com.atypon.nosql.auth.AES;
import com.atypon.nosql.auth.TokenAuth;
import com.atypon.nosql.cache.LFUCache;
import com.atypon.nosql.data.DAO;
import com.atypon.nosql.data.SchemaDAO;
import com.atypon.nosql.middleware.AdminAuth;
import com.atypon.nosql.middleware.AuthenticationLogin;
import com.atypon.nosql.record.Attribute;
import com.atypon.nosql.record.NullRecord;
import com.atypon.nosql.record.Record;
import com.atypon.nosql.schema.SchemaBuilder;
import com.atypon.nosql.utils.Hash;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class WriteController {
    private static Attribute getAttribute(Object key, Map<String, Object> body) {
        if (body.get(key) instanceof List) {
            ArrayList<Attribute> nestedAttribute = new ArrayList<>();
            for (Object data : (ArrayList<?>) body.get(key)) {
                LinkedHashMap<String, Object> smallAtt = (LinkedHashMap<String, Object>) data;
                Map.Entry<String, Object> entry = smallAtt.entrySet().iterator().next();
                Attribute currentAtt = new Attribute(entry.getKey(), entry.getValue());
                nestedAttribute.add(currentAtt);
            }
            Attribute attribute = new Attribute(key.toString(), nestedAttribute);
            return attribute;
        } else {
            Attribute attribute = new Attribute(key.toString(), body.get(key));
            return attribute;
        }
    }

    @PostMapping(
            value = "/{schema}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Object add(
            @PathVariable("schema") String schema,
            @RequestBody Map<String, Object> body,
            @RequestHeader Map<String, String> headers)
            throws IOException, ParseException {
        List<Attribute> attributes = new ArrayList<>();
        String authToken = headers.get("authorization");
        if (!AuthenticationLogin.isLoggedIn(authToken)) {
            return new ResponseEntity("Login first ", HttpStatus.FORBIDDEN);
        }
        for (Object key : body.keySet()) {
            Attribute attribute = getAttribute(key, body);
            attributes.add(attribute);
        }
        DAO dao = SchemaDAO.getInstance(schema);
        Record record = new Record(attributes);
        if (dao.add(record)) {
            return record.toJson().toJSONString();
        } else {
            return ResponseEntity.badRequest();
        }
    }

    @PostMapping("/schema/{schema}")
    public Object createSchema(
            @RequestBody String body,
            @PathVariable("schema") String schema,
            @RequestHeader Map<String, String> headers)
            throws IOException, ParseException {
        String authToken = headers.get("authorization");
        if (authToken.length() <= 0) return ResponseEntity.status(HttpStatus.FORBIDDEN);
        String decrypted = AES.decrypt(authToken, "nosecret");
        if (decrypted == null) return ResponseEntity.status(HttpStatus.FORBIDDEN);
        if (AdminAuth.checkAuth(decrypted)) return ResponseEntity.status(HttpStatus.FORBIDDEN);
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(body);
        if (SchemaBuilder.createSchema(schema, json)) {
            return json;
        } else {
            return ResponseEntity.badRequest();
        }
    }

    @PostMapping("/login")
    public Object login(@RequestBody Map<String, Object> body) throws IOException, ParseException {
        String username = (String) body.get("username");
        String password = (String) body.get("password");
        Attribute attribute = new Attribute("username", username);
        String hash = Hash.hashRecord("users", attribute.getKey());
        LFUCache.getInstance().remove(hash);
        SchemaDAO schemaDAO = (SchemaDAO) SchemaDAO.getInstance("users");
        Object user = schemaDAO.getByAttribute(attribute);
        if (user instanceof NullRecord)
            return new ResponseEntity("invalid credentials ", HttpStatus.FORBIDDEN);
        ArrayList<Record> recordArrayList = (ArrayList<Record>) user;
        if (recordArrayList.size() > 0) {
            TokenAuth tokenAuth = TokenAuth.getInstance();
            Record record = recordArrayList.get(0);
            return tokenAuth.generate(record.getRecordID());
        } else {
            return new ResponseEntity("invalid credentials ", HttpStatus.FORBIDDEN);
        }
    }
}
