package com.ii.testautomation.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LicenseRequest {
    private Long id;
    private String name;
    private Long duration;
    private Long noOfProjects;
    private Long noOfUsers;
    private Double price;
}
