package com.ii.testautomation.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CompanyUserRequest {
    private Long id;
    private Long licenses_id;
    private String companyName;
    private String firstName;
    private String lastName;
    private String email;
    private String contactNumber;
    private Boolean status;
    private LocalDate startDate;
}
