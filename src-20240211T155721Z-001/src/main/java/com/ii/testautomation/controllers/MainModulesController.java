package com.ii.testautomation.controllers;

import com.ii.testautomation.dto.request.MainModulesRequest;
import com.ii.testautomation.dto.search.MainModuleSearch;
import com.ii.testautomation.enums.RequestStatus;
import com.ii.testautomation.response.common.BaseResponse;
import com.ii.testautomation.response.common.ContentResponse;
import com.ii.testautomation.response.common.FileResponse;
import com.ii.testautomation.response.common.PaginatedContentResponse;
import com.ii.testautomation.service.MainModulesService;
import com.ii.testautomation.service.ModulesService;
import com.ii.testautomation.service.ProjectService;
import com.ii.testautomation.service.SubModulesService;
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
public class MainModulesController {

    @Autowired
    private MainModulesService mainModulesService;
    @Autowired
    private StatusCodeBundle statusCodeBundle;
    @Autowired
    private SubModulesService subModulesService;
    @Autowired
    private ModulesService modulesService;
    @Autowired
    private ProjectService projectService;

    @PostMapping(EndpointURI.MAIN_MODULE)
    public ResponseEntity<Object> insertMainModules(@RequestBody MainModulesRequest mainModulesRequest) {
        if (!Utils.checkRegexBeforeAfterWords(mainModulesRequest.getName()))
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getSpacesNotAllowedMessage()));
        if (!mainModulesService.isExistModulesId(mainModulesRequest.getModuleId()))
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getModuleIdNotFound()));
        if (mainModulesService.isExistMainModulesName(mainModulesRequest.getName(), mainModulesRequest.getModuleId()))
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getAlreadyExistCode(), statusCodeBundle.getMainModulesNameAlreadyExistMessage()));
        if (!Utils.checkRegexBeforeAfterWords(mainModulesRequest.getPrefix()))
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getSpacesNotAllowedMessage()));
        if (mainModulesService.isExistPrefix(mainModulesRequest.getPrefix(), mainModulesRequest.getModuleId()))
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getAlreadyExistCode(), statusCodeBundle.getMainModulesPrefixAlreadyExistMessage()));
        mainModulesService.saveMainModules(mainModulesRequest);
        return ResponseEntity.ok(new BaseResponse(RequestStatus.SUCCESS.getStatus(), statusCodeBundle.getCommonSuccessCode(), statusCodeBundle.getSuccessMessageInsertMainModules()));
    }

    @DeleteMapping(EndpointURI.MAIN_MODULE_BY_ID)
    public ResponseEntity<Object> deleteMainModules(@PathVariable Long id) {
        if (!mainModulesService.isExistMainModulesId(id))
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getMainModulesIdNotFound()));

        if (subModulesService.existsByMainModuleId(id))
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getIdAssignedWithAnotherTableCode(), statusCodeBundle.getIdAssignedWithAnotherTable()));

        mainModulesService.deleteMainModules(id);
        return ResponseEntity.ok(new BaseResponse(RequestStatus.SUCCESS.getStatus(), statusCodeBundle.getCommonSuccessCode(), statusCodeBundle.getSuccessMessageDeleteMainModules()));
    }

    @PutMapping(EndpointURI.MAIN_MODULE)
    public ResponseEntity<Object> updateMainModules(@RequestBody MainModulesRequest mainModulesRequest) {
        if (!mainModulesService.isExistMainModulesId(mainModulesRequest.getId()))
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getMainModulesIdNotFound()));
        if (!mainModulesService.isExistModulesId(mainModulesRequest.getModuleId()))
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getModuleIdNotFound()));
        if (!Utils.checkRegexBeforeAfterWords(mainModulesRequest.getName()))
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getSpacesNotAllowedMessage()));
        if (mainModulesService.isUpdateMainModulesNameExist(mainModulesRequest.getName(), mainModulesRequest.getId()))
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getAlreadyExistCode(), statusCodeBundle.getMainModulesNameAlreadyExistMessage()));
        if (!Utils.checkRegexBeforeAfterWords(mainModulesRequest.getPrefix()))
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getSpacesNotAllowedMessage()));
        if (mainModulesService.isUpdateMainModulesPrefixExist(mainModulesRequest.getPrefix(), mainModulesRequest.getId()))
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getAlreadyExistCode(), statusCodeBundle.getMainModulesPrefixAlreadyExistMessage()));

        mainModulesService.saveMainModules(mainModulesRequest);
        return ResponseEntity.ok(new BaseResponse(RequestStatus.SUCCESS.getStatus(), statusCodeBundle.getCommonSuccessCode(), statusCodeBundle.getSuccessUpdateMessageMainModules()));
    }

    @GetMapping(EndpointURI.MAIN_MODULE_BY_ID)
    public ResponseEntity<Object> getMainModulesByMainModuleId(@PathVariable Long id) {
        if (!mainModulesService.isExistMainModulesId(id))
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getMainModulesIdNotFound()));
        return ResponseEntity.ok(new ContentResponse<>(Constants.MAINMODULES, mainModulesService.getByMainModulesId(id), RequestStatus.SUCCESS.getStatus(), statusCodeBundle.getCommonSuccessCode(), statusCodeBundle.getSuccessViewAllMessageMainModules()));
    }

    @GetMapping(EndpointURI.MAIN_MODULE_BY_MODULE_ID)
    public ResponseEntity<Object> getMainModulesByModuleId(@PathVariable Long id) {
        if (!mainModulesService.isExistModulesId(id))
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getModuleIdNotFound()));
        if (mainModulesService.getMainModulesByModuleId(id).isEmpty())
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getModuleIdNotAssigned()));

        return ResponseEntity.ok(new ContentResponse<>(Constants.MAINMODULES, mainModulesService.getMainModulesByModuleId(id), RequestStatus.SUCCESS.getStatus(), statusCodeBundle.getCommonSuccessCode(), statusCodeBundle.getSuccessViewAllMessageMainModules()));
    }

    @GetMapping(EndpointURI.MAIN_MODULE_BY_PROJECT_ID)
    public ResponseEntity<Object> getMainModulesByProjectId(@RequestParam(name = "page") int page, @RequestParam(name = "size") int size, @RequestParam(name = "direction") String direction, @RequestParam(name = "sortField") String sortField, @PathVariable Long id) {
        if (!projectService.existByProjectId(id))
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getProjectNotExistCode(), statusCodeBundle.getProjectNotExistsMessage()));
        if (!mainModulesService.isExistMainModulesByProjectId(id))
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getProjectNotExistCode(), statusCodeBundle.getMainModulesNotMappedWithProjectMessage()));
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.valueOf(direction), sortField);
        PaginatedContentResponse.Pagination pagination = new PaginatedContentResponse.Pagination(page, size, 0, 0l);
        return ResponseEntity.ok(new PaginatedContentResponse<>(Constants.MAINMODULES, mainModulesService.getMainModulesByProjectId(pageable, pagination, id), RequestStatus.SUCCESS.getStatus(), statusCodeBundle.getCommonSuccessCode(), statusCodeBundle.getSuccessViewAllMessageMainModules(), pagination));
    }

    @GetMapping(EndpointURI.MAIN_MODULE_PAGE)
    public ResponseEntity<Object> SearchMainModulesWithPage(@RequestParam(name = "page") int page, @RequestParam(name = "size") int size, @RequestParam(name = "direction") String direction, @RequestParam(name = "sortField") String sortField, MainModuleSearch mainModuleSearch) {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.valueOf(direction), sortField);
        PaginatedContentResponse.Pagination pagination = new PaginatedContentResponse.Pagination(page, size, 0, 0l);
        return ResponseEntity.ok(new ContentResponse<>(Constants.MAINMODULES, mainModulesService.SearchMainModulesWithPagination(pageable, pagination, mainModuleSearch), RequestStatus.SUCCESS.getStatus(), statusCodeBundle.getCommonSuccessCode(), statusCodeBundle.getSuccessViewAllMessageMainModules()));
    }

    @PostMapping(EndpointURI.MAIN_MODULE_IMPORT)
    public ResponseEntity<Object> importMainModules(@RequestParam MultipartFile multipartFile) {
        Map<String, List<Integer>> errorMessages = new HashMap<>();
        Set<String> mainModuleNames = new HashSet<>();
        Set<String> mainModulePrefixes = new HashSet<>();
        Map<Integer, MainModulesRequest> mainModulesRequestList;
        try {
            if (!mainModulesService.isCSVHeaderMatch(multipartFile) && !mainModulesService.isExcelHeaderMatch(multipartFile)) {
                return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFileFailureCode(), statusCodeBundle.getHeaderNotExistsMessage()));
            }
            if (Objects.requireNonNull(multipartFile.getOriginalFilename()).endsWith(".csv")) {
                mainModulesRequestList = mainModulesService.csvProcess(multipartFile.getInputStream());
            } else if (mainModulesService.hasExcelFormat(multipartFile)) {
                mainModulesRequestList = mainModulesService.excelProcess(multipartFile);
            } else {
                return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFileFailureCode(), statusCodeBundle.getFileFailureMessage()));
            }
            for (Map.Entry<Integer, MainModulesRequest> entry : mainModulesRequestList.entrySet()) {
                if (!Utils.isNotNullAndEmpty(entry.getValue().getName())) {
                    mainModulesService.addToErrorMessages(errorMessages, statusCodeBundle.getMainModulesNameFiledEmptyMessage(), entry.getKey());
                } else if (mainModuleNames.contains(entry.getValue().getName())) {
                    mainModulesService.addToErrorMessages(errorMessages, statusCodeBundle.getMainModulesNameDuplicateMessage(), entry.getKey());
                } else {
                    mainModuleNames.add(entry.getValue().getPrefix());
                }
                if (!Utils.isNotNullAndEmpty(entry.getValue().getPrefix())) {
                    mainModulesService.addToErrorMessages(errorMessages, statusCodeBundle.getMainModulesPrefixFiledEmptyMessage(), entry.getKey());
                } else if (mainModuleNames.contains(entry.getValue().getPrefix())) {
                    mainModulesService.addToErrorMessages(errorMessages, statusCodeBundle.getMainModulesPrefixDuplicateMessage(), entry.getKey());
                } else {
                    mainModulePrefixes.add(entry.getValue().getPrefix());
                }
                if (mainModulesService.isExistMainModulesName(entry.getValue().getName(), entry.getValue().getModuleId())) {
                    mainModulesService.addToErrorMessages(errorMessages, statusCodeBundle.getMainModulesNameAlreadyExistMessage(), entry.getKey());
                }
                if (mainModulesService.isExistPrefix(entry.getValue().getPrefix(), entry.getValue().getModuleId())) {
                    mainModulesService.addToErrorMessages(errorMessages, statusCodeBundle.getMainModulesPrefixAlreadyExistMessage(), entry.getKey());
                }
            }
            if (!errorMessages.isEmpty()) {

                return ResponseEntity.ok(new FileResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getMainModulesNotSavedMessage(), errorMessages));
            } else {
                for (Map.Entry<Integer, MainModulesRequest> entry : mainModulesRequestList.entrySet()) {
                    mainModulesService.saveMainModules(entry.getValue());
                }
                return ResponseEntity.ok(new BaseResponse(RequestStatus.SUCCESS.getStatus(), statusCodeBundle.getCommonSuccessCode(), statusCodeBundle.getSuccessMessageInsertMainModules()));
            }
        } catch (IOException e) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getMainModulesNotSavedMessage()));
        }
    }
}
