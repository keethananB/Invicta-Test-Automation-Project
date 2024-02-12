package com.ii.testautomation.controllers;

import com.ii.testautomation.dto.request.ModulesRequest;
import com.ii.testautomation.dto.response.ModulesResponse;
import com.ii.testautomation.dto.response.ProjectModuleResponse;
import com.ii.testautomation.dto.search.ModuleSearch;
import com.ii.testautomation.enums.RequestStatus;
import com.ii.testautomation.response.common.BaseResponse;
import com.ii.testautomation.response.common.ContentResponse;
import com.ii.testautomation.response.common.FileResponse;
import com.ii.testautomation.response.common.PaginatedContentResponse;
import com.ii.testautomation.service.MainModulesService;
import com.ii.testautomation.service.ModulesService;
import com.ii.testautomation.service.ProjectService;
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
import java.io.InputStream;
import java.util.*;

@RestController
@CrossOrigin
public class ModulesController {
    @Autowired
    private ModulesService modulesService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private MainModulesService mainModulesService;
    @Autowired
    private StatusCodeBundle statusCodeBundle;

    @PostMapping(value = EndpointURI.MODULE)
    public ResponseEntity<Object> saveModule(@RequestBody ModulesRequest modulesRequest) {
        if (!Utils.checkRegexBeforeAfterWords(modulesRequest.getName()))
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getSpacesNotAllowedMessage()));
        if (modulesService.isModuleExistsByName(modulesRequest.getName(), modulesRequest.getProject_id())) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getModuleAlReadyExistsCode(), statusCodeBundle.getModuleNameAlReadyExistsMessage()));
        }
        if (modulesService.isModuleExistsByPrefix(modulesRequest.getPrefix(), modulesRequest.getProject_id())) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getModuleAlReadyExistsCode(), statusCodeBundle.getModulePrefixAlReadyExistsMessage()));
        }
        if (!projectService.existByProjectId(modulesRequest.getProject_id())) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getProjectNotExistCode(), statusCodeBundle.getProjectNotExistsMessage()));
        }
        modulesService.saveModule(modulesRequest);
        return ResponseEntity.ok(new BaseResponse(RequestStatus.SUCCESS.getStatus(), statusCodeBundle.getCommonSuccessCode(), statusCodeBundle.getSaveModuleSuccessMessage()));
    }

    @PutMapping(value = EndpointURI.MODULE)
    public ResponseEntity<Object> UpdateModule(@RequestBody ModulesRequest modulesRequest) {
        if (!modulesService.existsByModulesId(modulesRequest.getId())) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getModuleNotExistsCode(), statusCodeBundle.getModuleNotExistsMessage()));
        }
        if (!Utils.checkRegexBeforeAfterWords(modulesRequest.getName()))
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getSpacesNotAllowedMessage()));
        if (modulesService.isUpdateModuleNameExists(modulesRequest.getName(), modulesRequest.getId())) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(),
                    statusCodeBundle.getModuleAlReadyExistsCode(),
                    statusCodeBundle.getModuleNameAlReadyExistsMessage()));
        }
        if (modulesService.isUpdateModulePrefixExists(modulesRequest.getPrefix(), modulesRequest.getId())) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getModuleAlReadyExistsCode(), statusCodeBundle.getModulePrefixAlReadyExistsMessage()));
        }
        if (!projectService.existByProjectId(modulesRequest.getProject_id())) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getProjectNotExistCode(), statusCodeBundle.getProjectNotExistsMessage()));
        }
        modulesService.saveModule(modulesRequest);
        return ResponseEntity.ok(new BaseResponse(RequestStatus.SUCCESS.getStatus(), statusCodeBundle.getCommonSuccessCode(), statusCodeBundle.getUpdateModuleSuccessMessage()));
    }

    @DeleteMapping(value = EndpointURI.MODULE_BY_ID)
    public ResponseEntity<Object> deleteModuleById(@PathVariable Long id) {
        if (!modulesService.existsByModulesId(id)) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getModuleNotExistsCode(), statusCodeBundle.getModuleNotExistsMessage()));
        }
        if (mainModulesService.existsMainModuleByModuleId(id)) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getModuleDependentCode(), statusCodeBundle.getGetValidationModuleAssignedMessage()));
        }
        modulesService.deleteModuleById(id);
        return ResponseEntity.ok(new BaseResponse(RequestStatus.SUCCESS.getStatus(), statusCodeBundle.getCommonSuccessCode(), statusCodeBundle.getDeleteModuleSuccessMessage()));
    }

    @GetMapping(value = EndpointURI.MODULES)
    public ResponseEntity<Object> getAllModules(@RequestParam(name = "page") int page, @RequestParam(name = "size") int size, @RequestParam(name = "direction") String direction, @RequestParam(name = "sortField") String sortField, ModuleSearch moduleSearch) {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.valueOf(direction), sortField);
        PaginatedContentResponse.Pagination pagination = new PaginatedContentResponse.Pagination(page, size, 0, 0L);
        return ResponseEntity.ok(new ContentResponse<>(Constants.MODULES, modulesService.multiSearchModules(pageable, pagination, moduleSearch), RequestStatus.SUCCESS.getStatus(), statusCodeBundle.getCommonSuccessCode(), statusCodeBundle.getGetAllModuleSuccessMessage()));
    }

    @GetMapping(value = EndpointURI.MODULE_BY_ID)
    public ResponseEntity<Object> getModuleById(@PathVariable Long id) {
        if (!modulesService.existsByModulesId(id)) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getModuleNotExistsCode(), statusCodeBundle.getModuleNotExistsMessage()));
        }
        return ResponseEntity.ok(new ContentResponse<>(Constants.MODULE, modulesService.getModuleById(id),
                RequestStatus.SUCCESS.getStatus(), statusCodeBundle.getCommonSuccessCode(), statusCodeBundle.getGetModuleByIdSuccessMessage()));
    }

    @GetMapping(value = EndpointURI.MODULES_BY_ID)
    public ResponseEntity<BaseResponse> getAllModulesByProjectIdWithPagination(@PathVariable Long id,
                                                                               @RequestParam(name = "page") int page,
                                                                               @RequestParam(name = "size") int size,
                                                                               @RequestParam(name = "direction") String direction,
                                                                               @RequestParam(name = "sortField") String sortField) {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.valueOf(direction), sortField);
        PaginatedContentResponse.Pagination pagination = new PaginatedContentResponse.Pagination(page, size, 0, 0L);
        if (!projectService.existByProjectId(id)) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getProjectNotExistCode(), statusCodeBundle.getProjectNotExistsMessage()));
        }

        List<ModulesResponse> modulesResponseList = modulesService.getAllModuleByProjectIdWithPagination(id, pageable, pagination);
        if (modulesResponseList.isEmpty()) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getModuleNotHaveProjectMessage()));
        }
        return ResponseEntity.ok(new PaginatedContentResponse<>(Constants.MODULES, modulesResponseList,
                RequestStatus.SUCCESS.getStatus(), statusCodeBundle.getCommonSuccessCode(), statusCodeBundle.getGetModuleByProjectIdSuccessMessage(), pagination));
    }

    @GetMapping(value = EndpointURI.MODULE_BY_PROJECT_ID)
    public ResponseEntity<Object> getAllModulesByProjectIdAndSearch(@PathVariable Long id, @RequestParam String testCaseName) {
        if (!projectService.existByProjectId(id)) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getProjectNotExistCode(), statusCodeBundle.getProjectNotExistsMessage()));
        }
        ProjectModuleResponse projectModuleResponse = modulesService.getAllByProjectIdAndSearch(id, testCaseName);
        return ResponseEntity.ok(new ContentResponse<>(Constants.MODULES, projectModuleResponse, RequestStatus.SUCCESS.getStatus(), statusCodeBundle.getCommonSuccessCode(), statusCodeBundle.getGetAllModulesByProjectId()));
    }

    @PostMapping(value = EndpointURI.MODULE_IMPORT)
    public ResponseEntity<Object> importModuleFile(@RequestParam MultipartFile multipartFile) {
        Map<String, List<Integer>> errorMessages = new HashMap<>();
        Map<Integer, ModulesRequest> modulesRequestList;
        Set<String> modulesNames = new HashSet<>();
        Set<String> modulesPrefix = new HashSet<>();
        try (InputStream inputStream = multipartFile.getInputStream()) {
            if (!modulesService.isCSVHeaderMatch(multipartFile) && (!modulesService.isExcelHeaderMatch(multipartFile))) {
                return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getHeaderNotExistsMessage()));
            }
            if (Objects.requireNonNull(multipartFile.getOriginalFilename()).endsWith(".csv")) {
                modulesRequestList = modulesService.csvToModulesRequest(inputStream);
            } else if (modulesService.hasExcelFormat(multipartFile)) {
                modulesRequestList = modulesService.excelToModuleRequest(multipartFile);
            } else {
                return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFileFailureCode(), statusCodeBundle.getFileFailureMessage()));
            }
            for (Map.Entry<Integer, ModulesRequest> entry : modulesRequestList.entrySet()) {
                if (!Utils.isNotNullAndEmpty(entry.getValue().getName())) {
                    modulesService.addToErrorMessages(errorMessages, statusCodeBundle.getModuleNameEmptyMessage(), entry.getKey());
                } else if (modulesNames.contains(entry.getValue().getName())) {
                    modulesService.addToErrorMessages(errorMessages, statusCodeBundle.getModuleNameDuplicateMessage(), entry.getKey());
                } else {
                    modulesNames.add(entry.getValue().getName());
                }

                if (!Utils.isNotNullAndEmpty(entry.getValue().getPrefix())) {
                    modulesService.addToErrorMessages(errorMessages, statusCodeBundle.getModulePrefixEmptyMessage(), entry.getKey());
                } else if (modulesPrefix.contains(entry.getValue().getPrefix())) {
                    modulesService.addToErrorMessages(errorMessages, statusCodeBundle.getModulePrefixDuplicateMessage(), entry.getKey());
                } else {
                    modulesPrefix.add(entry.getValue().getPrefix());
                }
                if (modulesService.isModuleExistsByName(entry.getValue().getName(), entry.getValue().getProject_id())) {
                    modulesService.addToErrorMessages(errorMessages, statusCodeBundle.getModuleNameAlReadyExistsMessage(), entry.getKey());
                }
                if (modulesService.isModuleExistsByPrefix(entry.getValue().getPrefix(), entry.getValue().getProject_id())) {
                    modulesService.addToErrorMessages(errorMessages, statusCodeBundle.getModulePrefixAlReadyExistsMessage(), entry.getKey());
                }
                if (entry.getValue().getProject_id() == null) {
                    modulesService.addToErrorMessages(errorMessages, statusCodeBundle.getModuleProjectIdEmptyMessage(), entry.getKey());
                } else if (!projectService.existByProjectId(entry.getValue().getProject_id())) {
                    modulesService.addToErrorMessages(errorMessages, statusCodeBundle.getProjectNotExistsMessage(), entry.getKey());
                }
            }
            if (!errorMessages.isEmpty()) {
                return ResponseEntity.ok(new FileResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getModuleFileErrorMessage(), errorMessages));
            } else if (modulesRequestList.isEmpty()) {
                return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(),
                        statusCodeBundle.getFileFailureCode(), statusCodeBundle.getModuleFileEmptyMessage()));
            } else {
                for (Map.Entry<Integer, ModulesRequest> entry : modulesRequestList.entrySet()) {
                    modulesService.saveModule(entry.getValue());
                }
                return ResponseEntity.ok(new BaseResponse(RequestStatus.SUCCESS.getStatus(), statusCodeBundle.getCommonSuccessCode(), statusCodeBundle.getSaveModuleSuccessMessage()));
            }
        } catch (IOException e) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getSaveModuleValidationMessage()));
        }
    }
}