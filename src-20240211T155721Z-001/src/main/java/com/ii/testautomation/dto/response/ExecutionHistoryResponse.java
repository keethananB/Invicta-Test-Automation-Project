package com.ii.testautomation.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExecutionHistoryResponse {
    private Long id;
    private Long testGroupingId;
    private String reportName;
}
