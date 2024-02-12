package com.ii.testautomation.entities;

import com.ii.testautomation.utils.DateAudit;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class TestCases extends DateAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(length = 1500)
    private String description;
    @ManyToOne
    @JoinColumn(name = "submodule_id",nullable = false)
    private SubModules subModule;
    private Boolean executionStatus = false;
}