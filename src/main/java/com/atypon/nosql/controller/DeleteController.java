package com.atypon.nosql.controller;

import com.atypon.nosql.data.SchemaDAO;
import com.atypon.nosql.middleware.AuthenticationLogin;
import com.atypon.nosql.record.AbstractRecord;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class DeleteController {
    @DeleteMapping("/{schema}/{id}")
    public Object delete(
            @RequestHeader Map<String, String> headers,
            @PathVariable("schema") String schema,
            @PathVariable("id") String id)
            throws IOException, ParseException {
        String authToken = headers.get("authorization");
        if (!AuthenticationLogin.isLoggedIn(authToken)) {
            return new ResponseEntity("Login first ", HttpStatus.FORBIDDEN);
        }
        SchemaDAO schemaDAO = (SchemaDAO) SchemaDAO.getInstance(schema);
        AbstractRecord record = schemaDAO.delete(id);
        if (record.isNull()) {
            return new ResponseEntity("user not found", HttpStatus.NOT_FOUND);
        }
        return record.toJson();
    }
}
