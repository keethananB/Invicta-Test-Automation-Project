package com.ii.testautomation.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestTypesResponse {
    private Long id;
    private String name;
    private String description;
    private Long CompanyUserId;
}
