package com.ii.testautomation.response.common;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
@ControllerAdvice
public class ExceptionHandling {

    @ExceptionHandler
    public ResponseEntity handleException(Exception e){
        return ResponseEntity.ok("Hello This is wrong");
    }


}

