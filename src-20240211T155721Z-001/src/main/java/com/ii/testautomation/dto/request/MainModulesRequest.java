package com.ii.testautomation.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MainModulesRequest
{
    private Long moduleId;
    private Long id;
    private String name;
    private String prefix;
}
