package com.ii.testautomation.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserRequest {
    private Long id;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String contactNumber;
    private Long companyUserId;
    private String companyUserName;
    private Long designationId;
    private Boolean status;
}
