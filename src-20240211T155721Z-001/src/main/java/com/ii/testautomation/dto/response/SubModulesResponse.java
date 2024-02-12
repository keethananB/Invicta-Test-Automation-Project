package com.ii.testautomation.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SubModulesResponse {
    private Long id;
    private String name;
    private String prefix;
    private String mainModuleName;
    private Long mainModuleId;
    private String moduleName;
    private Long moduleId;
    List<TestCaseResponse> testCaseResponses;
}