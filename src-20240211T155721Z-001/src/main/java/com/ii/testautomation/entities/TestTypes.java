package com.ii.testautomation.entities;

import com.ii.testautomation.utils.DateAudit;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class TestTypes extends DateAudit
{   @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(length = 1500)
    private String description;
    @ManyToOne
    @JoinColumn(name = "company_user_id", nullable = false)
    private CompanyUser companyUser;
}