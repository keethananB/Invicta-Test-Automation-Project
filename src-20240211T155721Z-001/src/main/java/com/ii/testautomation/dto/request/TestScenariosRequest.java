package com.ii.testautomation.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TestScenariosRequest {
    private Long id;
    private String name;
    private List<Long> testCasesId;
    private List<Long> moduleIds;
    private List<Long> subModuleIds;
    private List<Long> mainModuleIds;
    private Long projectId;
}
