package com.ii.testautomation.dto.response;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import java.time.LocalDate;

@Getter
@Setter
public class CompanyUserResponse {
    private Long id;
    private String companyName;
    private String firstName;
    private String lastName;
    private String email;
    private String contactNumber;
    private Boolean status;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long licenseId;
    private String licenseName;
    private Long licenseDuration;
    private Long noOfProjects;
    private Long noOfUsers;
    private Double price;
}
