package com.atypon.nosql.auth;

public interface Authentication {
  public Object verify(String token, String secretKey);

  public String generate(Object payload);
}
