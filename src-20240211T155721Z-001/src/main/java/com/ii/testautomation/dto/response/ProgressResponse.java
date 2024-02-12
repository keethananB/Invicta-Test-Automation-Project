package com.ii.testautomation.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProgressResponse {
    private int percentage;
    private String groupName;
    private String scheduleName;
    private Long groupId;
    private Long projectId;
}
