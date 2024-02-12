package com.ii.testautomation.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubModulesRequest {
    private Long id;
    private String name;
    private String prefix;
    private Long main_module_Id;
}
