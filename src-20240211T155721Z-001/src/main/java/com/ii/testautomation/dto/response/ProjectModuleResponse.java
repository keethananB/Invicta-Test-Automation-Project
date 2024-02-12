package com.ii.testautomation.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProjectModuleResponse {
    List<ModulesResponse> modulesResponseList;
}
