package com.ii.testautomation.service;

import com.ii.testautomation.dto.request.EmailRequest;
import com.ii.testautomation.dto.response.ExecutionHistoryResponse;
import javax.mail.MessagingException;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

public interface ExecutionHistoryService {

    boolean existByTestGropingId(Long id);

    List<ExecutionHistoryResponse> viewByTestGroupingId(Long id);

    String viewReportByExecutionHistoryId(Long id) throws IOException;

    String viewReportWithLastUpdateByExecutionHistoryId(Long id) throws IOException;

    boolean existByExecutionHistoryId(Long id);

    boolean deleteExecutionHistory(Long id, Long projectId);

    List<ExecutionHistoryResponse> executionHistoryDateFilter(Long id, Timestamp startDate, Timestamp endDate);

    void emailHistoryReports(EmailRequest emailRequest) throws IOException, MessagingException;
}
