package com.atypon.nosql.controller;

import com.atypon.nosql.data.DAO;
import com.atypon.nosql.data.SchemaDAO;
import com.atypon.nosql.middleware.AuthenticationLogin;
import com.atypon.nosql.record.Attribute;
import com.atypon.nosql.record.Record;
import com.atypon.nosql.utils.ResourcesPath;
import org.json.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class ReadController {
    @GetMapping("/ping")
    public String ping() throws UnknownHostException {
        return (InetAddress.getLocalHost().getHostName());
    }

    @GetMapping("/{schema}")
    public Object getAll(
            @PathVariable("schema") String schema, @RequestHeader Map<String, String> headers)
            throws IOException, ParseException {
        String authToken = headers.get("authorization");
        if (!AuthenticationLogin.isLoggedIn(authToken)) {
            return new ResponseEntity("Login first ", HttpStatus.FORBIDDEN);
        }
        DAO dao = SchemaDAO.getInstance(schema);
        JSONArray jsonArray = (JSONArray) dao.getAll();
        return jsonArray.toString();
    }

    @GetMapping("/{schema}/attribute")
    public Object getByAttribute(
            @PathVariable("schema") String schema,
            @RequestParam Map<String, String> params,
            @RequestHeader Map<String, String> headers)
            throws IOException, ParseException {
        String authToken = headers.get("authorization");
        if (!AuthenticationLogin.isLoggedIn(authToken)) {
            return new ResponseEntity("Login first ", HttpStatus.FORBIDDEN);
        }
        DAO dao = SchemaDAO.getInstance(schema);
        String key = null;
        for (String currentKey : params.keySet()) {
            key = currentKey;
        }
        String value = params.get(key);
        Attribute attribute = new Attribute(key, value);
        ArrayList<Record> records = (ArrayList<Record>) dao.getByAttribute(attribute);
        JSONArray jsonArray = new JSONArray();
        for (Record record : records) {
            jsonArray.put(record.toJson());
        }
        return jsonArray.toString();
    }

    @GetMapping("/schemas")
    public Object getAllSchemas(@RequestHeader Map<String, String> headers)
            throws IOException, ParseException {
        String authToken = headers.get("authorization");
        if (!AuthenticationLogin.isLoggedIn(authToken)) {
            return new ResponseEntity("Login first ", HttpStatus.FORBIDDEN);
        }
        File directory =
                new File(ResourcesPath.getDevelopmentSchemaResource()); // current directory
        File[] files = directory.listFiles();
        JSONArray jsonArray = new JSONArray();
        JSONParser jsonParser = new JSONParser();
        for (File file : files) {
            JSONObject schema = new JSONObject();
            JSONObject data = (JSONObject) jsonParser.parse(new FileReader(file.getAbsolutePath()));
            schema.put(file.getName().substring(0, file.getName().indexOf(".")), data);
            jsonArray.put(schema);
        }
        return jsonArray.toString();
    }
}
