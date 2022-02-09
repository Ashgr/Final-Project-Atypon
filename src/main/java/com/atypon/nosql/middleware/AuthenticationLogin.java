package com.atypon.nosql.middleware;

import com.atypon.nosql.auth.TokenAuth;
import com.atypon.nosql.data.SchemaDAO;
import com.atypon.nosql.record.AbstractRecord;
import com.atypon.nosql.record.Attribute;
import com.atypon.nosql.record.NullRecord;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.List;

public class AuthenticationLogin {
    private static boolean checkTokenUser(String userId) throws IOException, ParseException {
        SchemaDAO dao = (SchemaDAO) SchemaDAO.getInstance("users");
        Attribute attribute = new Attribute("_id", userId);
        Object result = dao.getByAttribute(attribute);
        if (result instanceof NullRecord) {
            return false;
        }
        List<AbstractRecord> records = (List<AbstractRecord>) result;
        return records.size() > 0;
    }

    public static boolean isLoggedIn(String token) throws IOException, ParseException {
        if (token == null) return false;
        if (token.length() <= 0) return false;
        TokenAuth tokenAuth = TokenAuth.getInstance();
        String decrypted = (String) tokenAuth.verify(token, "nosecret");
        if (decrypted == null) return false;
        return checkTokenUser(decrypted);
    }
}
