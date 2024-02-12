package com.ii.testautomation.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DesignationRequest {
    private Long id;
    private String name;
    private Long companyUserId;
    private Long userId;
}
