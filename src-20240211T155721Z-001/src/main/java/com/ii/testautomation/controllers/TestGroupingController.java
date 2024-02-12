package com.ii.testautomation.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ii.testautomation.config.ProgressWebSocketHandler;
import com.ii.testautomation.config.WebSocketConfig;
import com.ii.testautomation.dto.request.ExecutionRequest;
import com.ii.testautomation.dto.request.TestGroupingRequest;
import com.ii.testautomation.dto.response.ScheduledTestScenarioResponse;
import com.ii.testautomation.dto.response.SchedulingGroupingTestCases;
import com.ii.testautomation.dto.response.TestGroupingResponse;
import com.ii.testautomation.dto.search.TestGroupingSearch;
import com.ii.testautomation.enums.RequestStatus;
import com.ii.testautomation.response.common.BaseResponse;
import com.ii.testautomation.response.common.ContentResponse;
import com.ii.testautomation.response.common.PaginatedContentResponse;
import com.ii.testautomation.service.*;
import com.ii.testautomation.utils.Constants;
import com.ii.testautomation.utils.EndpointURI;
import com.ii.testautomation.utils.StatusCodeBundle;
import com.ii.testautomation.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
public class TestGroupingController {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TestGroupingService testGroupingService;
    @Autowired
    private TestScenariosService testScenariosService;
    @Autowired
    private TestTypesService testTypesService;
    @Autowired
    private TestCasesService testCasesService;
    @Autowired
    private ModulesService modulesService;
    @Autowired
    private SubModulesService subModulesService;
    @Autowired
    private MainModulesService mainModulesService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private StatusCodeBundle statusCodeBundle;
    @Autowired
    private ProgressWebSocketHandler progressWebSocketHandler;
    @Autowired
    private ExecutionHistoryService executionHistoryService;
    @Autowired
    private WebSocketConfig webSocketConfig;
    @Autowired
    private SchedulingService schedulingService;

