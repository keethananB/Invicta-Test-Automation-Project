package com.ii.testautomation.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponse {
    private Long id;
    private String email;
    private String password;
    private String status;
    private String firstName;
    private String lastName;
    private String contactNumber;
    private Long designationId;
    private String designationName;
    private Long companyUserId;
    private String companyUserName;
    private boolean isActive;
}
