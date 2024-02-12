package com.ii.testautomation.entities;

import com.ii.testautomation.utils.DateAudit;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
import java.time.LocalDateTime;
@Entity
@Getter
@Setter
public class Scheduling extends DateAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "testCases_id", nullable = true)
    private List<TestCases> testCases;
    @ManyToMany
    @JoinColumn(name = "testScenarios_id", nullable = true)
    private List<TestScenarios> testScenarios;
    @ManyToOne
    @JoinColumn(name = "testGrouping_id", nullable = false)
    private TestGrouping testGrouping;
    @ElementCollection
    private List<Long> testCasesIds;
    private boolean status = true;
    @Column(unique = true)
    private String schedulingCode;
    private LocalDateTime startDateTime;
    private int year;
    private int hour;
    private int noOfTimes;
    private int month;
    private int week;
    private int minutes;
    private LocalDateTime nextExecutionTime;
    private int count=0;

}



