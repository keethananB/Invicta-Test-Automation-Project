package com.ii.testautomation.service;

import com.ii.testautomation.dto.request.ExecutionRequest;
import com.ii.testautomation.dto.request.TestGroupingRequest;
import com.ii.testautomation.dto.response.ScheduledTestScenarioResponse;
import com.ii.testautomation.dto.response.SchedulingGroupingTestCases;
import com.ii.testautomation.dto.response.TestGroupingResponse;
import com.ii.testautomation.dto.search.TestGroupingSearch;
import com.ii.testautomation.response.common.PaginatedContentResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface TestGroupingService {
    void saveTestGrouping(TestGroupingRequest testGroupingRequest, List<MultipartFile> excelFiles);

    void updateTestGrouping(TestGroupingRequest testGroupingRequest, List<MultipartFile> excelFiles);

    boolean hasExcelFormat(List<MultipartFile> multipartFiles);

    boolean allTestCasesInSameProject(List<Long> testCaseIds);

    boolean existsByTestGroupingId(Long testGroupingId);

    boolean existsByTestCasesId(Long testCaseId);

    boolean existsByTestTypesId(Long testTypeId);

    List<TestGroupingResponse> getAllTestGroupingByTestTypeId(Long testTypeId);

    List<TestGroupingResponse> multiSearchTestGrouping(Pageable pageable, PaginatedContentResponse.Pagination pagination, TestGroupingSearch testGroupingSearch);

    List<TestGroupingResponse> getAllTestGroupingByTestCaseId(Long testCaseId);

    TestGroupingResponse getTestGroupingById(Long id);


    boolean existByProjectId(Long projectId);

    List<TestGroupingResponse> getAllTestGroupingByProjectId(Pageable pageable, PaginatedContentResponse.Pagination pagination, Long projectId);

    boolean existsByTestGroupingNameByTestCaseAndProjectId(String name, Long projectId);

    boolean existsByTestGroupingNameByTestScenarioAndProjectId(String name, Long projectId);

    boolean isUpdateTestGroupingNameByProjectId(String name, Long projectId, Long groupingId);

    boolean hasExcelPath(Long testGroupingId);

    boolean existsTestGroupingByTestScenarioId(Long id);

    boolean existsById(Long id);

    void deleteTestGroupingById(Long id, Long projectId);

    void execution(ExecutionRequest executionRequest) throws IOException;

    boolean folderExists(Long groupId);

    List<SchedulingGroupingTestCases> getScheduledTestCases(Long groupId);

    List<ScheduledTestScenarioResponse> getScheduledTestScenario(Long groupId);
}
