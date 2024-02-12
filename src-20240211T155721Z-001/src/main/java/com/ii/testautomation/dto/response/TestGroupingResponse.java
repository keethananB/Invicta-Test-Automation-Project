package com.ii.testautomation.dto.response;

import com.ii.testautomation.entities.TestScenarios;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class TestGroupingResponse {
    private Long id;
    private String name;
    private List<Long> testCaseIds;
    private List<String> testCaseName;
    private Long testTypeId;
    private String testTypeName;
    private List<Long> testScenarioIds;
    private List<String> testScenarioName;
    private List<String> testCaseNames;
    private List<String> subModuleName;
    private List<String> mainModuleName;
    private List<String> moduleName;
    private List<String> excelFile;
    private List<TestScenariosResponse> testScenariosResponseList;
    private List<TestCaseResponse> testCaseResponseList;
}
