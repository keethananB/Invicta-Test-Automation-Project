package com.ii.testautomation.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
public class ScheduleResponse {
    private Long id;
    private String name;
    private Long testGroupingId;
    private String testGroupingName;
    private Map<Integer, Long> testScenario;
    private Map<Integer, Long> testCase;
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
