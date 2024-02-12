package com.ii.testautomation.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SchedulingGroupingTestCases {
    private Long testCaseId;
    private String testCaseName;
    private Long schedulingId;
    private Long groupId;
}
