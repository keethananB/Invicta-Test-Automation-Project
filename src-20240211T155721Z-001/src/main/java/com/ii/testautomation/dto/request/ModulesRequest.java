package com.ii.testautomation.dto.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ModulesRequest {
    private Long id;
    private String name;
    private String prefix;
    private Long project_id;
}
