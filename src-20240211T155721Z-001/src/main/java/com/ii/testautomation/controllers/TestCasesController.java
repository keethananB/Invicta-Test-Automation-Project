package com.ii.testautomation.controllers;

import com.ii.testautomation.dto.request.TestCaseRequest;
import com.ii.testautomation.dto.response.TestCaseResponse;
import com.ii.testautomation.dto.search.TestCaseSearch;
import com.ii.testautomation.enums.RequestStatus;
import com.ii.testautomation.response.common.*;
import com.ii.testautomation.service.*;
import com.ii.testautomation.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController
@CrossOrigin
public class TestCasesController {
    @Autowired
    private TestCasesService testCasesService;
    @Autowired
    private SubModulesService subModulesService;
    @Autowired
    private StatusCodeBundle statusCodeBundle;
    @Autowired
    private TestGroupingService testGroupingService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private ModulesService modulesService;
    @Autowired
    private MainModulesService mainModulesService;

    @PostMapping(value = EndpointURI.TESTCASE)
    public ResponseEntity<Object> saveTestCase(@RequestBody TestCaseRequest testCaseRequest) {
        if (!subModulesService.existsBySubModuleId(testCaseRequest.getSubModuleId())) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getSubModulesNotExistCode(), statusCodeBundle.getSubModuleNotExistsMessage()));
        }
        if (!Utils.checkRagexBeforeAfterWordsTestCases(testCaseRequest.getName()))
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getSpacesNotAllowedMessage()));
        if (testCaseRequest.getName() == null && testCaseRequest.getName().isEmpty()) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.ERROR.getStatus(), statusCodeBundle.getNullValuesCode(),
                    statusCodeBundle.getTestCaseNameEmptyMessage()));
        }
        if (testCasesService.existsByTestCasesName(testCaseRequest.getName(), testCaseRequest.getSubModuleId())) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getTestCasesAlreadyExistsCode(), statusCodeBundle.getTestCaseNameAlreadyExistsMessage()));
        }
        if (testCasesService.existsTestCaseNameSubString(testCaseRequest.getName(), testCaseRequest.getSubModuleId())) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getTestCasesAlreadyExistsCode(), statusCodeBundle.getTestCaseNameAlreadyExistsMessage()));
        }
        testCasesService.saveTestCase(testCaseRequest);
        return ResponseEntity.ok(new BaseResponse(RequestStatus.SUCCESS.getStatus(), statusCodeBundle.getCommonSuccessCode(), statusCodeBundle.getSaveTestCaseSuccessMessage()));
    }

    @GetMapping(value = EndpointURI.TESTCASE_BY_ID)
    public ResponseEntity<Object> GetTestcaseById(@PathVariable Long id) {
        if (!testCasesService.existsByTestCasesId(id)) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getTestCasesNotExistCode(), statusCodeBundle.getTestCasesNotExistsMessage()));
        }
        return ResponseEntity.ok(new ContentResponse<>(Constants.TESTCASE, testCasesService.getById(id), RequestStatus.SUCCESS.getStatus(), statusCodeBundle.getCommonSuccessCode(), statusCodeBundle.getGetTestCaseByIdSuccessMessage()));
    }

    @PutMapping(value = EndpointURI.TESTCASE)
    public ResponseEntity<Object> UpdateTestCase(@RequestBody TestCaseRequest testCaseRequest) {
        if (!testCasesService.existsByTestCasesId(testCaseRequest.getId())) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(),
                    statusCodeBundle.getTestCasesNotExistCode(),
                    statusCodeBundle.getTestCasesNotExistsMessage()));
        }
        if (!Utils.checkRagexBeforeAfterWordsTestCases(testCaseRequest.getName()))
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getSpacesNotAllowedMessage()));
        if (testCaseRequest.getName() == null && testCaseRequest.getName().isEmpty()) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.ERROR.getStatus(), statusCodeBundle.getNullValuesCode(),
                    statusCodeBundle.getTestCaseNameEmptyMessage()));
        }
        if (testCasesService.isUpdateTestCaseNameExists(testCaseRequest.getName(), testCaseRequest.getId(), testCaseRequest.getSubModuleId())) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(),
                    statusCodeBundle.getTestCasesAlreadyExistsCode(),
                    statusCodeBundle.getTestCaseNameAlreadyExistsMessage()));
        }
        if (testCasesService.isUpdateTestCaseNameExistsSubString(testCaseRequest.getName(), testCaseRequest.getId(), testCaseRequest.getSubModuleId())) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getTestCasesAlreadyExistsCode(), statusCodeBundle.getTestCaseNameAlreadyExistsMessage()));
        }
        if (!subModulesService.existsBySubModuleId(testCaseRequest.getSubModuleId())) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(),
                    statusCodeBundle.getSubModulesNotExistCode(),
                    statusCodeBundle.getSubModuleNotExistsMessage()));
        }
        testCasesService.saveTestCase(testCaseRequest);
        return ResponseEntity.ok(new BaseResponse(RequestStatus.SUCCESS.getStatus(),
                statusCodeBundle.getCommonSuccessCode(),
                statusCodeBundle.getUpdateTestCaseSuccessMessage()));
    }

    @GetMapping(value = EndpointURI.TESTCASES)
    public ResponseEntity<Object> getAllWithMultiSearch(@RequestParam(name = "page") int page, @RequestParam(name = "size") int size, @RequestParam(name = "direction") String direction, @RequestParam(name = "sortField") String sortField, TestCaseSearch testCaseSearch) {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.valueOf(direction), sortField);
        PaginatedContentResponse.Pagination pagination = new PaginatedContentResponse.Pagination(page, size, 0, 0L);
        return ResponseEntity.ok(new ContentResponse<>(Constants.TESTCASES, testCasesService.multiSearchTestCase(pageable, pagination, testCaseSearch), RequestStatus.SUCCESS.getStatus(), statusCodeBundle.getCommonSuccessCode(), statusCodeBundle.getGetAllTestCasesSuccessMessage()));
    }

    @PostMapping(EndpointURI.TESTCASE_IMPORT)
    public ResponseEntity<Object> testCaseImport(@PathVariable Long id, @RequestParam MultipartFile multipartFile) {
        Long projectId = id;
        Map<String, List<Integer>> errorMessages = new HashMap<>();
        Map<Integer, TestCaseRequest> testCaseRequestList;
        Set<String> testCasesNames = new HashSet<>();
        try {
            if (Objects.requireNonNull(multipartFile.getOriginalFilename()).endsWith(".csv")) {
                testCaseRequestList = testCasesService.csvToTestCaseRequest(multipartFile.getInputStream(), projectId);
            } else if (testCasesService.hasExcelFormat(multipartFile)) {
                testCaseRequestList = testCasesService.excelToTestCaseRequest(multipartFile, projectId);
            } else {
                return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(),
                        statusCodeBundle.getFileFailureCode(), statusCodeBundle.getFileFailureMessage()));
            }
            if (!testCasesService.isCSVHeaderMatch(multipartFile) && (!testCasesService.isExcelHeaderMatch(multipartFile))) {
                return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(),
                        statusCodeBundle.getFailureCode(), statusCodeBundle.getHeaderNotExistsMessage()));
            }

            for (Map.Entry<Integer, TestCaseRequest> entry : testCaseRequestList.entrySet()) {
                if (!Utils.isNotNullAndEmpty(entry.getValue().getName())) {
                    testCasesService.addToErrorMessages(errorMessages, statusCodeBundle.getTestCaseNameEmptyMessage(), entry.getKey());
                } else if (!Utils.checkRagexBeforeAfterWordsTestCases(entry.getValue().getName()))
                    testCasesService.addToErrorMessages(errorMessages, statusCodeBundle.getSpacesNotAllowedMessage(), entry.getKey());
                else if (testCasesNames.contains(entry.getValue().getName())) {
                    testCasesService.addToErrorMessages(errorMessages, statusCodeBundle.getTestCaseNameDuplicateMessage(), entry.getKey());
                } else {
                    testCasesNames.add(entry.getValue().getName());
                }
                if (!Utils.isNotNullAndEmpty(entry.getValue().getSubModuleName())) {
                    testCasesService.addToErrorMessages(errorMessages, statusCodeBundle.getTestcaseSubModuleIdEmptyMessage(), entry.getKey());
                } else if (!subModulesService.existsBySubModulesNameForProject(entry.getValue().getSubModuleName(), projectId)) {
                    testCasesService.addToErrorMessages(errorMessages, statusCodeBundle.getSubModuleNotExistsMessage(), entry.getKey());
                } else if (testCasesService.existsByTestCasesName(entry.getValue().getName(), subModulesService.getSubModuleIdByNameForProject(entry.getValue().getSubModuleName(), projectId))) {
                    testCasesService.addToErrorMessages(errorMessages, statusCodeBundle.getTestCaseNameAlreadyExistsMessage(), entry.getKey());
                }
            }
            if (!errorMessages.isEmpty()) {
                return ResponseEntity.ok(new FileResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getTestCaseFileErrorMessage(), errorMessages));
            } else if (testCaseRequestList.isEmpty()) {
                return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(),
                        statusCodeBundle.getTestcaseFileEmptyMessage()));
            } else {
                for (Map.Entry<Integer, TestCaseRequest> entry : testCaseRequestList.entrySet()) {

                    testCasesService.saveTestCase(entry.getValue());
                }
                return ResponseEntity.ok(new BaseResponse(RequestStatus.SUCCESS.getStatus(), statusCodeBundle.getCommonSuccessCode(), statusCodeBundle.getSaveTestCaseSuccessMessage()));
            }
        } catch (IOException e) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getTestCaseValidationSaveMessage()));
        }
    }

    @GetMapping(value = EndpointURI.TESTCASES_BY_ID)
    public ResponseEntity<Object> getAllTestCaseBySubModuleId(@PathVariable Long id) {
        if (!subModulesService.existsBySubModuleId(id)) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getSubModulesNotExistCode(), statusCodeBundle.getSubModuleNotExistsMessage()));
        }
        List<TestCaseResponse> testCaseResponseList = testCasesService.getAllTestCaseBySubModuleId(id);
        if (testCaseResponseList.isEmpty()) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getGetTestCaseNotHaveSubModuleIdMessage()));
        }
        return ResponseEntity.ok(new ContentResponse<>(Constants.TESTCASES, testCasesService.getAllTestCaseBySubModuleId(id), RequestStatus.SUCCESS.getStatus(), statusCodeBundle.getCommonSuccessCode(), statusCodeBundle.getGetTestCaseBySubModuleIdSuccessMessage()));

    }

    @GetMapping(value = EndpointURI.TESTCASE_BY_MAIN_MODULE_ID)
    public ResponseEntity<Object> getAllTestCasesByMainModuleId(@PathVariable Long id) {
        if (!mainModulesService.isExistMainModulesId(id)) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(),
                    statusCodeBundle.getMainModulesNotExistCode(), statusCodeBundle.getMainModuleNotExistsMessage()));
        }
        List<TestCaseResponse> testCaseResponseList = testCasesService.getAllTestCasesByMainModuleId(id);
        if (testCaseResponseList.isEmpty()) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getGetTestCaseNotHaveMainModuleId()));
        }
        return ResponseEntity.ok(new ContentResponse<>(Constants.TESTCASES, testCasesService.getAllTestCasesByMainModuleId(id), RequestStatus.SUCCESS.getStatus(), statusCodeBundle.getCommonSuccessCode(), statusCodeBundle.getGetAllTestCasesSuccessMainModuleIdMessage()));
    }

    @GetMapping(value = EndpointURI.TESTCASE_BY_MODULE_ID)
    public ResponseEntity<Object> getAllTestCasesByModuleId(@PathVariable Long id) {
        if (!modulesService.existsByModulesId(id)) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getModuleNotExistsCode(),
                    statusCodeBundle.getModuleNotExistsMessage()));
        }
        List<TestCaseResponse> testCaseResponseList = testCasesService.getAllTestCasesByModuleId(id);
        if (testCaseResponseList.isEmpty()) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getGetTestCasesNotHaveModuleIdMessage()));
        }
        return ResponseEntity.ok(new ContentResponse<>(Constants.TESTCASES, testCasesService.getAllTestCasesByModuleId(id),
                RequestStatus.SUCCESS.getStatus(), statusCodeBundle.getCommonSuccessCode(), statusCodeBundle.getGetTestCasesByModuleIdSuccessMessage()));
    }

    @GetMapping(value = EndpointURI.TESTCASE_BY_PROJECT_ID)
    public ResponseEntity<Object> getAllTestCasesByProjectIdWithPagination(@PathVariable Long id,
                                                                           @RequestParam(name = "page") int page,
                                                                           @RequestParam(name = "size") int size, @RequestParam(name = "direction") String direction,
                                                                           @RequestParam(name = "sortField") String sortField) {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.valueOf(direction), sortField);
        PaginatedContentResponse.Pagination pagination = new PaginatedContentResponse.Pagination(page, size, 0, 0L);
        if (!projectService.existByProjectId(id)) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(),
                    statusCodeBundle.getProjectNotExistCode(), statusCodeBundle.getProjectNotExistsMessage()));
        }
        if (!testCasesService.existsTestCaseByProjectId(id)) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(),
                    statusCodeBundle.getFailureCode(), statusCodeBundle.getGetTestCaseNotHaveProjectId()));
        }
        return ResponseEntity.ok(new PaginatedContentResponse<>(Constants.TESTCASES, testCasesService.getAllTestcasesByProjectIdWithPagination(id, pageable, pagination),
                RequestStatus.SUCCESS.getStatus(), statusCodeBundle.getCommonSuccessCode(),
                statusCodeBundle.getGetAllTestCasesSuccessGivenProjectId(), pagination));
    }
    @DeleteMapping(value = EndpointURI.TESTCASES_BY_IDS)
   public ResponseEntity<Object> deleteTestCasesByIds(@RequestBody TestCaseRequest testCaseRequest) {

        for (Long id: testCaseRequest.getTestCaseIds())
        {
            if (testGroupingService.existsByTestCasesId(id)) continue;
            testCasesService.DeleteTestCaseById(id);
        }
        return ResponseEntity.ok(new BaseResponse(
                    RequestStatus.SUCCESS.getStatus(),
                    statusCodeBundle.getCommonSuccessCode(),
                    statusCodeBundle.getOnlyDeleteIndependentTestCasesSuccessfullyMessage()));
    }
    }