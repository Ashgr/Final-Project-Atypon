package com.atypon.nosql.auth;

public class TokenAuth implements Authentication {
    private static TokenAuth tokenAuth;

    private TokenAuth() {}

    public static TokenAuth getInstance() {
        if (tokenAuth == null) tokenAuth = new TokenAuth();
        return tokenAuth;
    }

    @Override
    public Object verify(String token, String secretKey) {
        return AES.decrypt(token, secretKey);
    }

    @Override
    public String generate(Object payload) {
        return AES.encrypt(payload.toString(), "nosecret");
    }
}