    @PostMapping(value = EndpointURI.TEST_GROUPING)
    public ResponseEntity<Object> saveTestGrouping(@RequestParam String testGrouping, @RequestParam(value = "excelFiles", required = false) List<MultipartFile> excelFiles) throws JsonProcessingException, JsonProcessingException {

        TestGroupingRequest testGroupingRequest = objectMapper.readValue(testGrouping, TestGroupingRequest.class);
        if ((testGroupingRequest.getTestCaseId() == null || testGroupingRequest.getTestCaseId().isEmpty()) && (testGroupingRequest.getSubModuleIds() == null || testGroupingRequest.getSubModuleIds().isEmpty()) &&
                (testGroupingRequest.getModuleIds() == null || testGroupingRequest.getModuleIds().isEmpty()) && (testGroupingRequest.getMainModuleIds() == null || testGroupingRequest.getMainModuleIds().isEmpty()) &&
                (testGroupingRequest.getTestScenarioIds() == null || testGroupingRequest.getTestScenarioIds().isEmpty())) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getWantToOneHaveOneTestScenarioOrOneTestCase()));
        }
        if (!projectService.existByProjectId(testGroupingRequest.getProjectId())) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.SUCCESS.getStatus(), statusCodeBundle.getProjectNotExistCode(), statusCodeBundle.getProjectNotExistsMessage()));
        }
        if (!testTypesService.existsByTestTypesId(testGroupingRequest.getTestTypeId())) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getTestTypesNotExistCode(), statusCodeBundle.getTestTypesNotExistsMessage()));
        }
        if (!Utils.checkRegexBeforeAfterWords(testGroupingRequest.getName()))
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getSpacesNotAllowedMessage()));
        if (testGroupingService.existsByTestGroupingNameByTestCaseAndProjectId(testGroupingRequest.getName(), testGroupingRequest.getProjectId())) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getTestGroupingAlReadyExistCode(), statusCodeBundle.getTestGroupingNameAlReadyExistMessage()));
        }
        if (testGroupingService.existsByTestGroupingNameByTestScenarioAndProjectId(testGroupingRequest.getName(), testGroupingRequest.getProjectId())) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getTestGroupingAlReadyExistCode(), statusCodeBundle.getTestGroupingNameAlReadyExistMessage()));
        }
        if (testGroupingRequest.getTestCaseId() != null) {
            for (Long testCaseId : testGroupingRequest.getTestCaseId()) {
                if (!testCasesService.existsByTestCasesId(testCaseId)) {
                    return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getTestCasesNotExistCode(), statusCodeBundle.getTestCasesNotExistsMessage()));
                }
            }
        }
        if (testGroupingRequest.getSubModuleIds() != null) {
            for (Long subModuleId : testGroupingRequest.getSubModuleIds()) {
                if (!subModulesService.existsBySubModuleId(subModuleId)) {
                    return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getSubModulesNotExistCode(), statusCodeBundle.getSubModuleNotExistsMessage()));
                }
            }
        }
        if (testGroupingRequest.getMainModuleIds() != null) {
            for (Long mainModuleId : testGroupingRequest.getMainModuleIds()) {
                if (!mainModulesService.isExistMainModulesId(mainModuleId)) {
                    return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getMainModulesNotExistCode(), statusCodeBundle.getMainModuleNotExistsMessage()));
                }
            }
        }
        if (testGroupingRequest.getTestScenarioIds() != null) {
            for (Long testScenarioId : testGroupingRequest.getTestScenarioIds()) {
                if (!testScenariosService.existsByTestScenarioId(testScenarioId)) {
                    return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getTestScenariosNotExistCode(), statusCodeBundle.getTestScenarioNotExistsMessage()));
                }
            }
        }
        if (!testGroupingService.hasExcelFormat(excelFiles)) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFileFailureCode(), statusCodeBundle.getFileFailureMessage()));
        }
        testGroupingService.saveTestGrouping(testGroupingRequest, excelFiles);
        return ResponseEntity.ok(new BaseResponse(RequestStatus.SUCCESS.getStatus(), statusCodeBundle.getCommonSuccessCode(), statusCodeBundle.getSaveTestGroupingSuccessMessage()));
    }

    @PutMapping(value = EndpointURI.TEST_GROUPING)
    public ResponseEntity<Object> editTestGrouping(@RequestParam String testGrouping, @RequestParam(value = "excelFiles", required = false) List<MultipartFile> excelFiles) throws JsonProcessingException, JsonProcessingException {
        TestGroupingRequest testGroupingRequest = objectMapper.readValue(testGrouping, TestGroupingRequest.class);
        if (!testGroupingService.existsByTestGroupingId(testGroupingRequest.getId())) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getTestGroupingNotExistCode(), statusCodeBundle.getTestGroupingNotExistsMessage()));
        }
        if (!testTypesService.existsByTestTypesId(testGroupingRequest.getTestTypeId())) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getTestTypesNotExistCode(), statusCodeBundle.getTestTypesNotExistsMessage()));
        }
        if ((testGroupingRequest.getTestCaseId() == null || testGroupingRequest.getTestCaseId().isEmpty()) && (testGroupingRequest.getSubModuleIds() == null || testGroupingRequest.getSubModuleIds().isEmpty()) &&
                (testGroupingRequest.getModuleIds() == null || testGroupingRequest.getModuleIds().isEmpty()) && (testGroupingRequest.getMainModuleIds() == null || testGroupingRequest.getMainModuleIds().isEmpty()) &&
                (testGroupingRequest.getTestScenarioIds() == null || testGroupingRequest.getTestScenarioIds().isEmpty())) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getWantToOneHaveOneTestScenarioOrOneTestCase()));
        }
        if (!Utils.checkRegexBeforeAfterWords(testGroupingRequest.getName()))
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getSpacesNotAllowedMessage()));
        if (testGroupingService.isUpdateTestGroupingNameByProjectId(testGroupingRequest.getName(), testGroupingRequest.getProjectId(), testGroupingRequest.getId())) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getTestGroupingAlReadyExistCode(), statusCodeBundle.getTestGroupingNameAlReadyExistMessage()));
        }
        if (testGroupingRequest.getTestCaseId() != null) {
            for (Long testCaseId : testGroupingRequest.getTestCaseId()) {
                if (!testCasesService.existsByTestCasesId(testCaseId)) {
                    return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getTestCasesNotExistCode(), statusCodeBundle.getTestCasesNotExistsMessage()));
                }
            }
        }
        if (testGroupingRequest.getSubModuleIds() != null) {
            for (Long subModuleId : testGroupingRequest.getSubModuleIds()) {
                if (!subModulesService.existsBySubModuleId(subModuleId)) {
                    return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getSubModulesNotExistCode(), statusCodeBundle.getSubModuleNotExistsMessage()));
                }
            }
        }
        if (testGroupingRequest.getMainModuleIds() != null) {
            for (Long mainModuleId : testGroupingRequest.getMainModuleIds()) {
                if (!mainModulesService.isExistMainModulesId(mainModuleId)) {
                    return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getMainModulesNotExistCode(), statusCodeBundle.getMainModuleNotExistsMessage()));
                }
            }
        }
        if (testGroupingRequest.getTestScenarioIds() != null) {
            for (Long testScenarioId : testGroupingRequest.getTestScenarioIds()) {
                if (!testScenariosService.existsByTestScenarioId(testScenarioId)) {
                    return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getTestScenariosNotExistCode(), statusCodeBundle.getTestScenarioNotExistsMessage()));
                }
            }
        }
        if (!testGroupingService.hasExcelFormat(excelFiles)) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFileFailureCode(), statusCodeBundle.getFileFailureMessage()));
        }
        if (!testGroupingService.folderExists(testGroupingRequest.getId())) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFileFailureCode(), "folder doesnot exists"));
        }
        testGroupingService.updateTestGrouping(testGroupingRequest, excelFiles);
        return ResponseEntity.ok(new BaseResponse(RequestStatus.SUCCESS.getStatus(), statusCodeBundle.getCommonSuccessCode(), statusCodeBundle.getUpdateTestGroupingSuccessMessage()));
    }

    @PutMapping(value = EndpointURI.TEST_GROUPING_UPDATE_EXECUTION_STATUS)
    public ResponseEntity<Object> updateExecution(@RequestBody ExecutionRequest executionRequest) throws IOException {
        if (!projectService.existByProjectId(executionRequest.getProjectId())) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getProjectNotExistCode(), statusCodeBundle.getProjectNotExistsMessage()));
        }
        for (Map.Entry<Integer, Long> entry : executionRequest.getTestScenario().entrySet()) {
            if (!testScenariosService.existsByTestScenarioId(entry.getValue())) {
                return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getTestScenariosNotExistCode(), "testScenarioNotExists"));
            }
        }
        for (Map.Entry<Integer, Long> entry : executionRequest.getTestCase().entrySet()) {
            if (!testCasesService.existsByTestCasesId(entry.getValue())) {
                return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getTestScenariosNotExistCode(), "testScenarioNotExists"));
            }
        }
        if (!projectService.hasJarPath(executionRequest.getProjectId())) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getProjectJarPathNotProvideMessage()));
        }
        if (!projectService.hasConfigPath(executionRequest.getProjectId())) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getProjectConfigPathNotProvideMessage()));
        }
        if (!testGroupingService.hasExcelPath(executionRequest.getTestGroupingId())) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getExcelPathNotProvideMessage()));
        }
        testGroupingService.execution(executionRequest);
        return ResponseEntity.ok(new BaseResponse(RequestStatus.SUCCESS.getStatus(), statusCodeBundle.getCommonSuccessCode(), statusCodeBundle.getExecutionSuccessMessage()));
    }

    @GetMapping(value = EndpointURI.TEST_GROUPING)
    public ResponseEntity<Object> getAllWithMultiSearch(@RequestParam(name = "page") int page, @RequestParam(name = "size") int size, @RequestParam(name = "direction") String direction, @RequestParam(name = "sortField") String sortField, TestGroupingSearch testGroupingSearch) {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.valueOf(direction), sortField);
        PaginatedContentResponse.Pagination pagination = new PaginatedContentResponse.Pagination(page, size, 0, 0l);
        return ResponseEntity.ok(new ContentResponse<>(Constants.TEST_GROUPINGS, testGroupingService.multiSearchTestGrouping(pageable, pagination, testGroupingSearch), RequestStatus.SUCCESS.getStatus(), statusCodeBundle.getCommonSuccessCode(), statusCodeBundle.getGetAllTestGroupingSuccessMessage()));
    }

    @GetMapping(value = EndpointURI.TEST_GROUPING_BY_TEST_TYPE_ID)
    public ResponseEntity<BaseResponse> getAllTestGroupingByTestTypeId(@PathVariable Long id) {
        if (!testTypesService.existsByTestTypesId(id)) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getTestTypeNotExistCode(), statusCodeBundle.getTestTypeNotExistMessage()));
        }
        List<TestGroupingResponse> testGroupingResponseList = testGroupingService.getAllTestGroupingByTestTypeId(id);
        if (testGroupingResponseList.isEmpty()) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getTestGroupingFileEmptyMessage()));

        }
        return ResponseEntity.ok(new ContentResponse<>(Constants.TEST_GROUPINGS, testGroupingResponseList, RequestStatus.SUCCESS.getStatus(), statusCodeBundle.getCommonSuccessCode(), statusCodeBundle.getTestGroupingByTestType()));
    }

    @DeleteMapping(value = EndpointURI.TEST_GROUPING_BY_ID)
    public ResponseEntity<Object> deleteTestGroupingById(@PathVariable Long id, @PathVariable Long projectId) {
        if (!testGroupingService.existsByTestGroupingId(id)) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getTestGroupingNotExistCode(), statusCodeBundle.getTestGroupingNotExistsMessage()));
        }
        if (executionHistoryService.existByTestGropingId(id)) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getTestGroupingDependentCode(), statusCodeBundle.getTestGroupingDeleteDependentMessage()));
        }
        if (schedulingService.existsByTestGroupingId(id))
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getTestGroupingDependentCode(), statusCodeBundle.getTestGroupingDeleteDependentMessage()));
        testGroupingService.deleteTestGroupingById(id, projectId);
        return ResponseEntity.ok(new BaseResponse(RequestStatus.SUCCESS.getStatus(), statusCodeBundle.getCommonSuccessCode(), statusCodeBundle.getDeleteTestGroupingSuccessMessage()));
    }

    @GetMapping(value = EndpointURI.TEST_GROUPINGS_BY_ID)
    public ResponseEntity<Object> getTestGroupingById(@PathVariable Long id) {
        if (!testGroupingService.existsByTestGroupingId(id)) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getTestGroupingNotExistCode(), statusCodeBundle.getTestGroupingNotExistsMessage()));
        }
        return ResponseEntity.ok(new ContentResponse<>(Constants.TEST_GROUPING, testGroupingService.getTestGroupingById(id), RequestStatus.SUCCESS.getStatus(), statusCodeBundle.getCommonSuccessCode(), statusCodeBundle.getGetTestGroupingSuccessMessage()));
    }

    @GetMapping(value = EndpointURI.TEST_GROUPING_BY_TEST_CASE_ID)
    public ResponseEntity<Object> getTestGroupingByTestCaseId(@PathVariable Long id) {
        if (!testCasesService.existsByTestCasesId(id)) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getTestCasesNotExistCode(), statusCodeBundle.getTestCasesNotExistsMessage()));
        }
        if (!testGroupingService.existsByTestCasesId(id)) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getGetTestGroupingNotHaveTestCaseId()));
        }
        return ResponseEntity.ok(new ContentResponse<>(Constants.TEST_GROUPING, testGroupingService.getAllTestGroupingByTestCaseId(id), RequestStatus.SUCCESS.getStatus(), statusCodeBundle.getCommonSuccessCode(), statusCodeBundle.getGetTestGroupingSuccessMessage()));

    }

    @GetMapping(value = EndpointURI.TEST_GROUPING_BY_PROJECT_ID)
    public ResponseEntity<Object> getTestGroupingByProjectId(@RequestParam(name = "page") int page,
                                                             @RequestParam(name = "size") int size,
                                                             @RequestParam(name = "direction") String direction,
                                                             @RequestParam(name = "sortField") String sortField,
                                                             @PathVariable Long id) {
        if (!projectService.existByProjectId(id)) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getProjectNotExistCode(), statusCodeBundle.getProjectNotExistsMessage()));
        }
        if (!testGroupingService.existByProjectId(id)) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getProjectNotExistCode(), statusCodeBundle.getGetTestGroupingNotHaveProjectId()));
        }
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.valueOf(direction), sortField);
        PaginatedContentResponse.Pagination pagination = new PaginatedContentResponse.Pagination(page, size, 0, 0L);
        return ResponseEntity.ok(new ContentResponse<>(Constants.TEST_GROUPING, testGroupingService.getAllTestGroupingByProjectId(pageable, pagination, id), RequestStatus.SUCCESS.getStatus(), statusCodeBundle.getCommonSuccessCode(), statusCodeBundle.getGetTestGroupingSuccessMessage()));
    }
    @GetMapping(value = EndpointURI.TEST_GROUPING_SCHEDULING)
    public ResponseEntity<Object> getScheduledTestCases(@PathVariable Long id) {
        if(!testGroupingService.existsByTestGroupingId(id))
        {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getTestGroupingNotExistCode(), statusCodeBundle.getTestGroupingNotExistsMessage()));
        }
        List<SchedulingGroupingTestCases> schedulingGroupingTestCases=testGroupingService.getScheduledTestCases(id);
        if(schedulingGroupingTestCases==null && schedulingGroupingTestCases.isEmpty())
        {
           return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFileFailureCode(),statusCodeBundle.getGroupingNotHaveTScheduledTestCases()));
        }
        return ResponseEntity.ok(new ContentResponse<>(Constants.TEST_GROUPING_SCHEDULING_TESTCASES,schedulingGroupingTestCases,RequestStatus.SUCCESS.getStatus(),statusCodeBundle.getCommonSuccessCode(),statusCodeBundle.getTestGroupingTestCasesSuccessfully()));
    }

    @GetMapping(value = EndpointURI.TEST_GROUPING_SCHEDULING_TESTCASES)
    public ResponseEntity<Object> getScheduledTestScenarios(@PathVariable Long id) {
        if(!testGroupingService.existsByTestGroupingId(id))
        {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getTestGroupingNotExistCode(), statusCodeBundle.getTestGroupingNotExistsMessage()));
        }
        List<ScheduledTestScenarioResponse> schedulingGroupingTestCases=testGroupingService.getScheduledTestScenario(id);
        if(schedulingGroupingTestCases==null && schedulingGroupingTestCases.isEmpty())
        {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFileFailureCode(),statusCodeBundle.getGroupingNotHaveTScheduledTestCases()));
        }
        return ResponseEntity.ok(new ContentResponse<>(Constants.TEST_GROUPING_SCHEDULING_TESTCASES,schedulingGroupingTestCases,RequestStatus.SUCCESS.getStatus(),statusCodeBundle.getCommonSuccessCode(),statusCodeBundle.getTestGroupingTestCasesSuccessfully()));
    }
}