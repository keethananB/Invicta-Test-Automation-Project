package com.ii.testautomation.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class ExecutionRequest {
    private Long testGroupingId;
    private Long projectId;
    private Map<Integer, Long> testScenario;
    private Map<Integer, Long> testCase;
}
