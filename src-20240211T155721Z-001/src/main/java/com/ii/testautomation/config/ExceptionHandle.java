package com.ii.testautomation.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Header;

public class ExceptionHandle extends ExpiredJwtException{

  public ExceptionHandle(Header header, Claims claims, String message) {
    super(header, claims, message);
  }

  public ExceptionHandle(Header header, Claims claims, String message, Throwable cause) {
    super(header, claims, message, cause);
  }
}
