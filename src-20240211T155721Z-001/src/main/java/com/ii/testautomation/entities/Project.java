package com.ii.testautomation.entities;

import com.ii.testautomation.utils.DateAudit;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
public class Project extends DateAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(length = 1500)
    private String description;
    private String code;
    private String jarFilePath;
    private String configFilePath;
    private String projectPath;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id", nullable = false)
    private List<Users> users;
    @ManyToOne
    @JoinColumn(name = "company_user_id", nullable = false)
    private CompanyUser companyUser;
}