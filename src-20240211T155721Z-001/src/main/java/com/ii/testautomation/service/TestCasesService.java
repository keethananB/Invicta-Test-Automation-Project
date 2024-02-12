package com.ii.testautomation.service;

import com.ii.testautomation.dto.request.TestCaseRequest;
import com.ii.testautomation.dto.response.TestCaseResponse;
import com.ii.testautomation.dto.search.TestCaseSearch;
import com.ii.testautomation.response.common.PaginatedContentResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface TestCasesService {
    void saveTestCase(TestCaseRequest testCaseRequest);

    boolean existsByTestCasesId(Long id);

    boolean existsByTestCasesName(String testCaseName, Long subModulesId);

    TestCaseResponse getById(Long id);

    boolean isUpdateTestCaseNameExists(String name, Long id, Long subModuleId);

    boolean isUpdateTestCaseNameExistsSubString(String name,Long id,Long subModuleId);

    List<TestCaseResponse> multiSearchTestCase(Pageable pageable, PaginatedContentResponse.Pagination pagination, TestCaseSearch testCaseSearch);

    List<TestCaseResponse> getAllTestCaseBySubModuleId(Long subModuleId);

    void DeleteTestCaseById(Long id);

    boolean existsBySubModuleId(Long subModuleId);

    boolean hasExcelFormat(MultipartFile multipartFile);

    Map<Integer, TestCaseRequest> csvToTestCaseRequest(InputStream inputStream, Long projectId);

    Map<Integer, TestCaseRequest> excelToTestCaseRequest(MultipartFile multipartFile, Long projectId);

    boolean isExcelHeaderMatch(MultipartFile multipartFile);

    boolean isCSVHeaderMatch(MultipartFile multipartFile);

    void addToErrorMessages(Map<String, List<Integer>> errorMessages, String key, int value);

    List<TestCaseResponse> getAllTestcasesByProjectIdWithPagination(Long projectId, Pageable pageable, PaginatedContentResponse.Pagination pagination);

    List<TestCaseResponse> getAllTestCasesByModuleId(Long moduleId);

    List<TestCaseResponse> getAllTestCasesByMainModuleId(Long MainModuleId);

    boolean existsTestCaseByProjectId(Long projectId);

    void updateExecutionStatus(Long testCaseId);

    public boolean existsTestCaseNameSubString(String testCaseName, Long subModuleId);

}

