package com.ii.testautomation.entities;

import com.ii.testautomation.utils.DateAudit;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
public class Users extends DateAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String email;
    private String password;
    private String status;
    @Column(nullable = false)
    private String firstName;
    private String lastName;
    private String uniqueIdentification;
    @Column(nullable = false)
    private String contactNumber;
    private int wrongCount = 5;
    @ManyToOne
    @JoinColumn(name = "designation_id", nullable = false)
    private Designation designation;
    @ManyToOne
    @JoinColumn(name = "company_user_id", nullable = false)
    private CompanyUser companyUser;
}
