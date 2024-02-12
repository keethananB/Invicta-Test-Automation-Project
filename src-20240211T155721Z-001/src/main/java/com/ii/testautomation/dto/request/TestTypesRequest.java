package com.ii.testautomation.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestTypesRequest
{
    private Long id;
    private String name;
    private String description;
    private Long companyUserId;
}
