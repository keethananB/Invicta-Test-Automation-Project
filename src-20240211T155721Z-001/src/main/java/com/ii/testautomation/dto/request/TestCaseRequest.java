package com.ii.testautomation.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class TestCaseRequest {
    private Long id;
    private String name;
    private String description;
    private String subModuleName;
    private Long subModuleId;
    private Long project_id;
    private List<Long> testCaseIds;
}
