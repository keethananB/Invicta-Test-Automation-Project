package com.ii.testautomation.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class TestCaseResponse {
    private Long id;
    private String name;
    private String description;
    private Long moduleId;
    private String moduleName;
    private Long subModuleId;
    private String subModuleName;
    private Long mainModuleId;
    private String mainModuleName;
    private Long projectId;
    private String projectName;
}
