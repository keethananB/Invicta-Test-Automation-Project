package com.ii.testautomation.controllers;

import com.ii.testautomation.dto.request.EmailRequest;
import com.ii.testautomation.enums.RequestStatus;
import com.ii.testautomation.response.common.BaseResponse;
import com.ii.testautomation.response.common.ContentResponse;
import com.ii.testautomation.service.ExecutionHistoryService;
import com.ii.testautomation.service.ProjectService;
import com.ii.testautomation.service.TestGroupingService;
import com.ii.testautomation.utils.Constants;
import com.ii.testautomation.utils.EndpointURI;
import com.ii.testautomation.utils.StatusCodeBundle;
import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Calendar;
import javax.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class ExecutionHistoryController {
    @Autowired
    private ExecutionHistoryService executionHistoryService;
    @Autowired
    private TestGroupingService testGroupingService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private StatusCodeBundle statusCodeBundle;

    @GetMapping(EndpointURI.EXECUTION_HISTORY_BY_TEST_GROUPING_ID)
    public ResponseEntity<Object> viewByTestGroupingId(@PathVariable Long id) {

        if (!testGroupingService.existsByTestGroupingId(id)) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(),
                    statusCodeBundle.getTestGroupingNotExistCode(),
                    statusCodeBundle.getTestGroupingNotExistsMessage()));
        }
        if (!executionHistoryService.existByTestGropingId(id)) {
            return ResponseEntity.ok(
                    new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(),
                            statusCodeBundle.getTestGroupingNotMappedMessage()));
        }
        return ResponseEntity.ok(new ContentResponse<>(Constants.EXECUTION_HISTORY,
                executionHistoryService.viewByTestGroupingId(id), RequestStatus.SUCCESS.getStatus(),
                statusCodeBundle.getCommonSuccessCode(),
                statusCodeBundle.getViewExecutionHistoryMessage()));
    }

    @GetMapping(EndpointURI.EXECUTION_HISTORY_ID)
    public ResponseEntity<String> viewReportByExecutionHistoryId(@PathVariable Long id) throws IOException {
        if (id == null) return ResponseEntity.ok(statusCodeBundle.getExecutionHistoryIdNull());
        if (!executionHistoryService.existByExecutionHistoryId(id))
            return ResponseEntity.ok(statusCodeBundle.getExecutionHistoryNotFound());
        return ResponseEntity.ok(executionHistoryService.viewReportByExecutionHistoryId(id));
    }

    @GetMapping(EndpointURI.EXECUTION_HISTORY_BY_DATE)
    public ResponseEntity<String> viewReportWithLastUpdateByExecutionHistoryId(@PathVariable Long id) throws IOException {
        if (id == null) {
            return ResponseEntity.ok(statusCodeBundle.getExecutionHistoryIdNull());
        }
        if (!executionHistoryService.existByExecutionHistoryId(id)) {
            return ResponseEntity.ok(statusCodeBundle.getExecutionHistoryNotFound());
        }
        String response = executionHistoryService.viewReportWithLastUpdateByExecutionHistoryId(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping(EndpointURI.EXECUTION_HISTORY_DATE_FILTER)
    public ResponseEntity<Object> executionHistoryDateFilter(@PathVariable Long id, @RequestParam(value = "startDate", required = false) String startDate, @RequestParam(value = "endDate", required = false) String endDate) throws ParseException {

        if (!executionHistoryService.existByTestGropingId(id))
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getTestGroupingNotMappedMessage()));

        Timestamp startingDate;
        Timestamp endingDate;

        if (startDate.isEmpty() || startDate.isBlank())
            startingDate = new Timestamp(Date.valueOf(LocalDate.now().withDayOfMonth(1)).getTime());
         else startingDate = new Timestamp(Date.valueOf(startDate).getTime());

         if (endDate.isEmpty() || endDate.isBlank()) {

            endingDate = new Timestamp(new Date(System.currentTimeMillis()).getTime());
        }
        else {
            if (startDate.isEmpty() || startDate.isBlank())
            return ResponseEntity.ok(new BaseResponse(RequestStatus.ERROR.getStatus(),statusCodeBundle.getExecutionHistoryDateErrorCode(),statusCodeBundle.getExecutionHistoryEndDateEmptyMessage()));
            endingDate = new Timestamp(Date.valueOf(endDate).getTime());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(endingDate);
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            endingDate = new Timestamp(calendar.getTimeInMillis());
        }

        if (endingDate.before(startingDate))
        return ResponseEntity.ok(new BaseResponse(RequestStatus.ERROR.getStatus(), statusCodeBundle.getExecutionHistoryDateErrorCode(),statusCodeBundle.getExecutionHistoryEndDateBeforeStartDateMessage()));
        return ResponseEntity.ok(new ContentResponse<>(Constants.EXECUTION_HISTORY, executionHistoryService.executionHistoryDateFilter(id, startingDate, endingDate), RequestStatus.SUCCESS.getStatus(), statusCodeBundle.getCommonSuccessCode(), statusCodeBundle.getViewExecutionHistoryMessage()));
    }

    @DeleteMapping(value = EndpointURI.EXECUTION_HISTORY_PROJECT_ID)
    public ResponseEntity<Object> deleteExecutionHistoryById(@PathVariable Long id, @PathVariable Long projectId) {
        if (!executionHistoryService.existByExecutionHistoryId(id)) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getExecutionHistoryNotExistsCode(), statusCodeBundle.getExecutionHistoryNotFound()));
        }
        if (!projectService.existByProjectId(projectId)) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getProjectNotExistCode(), statusCodeBundle.getProjectNotExistsMessage()));
        }
        if (!executionHistoryService.deleteExecutionHistory(id, projectId)) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFileFailureCode(), statusCodeBundle.getGetFileNotExits()));
        }
        return ResponseEntity.ok(new BaseResponse(RequestStatus.SUCCESS.getStatus(), statusCodeBundle.getCommonSuccessCode(), statusCodeBundle.getExecutionHistoryDeleteSuccessMessage()));
    }

    @PostMapping(value = EndpointURI.EXECUTION_HISTORY_EMAIL)
    public ResponseEntity<Object> emailHistoryReports(@RequestBody EmailRequest emailRequest) throws IOException, MessagingException {

        if (emailRequest.getHistoryReportIds() == null || emailRequest.getHistoryReportIds().isEmpty())
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getExecutionHistoryMailFailureCode(), statusCodeBundle.getExecutionHistoryIdNull()));
        if (emailRequest.getToEmails() == null || emailRequest.getToEmails().isEmpty())
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getExecutionHistoryMailFailureCode(), statusCodeBundle.getExecutionHistoryMailFailureMessage()));
        for (Long id : emailRequest.getHistoryReportIds()) {
            if (!executionHistoryService.existByExecutionHistoryId(id))
                return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getExecutionHistoryNotExistsCode(), statusCodeBundle.getExecutionHistoryNotFound()));
        }
        executionHistoryService.emailHistoryReports(emailRequest);
        return ResponseEntity.ok(new BaseResponse(RequestStatus.SUCCESS.getStatus(), statusCodeBundle.getCommonSuccessCode(), statusCodeBundle.getExecutionHistoryMailSuccessMessage()));

    }
}