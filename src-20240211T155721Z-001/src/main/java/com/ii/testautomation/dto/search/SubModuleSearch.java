package com.ii.testautomation.dto.search;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class SubModuleSearch {
    private String name;
    private String prefix;
    private String mainModuleName;
    private String mainModulePrefix;
    private String moduleName;
    private String modulePrefix;
}
