package com.ii.testautomation.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Setter
@Getter
public class ProgressBar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;
    @ManyToOne
    @JoinColumn(name = "testGrouping_id", nullable = true)
    private TestGrouping testGrouping;
    private Long totalNoOfTestCases;
    private Long executedTestCaseCount;
}
