package com.atypon.nosql.controller;

import com.atypon.nosql.data.DAO;
import com.atypon.nosql.data.SchemaDAO;
import com.atypon.nosql.middleware.AuthenticationLogin;
import com.atypon.nosql.record.Attribute;
import com.atypon.nosql.record.NullRecord;
import com.atypon.nosql.record.Record;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class UpdateController {
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

    @PutMapping("/{schema}/{id}")
    public Object update(
            @RequestBody Map<String, Object> body,
            @RequestHeader Map<String, String> headers,
            @PathVariable("schema") String schema,
            @PathVariable("id") String id)
            throws IOException, ParseException {
        String authToken = headers.get("authorization");
        if (!AuthenticationLogin.isLoggedIn(authToken)) {
            return new ResponseEntity("Login first ", HttpStatus.FORBIDDEN);
        }
        DAO dao = SchemaDAO.getInstance(schema);
        Attribute idAttribute = new Attribute("_id", id);
        if (dao.getByAttribute(idAttribute) instanceof NullRecord) {
            return new ResponseEntity("user not found", HttpStatus.NOT_FOUND);
        }
        List<Attribute> attributes = new ArrayList<>();
        for (Object key : body.keySet()) {
            Attribute attribute = getAttribute(key, body);
            attributes.add(attribute);
        }
        attributes.add(idAttribute);
        Record record = new Record(attributes);
        return dao.update(record).toJson();
    }
}
