package com.ii.testautomation.dto.search;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
@Getter
@Setter
public class CompanyUserSearch {
    private String companyName;
    private String firstName;
    private String lastName;
    private String email;
    private String contactNumber;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    private String licenseName;
    private Long licenseDuration;
    private Long noOfProjects;
    private Long noOfUsers;
    private Double price;
}
