package com.ii.testautomation.response.common;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BaseResponse
{
  private String status;
  private String statusCode;
  private String message;


  public BaseResponse(String status, String statusCode, String message) {
    this.status = status;
    this.statusCode = statusCode;
    this.message = message;


  }
}
