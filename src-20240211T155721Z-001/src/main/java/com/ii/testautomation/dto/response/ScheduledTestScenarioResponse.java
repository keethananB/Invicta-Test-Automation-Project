package com.ii.testautomation.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class ScheduledTestScenarioResponse {
    private Long testScenarioId;
    private String testScenarioName;
    private Map<Long, String> testCases;
    private Long schedulingId;
    private Long groupId;
}
