package com.ii.testautomation.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EmailRequest {
    private List<Long> historyReportIds;
    private List<String> toEmails;
    private String subject;
    private String message;
}
