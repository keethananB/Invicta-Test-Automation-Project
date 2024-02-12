package com.ii.testautomation.dto.search;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LicensesSearch {
    private String name;
    private Long duration;
    private Long noOfProjects;
    private Long noOfUsers;
    private String price;

}
