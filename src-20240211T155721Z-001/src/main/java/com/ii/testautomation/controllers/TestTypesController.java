package com.ii.testautomation.controllers;

import com.ii.testautomation.dto.request.TestTypesRequest;
import com.ii.testautomation.dto.search.TestTypesSearch;
import com.ii.testautomation.enums.RequestStatus;
import com.ii.testautomation.response.common.BaseResponse;
import com.ii.testautomation.response.common.ContentResponse;
import com.ii.testautomation.response.common.FileResponse;
import com.ii.testautomation.response.common.PaginatedContentResponse;
import com.ii.testautomation.service.CompanyUserService;
import com.ii.testautomation.service.ProjectService;
import com.ii.testautomation.service.TestGroupingService;
import com.ii.testautomation.service.TestTypesService;
import com.ii.testautomation.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController
@CrossOrigin
public class TestTypesController {
    @Autowired
    private TestTypesService testTypesService;
    @Autowired
    private StatusCodeBundle statusCodeBundle;
    @Autowired
    private TestGroupingService testGroupingService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private CompanyUserService companyUserService;

    @PostMapping(EndpointURI.TEST_TYPE)
    public ResponseEntity<Object> insertTestTypes(@RequestBody TestTypesRequest testTypesRequest) {
        if (!Utils.checkRegexBeforeAfterWords(testTypesRequest.getName()))
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getSpacesNotAllowedMessage()));
        if (testTypesService.isExistsTestTypeByNameAndCompanyUserId(testTypesRequest.getName(),testTypesRequest.getCompanyUserId()))
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getAlreadyExistCode(), statusCodeBundle.getTestTypeNameAlReadyExistMessage()));
        if (!companyUserService.existsByCompanyUserId(testTypesRequest.getCompanyUserId()))
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getCompanyUserIdNotExistMessage()));
        testTypesService.saveTestTypes(testTypesRequest);
        return ResponseEntity.ok(new BaseResponse(RequestStatus.SUCCESS.getStatus(), statusCodeBundle.getCommonSuccessCode(), statusCodeBundle.getInsertTestTypesSuccessMessage()));
    }

    @PutMapping(EndpointURI.TEST_TYPE)
    public ResponseEntity<Object> updateTestTypes(@RequestBody TestTypesRequest testTypesRequest) {
        if (!testTypesService.isExistsTestTypeById(testTypesRequest.getId()))
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getTestTypeNotExistCode(), statusCodeBundle.getTestTypeIdNotFoundMessage()));
        if (!Utils.checkRegexBeforeAfterWords(testTypesRequest.getName()))
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getSpacesNotAllowedMessage()));

        if (testTypesService.isExistsTestTypesByNameIgnoreCaseAndCompanyUserIdAndIdNot(testTypesRequest.getName(), testTypesRequest.getCompanyUserId(), testTypesRequest.getId()))
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getTestTypeAlReadyExistCode(), statusCodeBundle.getTestTypeNameAlReadyExistMessage()));
        if (!companyUserService.existsByCompanyUserId(testTypesRequest.getCompanyUserId()))
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getCompanyUserIdNotExistMessage()));
        testTypesService.saveTestTypes(testTypesRequest);
        return ResponseEntity.ok(new BaseResponse(RequestStatus.SUCCESS.getStatus(), statusCodeBundle.getCommonSuccessCode(), statusCodeBundle.getUpdateTestTypeSuccessMessage()));
    }

    @DeleteMapping(EndpointURI.TEST_TYPE_BY_ID)
    public ResponseEntity<Object> deleteTestTypeById(@PathVariable Long id) {
        if (!testTypesService.isExistsTestTypeById(id))
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getTestTypeNotExistCode(), statusCodeBundle.getTestTypeIdNotFoundMessage()));
        if (testGroupingService.existsByTestTypesId(id))
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getTestTypeDependentCode(), statusCodeBundle.getTestTypeDependentMessage()));
        testTypesService.deleteTestTypeById(id);
        return ResponseEntity.ok(new BaseResponse(RequestStatus.SUCCESS.getStatus(), statusCodeBundle.getCommonSuccessCode(), statusCodeBundle.getDeleteTestTypesSuccessMessage()));
    }

    @GetMapping(EndpointURI.TEST_TYPE_BY_ID)
    public ResponseEntity<Object> getTestTypeById(@PathVariable Long id) {
        if (!testTypesService.isExistsTestTypeById(id))
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getTestTypeNotExistCode(), statusCodeBundle.getTestTypeIdNotFoundMessage()));

        return ResponseEntity.ok(new ContentResponse<>(Constants.TESTTYPE, testTypesService.getTestTypeById(id), RequestStatus.SUCCESS.getStatus(), statusCodeBundle.getCommonSuccessCode(), statusCodeBundle.getViewTestTypeforIdSuccessMessage()));
    }

    @GetMapping(EndpointURI.TEST_TYPE_BY_PROJECT_ID)
    public ResponseEntity<Object> getTestTypeByProjectId(@PathVariable Long id) {
        if (!projectService.existByProjectId(id))
            return ResponseEntity.ok(new BaseResponse(RequestStatus.UNKNOWN.getStatus(), statusCodeBundle.getProjectNotExistCode(), statusCodeBundle.getProjectNotExistsMessage()));
        else if (testTypesService.getTestTypesByProjectId(id).isEmpty())
            return ResponseEntity.ok(new BaseResponse(RequestStatus.ERROR.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getTestTypeNotMappedMessage()));
        else
            return ResponseEntity.ok(new ContentResponse<>(Constants.TESTTYPES, testTypesService.getTestTypesByProjectId(id), statusCodeBundle.getCommonSuccessCode(), RequestStatus.SUCCESS.getStatus(), statusCodeBundle.getViewTestTypeByProjectIdSuccessMessage()));
    }

    @GetMapping(EndpointURI.TEST_TYPES_SEARCH)
    public ResponseEntity<Object> SearchTestTypesWithPagination(@RequestParam(name = "page") int page, @RequestParam(name = "size") int size, @RequestParam(name = "direction") String direction, @RequestParam(name = "sortField") String sortField, TestTypesSearch testTypesSearch) {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.valueOf(direction), sortField);
        PaginatedContentResponse.Pagination pagination = new PaginatedContentResponse.Pagination(page, size, 0, 0l);

        return ResponseEntity.ok(new ContentResponse<>(Constants.TESTTYPES, testTypesService.SearchTestTypesWithPagination(pageable, pagination, testTypesSearch), RequestStatus.SUCCESS.getStatus(), statusCodeBundle.getCommonSuccessCode(), statusCodeBundle.getSuccessViewAllMessageMainModules()));
    }

    @PostMapping(EndpointURI.TEST_TYPE_IMPORT)
    public ResponseEntity<Object> importTestTypes(@RequestParam MultipartFile multipartFile) {

        Map<String, List<Integer>> errorMessages = new HashMap<>();
        Map<Integer, TestTypesRequest> testTypesRequestList;
        Set<String> testTypeNames = new HashSet<>();

        try {
            if (!projectService.isCSVHeaderMatch(multipartFile) && !projectService.isExcelHeaderMatch(multipartFile)) {
                return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFileFailureCode(), statusCodeBundle.getHeaderNotExistsMessage()));
            }
            if (Objects.requireNonNull(multipartFile.getOriginalFilename()).endsWith(".csv")) {
                testTypesRequestList = testTypesService.csvProcess(multipartFile.getInputStream());
            } else if (projectService.hasExcelFormat(multipartFile)) {
                testTypesRequestList = testTypesService.excelProcess(multipartFile);
            } else {
                return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFileFailureCode(), statusCodeBundle.getFileFailureMessage()));
            }
            for (Map.Entry<Integer, TestTypesRequest> entry : testTypesRequestList.entrySet()) {
                if (!Utils.isNotNullAndEmpty(entry.getValue().getName())) {
                    projectService.addToErrorMessages(errorMessages, statusCodeBundle.getProjectNameEmptyMessage(), entry.getKey());
                } else if (testTypeNames.contains(entry.getValue().getName())) {
                    projectService.addToErrorMessages(errorMessages, statusCodeBundle.getProjectNameDuplicateMessage(), entry.getKey());
                } else {
                    testTypeNames.add(entry.getValue().getName());
                }
                if (!Utils.isNotNullAndEmpty(entry.getValue().getDescription())) {
                    projectService.addToErrorMessages(errorMessages, statusCodeBundle.getProjectDescriptionEmptyMessage(), entry.getKey());
                }
                if (!Utils.isNotNullAndEmpty(entry.getValue().getDescription())) {
                    testTypesService.addToErrorMessages(errorMessages, statusCodeBundle.getTestTypeDescriptionEmptyMessage(), entry.getKey());
                }
            }

            if (!errorMessages.isEmpty()) {
                return ResponseEntity.ok(new FileResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getTestTypesNotSavedMessage(), errorMessages));
            } else {
                for (Map.Entry<Integer, TestTypesRequest> entry : testTypesRequestList.entrySet()) {
                    testTypesService.saveTestTypes(entry.getValue());
                }
                return ResponseEntity.ok(new BaseResponse(RequestStatus.SUCCESS.getStatus(), statusCodeBundle.getCommonSuccessCode(), statusCodeBundle.getInsertTestTypesSuccessMessage()));
            }
        } catch (IOException e) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getFileFailureMessage()));
        }
    }

    @GetMapping(EndpointURI.TEST_TYPE_BY_COMPANYUSER_ID)
    public ResponseEntity<Object> getTestTypeByCompanyUserId(@PathVariable Long id) {
        if (!testTypesService.isExistCompanyUserId(id))
            return ResponseEntity.ok(new BaseResponse((RequestStatus.FAILURE.getStatus()), statusCodeBundle.getFailureCode(), statusCodeBundle.getCompanyUserIdNotExistMessage()));

        return ResponseEntity.ok(new ContentResponse<>(Constants.TESTTYPES, testTypesService.getTestTypesByCompanyUserId(id), RequestStatus.SUCCESS.getStatus(), statusCodeBundle.getCommonSuccessCode(), statusCodeBundle.getViewTestTypeByCompanyUSerIdSuccessMessage()));
    }
}