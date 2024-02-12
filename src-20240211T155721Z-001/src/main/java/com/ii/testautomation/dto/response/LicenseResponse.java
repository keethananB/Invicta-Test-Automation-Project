package com.ii.testautomation.dto.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LicenseResponse {
    private Long id;
    private String name;
    private Long duration;
    private Long noOfProjects;
    private Long noOfUsers;
    private Double price;
}
