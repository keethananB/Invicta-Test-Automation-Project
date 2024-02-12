package com.ii.testautomation.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TestGroupingRequest {
    private Long id;
    private String name;
    private Long testTypeId;
    private List<Long> testCaseId;
    private List<Long> testScenarioIds;
    private List<Long> moduleIds;
    private List<Long> subModuleIds;
    private List<Long> mainModuleIds;
    private Long projectId;
}
