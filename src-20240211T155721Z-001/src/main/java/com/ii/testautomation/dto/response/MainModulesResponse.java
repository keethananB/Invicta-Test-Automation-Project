package com.ii.testautomation.dto.response;
import com.ii.testautomation.entities.Modules;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MainModulesResponse
{
    private Long id;
    private String name;
    private String prefix;
    private String modulesName;
    private Long moduleId;
    private List<SubModulesResponse> subModulesResponses;
}
