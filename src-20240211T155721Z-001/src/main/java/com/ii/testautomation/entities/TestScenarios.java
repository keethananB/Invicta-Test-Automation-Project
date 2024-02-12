package com.ii.testautomation.entities;

import com.ii.testautomation.utils.DateAudit;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
public class TestScenarios extends DateAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @ManyToMany
    @JoinColumn(name = "testCases_id", nullable = false)
    private List<TestCases> testCases;
    private Boolean executionStatus = false;
    @ElementCollection
    private List<String> excelPath;

}






