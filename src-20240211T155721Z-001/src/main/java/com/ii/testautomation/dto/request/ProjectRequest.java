package com.ii.testautomation.dto.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProjectRequest {
  private Long id;
  private String name;
  private String description;
  private String code;
  private Long companyUserId;
}

