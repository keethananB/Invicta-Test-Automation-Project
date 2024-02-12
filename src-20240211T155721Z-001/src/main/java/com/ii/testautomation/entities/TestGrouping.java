package com.ii.testautomation.entities;

import com.ii.testautomation.utils.DateAudit;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
public class TestGrouping extends DateAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @ManyToOne
    @JoinColumn(name = "testType_id", nullable = false)
    private TestTypes testType;
    @ManyToMany
    @JoinColumn(name = "testCases_id", nullable = true)
    private List<TestCases> testCases;
    @ManyToMany
    @JoinColumn(name = "testScenarios_id", nullable = true)
    private List<TestScenarios> testScenarios;
    private Boolean executionStatus = false;
    @ElementCollection
    private List<String> excelFilePath;
    private String groupPath;
    @ManyToOne
    @JoinColumn(name = "project_id",nullable = false)
    private Project project;
    private Boolean schedulingExecutionStatus = false;
}