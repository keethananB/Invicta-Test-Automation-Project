package com.ii.testautomation.controllers;

import com.ii.testautomation.dto.request.TestScenariosRequest;
import com.ii.testautomation.enums.RequestStatus;
import com.ii.testautomation.response.common.BaseResponse;
import com.ii.testautomation.response.common.ContentResponse;
import com.ii.testautomation.response.common.PaginatedContentResponse;
import com.ii.testautomation.service.ProjectService;
import com.ii.testautomation.service.TestGroupingService;
import com.ii.testautomation.service.TestScenariosService;
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

@RestController
@CrossOrigin
public class TestScenariosController {
    @Autowired
    private TestScenariosService testScenariosService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private TestGroupingService testGroupingService;
    @Autowired
    private StatusCodeBundle statusCodeBundle;

    @PostMapping(EndpointURI.TEST_SCENARIO)
    public ResponseEntity<Object> insertScenario(@RequestBody TestScenariosRequest testScenariosRequest) {
        if (testScenariosRequest.getName() == null || testScenariosRequest.getProjectId() == null)
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getTestScenariosAlreadyExistCode(), statusCodeBundle.getTestScenarioNameAndIdNullMessage()));

        if (testScenariosRequest.getTestCasesId() == null && testScenariosRequest.getMainModuleIds() == null
                && testScenariosRequest.getModuleIds() == null && testScenariosRequest.getSubModuleIds() == null)
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getTestScenarioNotExistCode(), statusCodeBundle.getTestCasesNotProvidedMessage()));

        if (testScenariosRequest.getTestCasesId().isEmpty() && testScenariosRequest.getMainModuleIds().isEmpty()
                && testScenariosRequest.getModuleIds().isEmpty() && testScenariosRequest.getSubModuleIds().isEmpty())
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getTestScenarioNotExistCode(), statusCodeBundle.getTestCasesNotProvidedMessage()));
        if (!Utils.checkRegexBeforeAfterWords(testScenariosRequest.getName()))
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getSpacesNotAllowedMessage()));
        if (testScenariosService.existsByTestScenarioNameIgnoreCase(testScenariosRequest.getName(), testScenariosRequest.getProjectId()))
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getTestScenariosAlreadyExistCode(), statusCodeBundle.getTestScenariosNameAlreadyExistMessage()));
        if (!projectService.existByProjectId(testScenariosRequest.getProjectId()))
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(),
                    statusCodeBundle.getProjectNotExistCode(),
                    statusCodeBundle.getProjectNotExistsMessage()));
        testScenariosService.saveTestScenario(testScenariosRequest);
        return ResponseEntity.ok(new BaseResponse(RequestStatus.SUCCESS.getStatus(), statusCodeBundle.getCommonSuccessCode(), statusCodeBundle.getTestScenariosSaveMessage()));

    }

    @PutMapping(EndpointURI.TEST_SCENARIO)
    public ResponseEntity<Object> UpdateTestScenarios(@RequestBody TestScenariosRequest testScenariosRequest) {

        if (!testScenariosService.existsByTestScenarioId(testScenariosRequest.getId())) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(),
                    statusCodeBundle.getTestScenariosNotExistCode(),
                    statusCodeBundle.getTestScenariosIdNotExistMessage()));

        }
        if (testScenariosRequest.getName() == null || testScenariosRequest.getProjectId() == null || testScenariosRequest.getId() == null) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(),
                    statusCodeBundle.getTestScenariosAlreadyExistCode(),
                    statusCodeBundle.getTestScenarioNameAndIdNullMessage()));

        }
        if (!Utils.checkRegexBeforeAfterWords(testScenariosRequest.getName()))
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getSpacesNotAllowedMessage()));

        if (testScenariosService.isUpdateTestScenariosNameExists(testScenariosRequest.getId(), testScenariosRequest.getName(), testScenariosRequest.getProjectId())) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(),
                    statusCodeBundle.getTestScenariosAlreadyExistCode(),
                    statusCodeBundle.getTestScenariosNameAlreadyExistMessage()));
        }
        testScenariosService.saveTestScenario(testScenariosRequest);
        return ResponseEntity.ok(new BaseResponse(RequestStatus.SUCCESS.getStatus(),
                statusCodeBundle.getCommonSuccessCode(),
                statusCodeBundle.getUpdateTestScenarioSuccessMessage()));
    }

    @GetMapping(EndpointURI.TEST_SCENARIO_BY_ID)
    public ResponseEntity<Object> viewScenarioById(@PathVariable Long id) {
        if (!testScenariosService.existsByTestScenarioId(id))
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getTestScenarioNotExistCode(), statusCodeBundle.getTestScenarioNotExistsMessage()));

        return ResponseEntity.ok(new ContentResponse<>(Constants.TESTSCENARIO, testScenariosService.viewScenarioById(id), RequestStatus.SUCCESS.getStatus(), statusCodeBundle.getCommonSuccessCode(), statusCodeBundle.getTestScenarioViewMessage()));
    }

    @GetMapping(EndpointURI.TEST_SCENARIO_BY_PROJECT_ID)
    public ResponseEntity<Object> getAllTestScenariosByProjectIdWithPagination(@PathVariable Long id,
                                                                               @RequestParam(name = "page") int page,
                                                                               @RequestParam(name = "size") int size, @RequestParam(name = "direction") String direction,
                                                                               @RequestParam(name = "sortField") String sortField) {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.valueOf(direction), sortField);
        PaginatedContentResponse.Pagination pagination = new PaginatedContentResponse.Pagination(page, size, 0, 0L);
        if (!projectService.existByProjectId(id)) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getProjectNotExistsMessage()));
        }
        return ResponseEntity.ok(new PaginatedContentResponse<>(Constants.TESTSCENARIOS, testScenariosService.getAllTestScenariosByProjectIdWithPagination(id, pageable, pagination),
                RequestStatus.SUCCESS.getStatus(),
                statusCodeBundle.getCommonSuccessCode(),
                statusCodeBundle.getGetAllTestScenarioSuccessGivenProjectId(), pagination));
    }

    @DeleteMapping(value = EndpointURI.TEST_SCENARIO_BY_ID)
    public ResponseEntity<Object> DeleteTestScenarioById(@PathVariable Long id) {

        if (!testScenariosService.existsByTestScenarioId(id)) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getTestScenarioNotExistCode(), statusCodeBundle.getTestScenarioNotExistsMessage()));
        }
        if (testGroupingService.existsTestGroupingByTestScenarioId(id)) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(),
                    statusCodeBundle.getTestScenarioIdDependentCode(),
                    statusCodeBundle.getTestScenarioIdDependentMessage()));
        }
        testScenariosService.DeleteTestScenariosById(id);
        return ResponseEntity.ok(new BaseResponse(RequestStatus.SUCCESS.getStatus(), statusCodeBundle.getCommonSuccessCode(), statusCodeBundle.getDeleteTestScenarioSuccessMessage()));
    }
}
