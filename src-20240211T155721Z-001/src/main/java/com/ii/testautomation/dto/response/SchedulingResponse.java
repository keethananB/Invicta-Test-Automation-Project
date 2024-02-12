package com.ii.testautomation.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class SchedulingResponse {
    private Long id;
    private String name;
    private Long testGroupingId;
    private String testGroupingName;
    private List<Long> testCasesIds;
    private List<String> testCasesNames;
    private List<Long> testScenarioIds;
    private List<String> testScenarioNames;
    private LocalDateTime nextExecutionTime;
    private String schedulingCode;
    private LocalDateTime startDateTime;
    private int year;
    private int hour;
    private int noOfTimes;
    private int month;
    private int week;
    private int minutes;
    private boolean status;

}
