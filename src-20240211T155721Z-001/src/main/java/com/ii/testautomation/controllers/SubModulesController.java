package com.ii.testautomation.controllers;

import com.ii.testautomation.dto.request.SubModulesRequest;
import com.ii.testautomation.dto.search.SubModuleSearch;
import com.ii.testautomation.enums.RequestStatus;
import com.ii.testautomation.response.common.BaseResponse;
import com.ii.testautomation.response.common.ContentResponse;
import com.ii.testautomation.response.common.FileResponse;
import com.ii.testautomation.response.common.PaginatedContentResponse;
import com.ii.testautomation.service.MainModulesService;
import com.ii.testautomation.service.ProjectService;
import com.ii.testautomation.service.SubModulesService;
import com.ii.testautomation.service.TestCasesService;
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
public class SubModulesController {
    @Autowired
    private SubModulesService subModulesService;
    @Autowired
    private MainModulesService mainModulesService;
    @Autowired
    private TestCasesService testCasesService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private StatusCodeBundle statusCodeBundle;

    @PostMapping(value = EndpointURI.SUBMODULE)
    public ResponseEntity<Object> saveSubModules(@RequestBody SubModulesRequest subModulesRequest) {
        if (!mainModulesService.isExistMainModulesId(subModulesRequest.getMain_module_Id())) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(),
                    statusCodeBundle.getMainModulesNotExistCode(),
                    statusCodeBundle.getMainModuleNotExistsMessage()));
        }
        if (!Utils.checkRegexBeforeAfterWords(subModulesRequest.getName()))
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getSpacesNotAllowedMessage()));
       if(subModulesRequest.getName()==null || subModulesRequest.getName().isEmpty() ){
           return ResponseEntity.ok(new BaseResponse(RequestStatus.ERROR.getStatus(),statusCodeBundle.getNullValuesCode(),
                   statusCodeBundle.getSubModuleNameEmptyMessage()));
       }
        if (subModulesService.existsBySubModulesName(subModulesRequest.getName(), subModulesRequest.getMain_module_Id())) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(),
                    statusCodeBundle.getSubModulesAlReadyExistCode(),
                    statusCodeBundle.getSubModuleNameAlReadyExistMessage()));
        }
        if (!Utils.checkRegexBeforeAfterWords(subModulesRequest.getPrefix()))
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getSpacesNotAllowedMessage()));

        if (subModulesService.existsBySubModulesPrefix(subModulesRequest.getPrefix(), subModulesRequest.getMain_module_Id())) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(),
                    statusCodeBundle.getSubModulesAlReadyExistCode(),
                    statusCodeBundle.getSubModulePrefixAlReadyExistMessage()));
        }
        subModulesService.saveSubModules(subModulesRequest);
        return ResponseEntity.ok(new BaseResponse(RequestStatus.SUCCESS.getStatus(),
                statusCodeBundle.getCommonSuccessCode(),
                statusCodeBundle.getSaveSubModuleSuccessMessage()));
    }

    @PostMapping(value = EndpointURI.SUBMODULE_IMPORT)
    public ResponseEntity<Object> importSubModuleFile(@RequestParam MultipartFile multipartFile) {
        Map<String, List<Integer>> errorMessages = new HashMap<>();
        Map<Integer, SubModulesRequest> subModulesRequestList;
        Set<String> subModuleNames = new HashSet<>();
        Set<String> subModulePrefixes = new HashSet<>();
        try {
            if (Objects.requireNonNull(multipartFile.getOriginalFilename()).endsWith(".csv")) {
                if (!subModulesService.isCSVHeaderMatch(multipartFile)) {
                    return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFileFailureCode(), statusCodeBundle.getHeaderNotExistsMessage()));
                } else {
                    subModulesRequestList = subModulesService.csvToSubModuleRequest(multipartFile.getInputStream());
                }
            } else if (subModulesService.hasExcelFormat(multipartFile)) {
                if (!subModulesService.isExcelHeaderMatch(multipartFile)) {
                    return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFileFailureCode(), statusCodeBundle.getHeaderNotExistsMessage()));
                } else {
                    subModulesRequestList = subModulesService.excelToSubModuleRequest(multipartFile);
                }
            } else {
                return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(),
                        statusCodeBundle.getFileFailureCode(), statusCodeBundle.getFileFailureMessage()));
            }
            for (Map.Entry<Integer, SubModulesRequest> entry : subModulesRequestList.entrySet()) {

                if (!Utils.isNotNullAndEmpty(entry.getValue().getName())) {
                    subModulesService.addToErrorMessages(errorMessages, statusCodeBundle.getSubModuleNameEmptyMessage(), entry.getKey());
                } else if (subModuleNames.contains(entry.getValue().getName())) {
                    subModulesService.addToErrorMessages(errorMessages, statusCodeBundle.getSubModuleNameDuplicateMessage(), entry.getKey());
                } else {
                    subModuleNames.add(entry.getValue().getName());
                }
                if (!Utils.isNotNullAndEmpty(entry.getValue().getPrefix())) {
                    subModulesService.addToErrorMessages(errorMessages, statusCodeBundle.getSubModulePrefixEmptyMessage(), entry.getKey());
                } else if (subModulePrefixes.contains(entry.getValue().getPrefix())) {
                    subModulesService.addToErrorMessages(errorMessages, statusCodeBundle.getSubModulePrefixDuplicateMessage(), entry.getKey());
                } else {
                    subModulePrefixes.add(entry.getValue().getPrefix());
                }
                if (entry.getValue().getMain_module_Id() == null) {
                    subModulesService.addToErrorMessages(errorMessages, statusCodeBundle.getSubModuleMainModuleIdEmptyMessage(), entry.getKey());
                } else if (!mainModulesService.isExistMainModulesId(entry.getValue().getMain_module_Id())) {
                    subModulesService.addToErrorMessages(errorMessages, statusCodeBundle.getMainModuleNotExistsMessage(), entry.getKey());
                } else {
                    if (subModulesService.existsBySubModulesPrefix(entry.getValue().getPrefix(), entry.getValue().getMain_module_Id())) {
                        subModulesService.addToErrorMessages(errorMessages, statusCodeBundle.getSubModulePrefixAlReadyExistMessage(), entry.getKey());
                    }
                    if (subModulesService.existsBySubModulesName(entry.getValue().getName(), entry.getValue().getMain_module_Id())) {
                        subModulesService.addToErrorMessages(errorMessages, statusCodeBundle.getSubModuleNameAlReadyExistMessage(), entry.getKey());
                    }
                }
            }
            if (!errorMessages.isEmpty()) {
                return ResponseEntity.ok(new FileResponse(RequestStatus.FAILURE.getStatus(),
                        statusCodeBundle.getFailureCode(),
                        statusCodeBundle.getSubModuleFileImportValidationMessage(),
                        errorMessages));
            } else if (subModulesRequestList.isEmpty()) {
                return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(),
                        statusCodeBundle.getFileFailureCode(), statusCodeBundle.getSubModulesFileEmptyMessage()));
            } else {
                for (Map.Entry<Integer, SubModulesRequest> entry : subModulesRequestList.entrySet()) {
                    subModulesService.saveSubModules(entry.getValue());
                }
                return ResponseEntity.ok(new BaseResponse(RequestStatus.SUCCESS.getStatus(),
                        statusCodeBundle.getCommonSuccessCode(),
                        statusCodeBundle.getSaveSubModuleSuccessMessage()));
            }
        } catch (IOException e) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(),
                    statusCodeBundle.getFailureCode(),
                    statusCodeBundle.getSaveProjectValidationMessage()));
        }
    }

    @PutMapping(value = EndpointURI.SUBMODULE)
    public ResponseEntity<Object> editSubModules(@RequestBody SubModulesRequest subModulesRequest) {
        if (!subModulesService.existsBySubModuleId(subModulesRequest.getId())) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(),
                    statusCodeBundle.getSubModulesNotExistCode(),
                    statusCodeBundle.getSubModuleNotExistsMessage()));
        }
        if (!mainModulesService.isExistMainModulesId(subModulesRequest.getMain_module_Id())) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(),
                    statusCodeBundle.getMainModulesNotExistCode(),
                    statusCodeBundle.getMainModuleNotExistsMessage()));
        }
        if (!Utils.checkRegexBeforeAfterWords(subModulesRequest.getName()))
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getSpacesNotAllowedMessage()));
        if(subModulesRequest.getName()==null && subModulesRequest.getName().isEmpty()){
            return ResponseEntity.ok(new BaseResponse(RequestStatus.ERROR.getStatus(),statusCodeBundle.getNullValuesCode(),
                    statusCodeBundle.getTestCaseNameEmptyMessage()));
        }
        if (subModulesService.isUpdateSubModuleNameExits(subModulesRequest.getName(), subModulesRequest.getId())) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(),
                    statusCodeBundle.getSubModulesAlReadyExistCode(),
                    statusCodeBundle.getSubModuleNameAlReadyExistMessage()));
        }
        if (!Utils.checkRegexBeforeAfterWords(subModulesRequest.getPrefix()))
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getSpacesNotAllowedMessage()));

        if (subModulesService.isUpdateSubModulePrefixExits(subModulesRequest.getPrefix(), subModulesRequest.getId())) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(),
                    statusCodeBundle.getSubModulesAlReadyExistCode(),
                    statusCodeBundle.getSubModulePrefixAlReadyExistMessage()));
        }
        subModulesService.saveSubModules(subModulesRequest);
        return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(),
                statusCodeBundle.getCommonSuccessCode(),
                statusCodeBundle.getUpdateSubModuleSuccessMessage()));
    }

    @GetMapping(value = EndpointURI.SUBMODULE_BY_ID)
    public ResponseEntity<Object> getSubModuleById(@PathVariable Long id) {
        if (!subModulesService.existsBySubModuleId(id)) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(),
                    statusCodeBundle.getSubModulesNotExistCode(),
                    statusCodeBundle.getSubModuleNotExistsMessage()));
        }
        return ResponseEntity.ok(new ContentResponse<>(Constants.SUBMODULE, subModulesService.getSubModuleById(id),
                RequestStatus.SUCCESS.getStatus(),
                statusCodeBundle.getCommonSuccessCode(),
                statusCodeBundle.getGetSubModulesSuccessMessage()));
    }

    @GetMapping(value = EndpointURI.SUBMODULE_BY_MAIN_MODULE_ID)
    public ResponseEntity<Object> getSubModuleByMainModuleId(@PathVariable Long id) {
        if (!mainModulesService.isExistMainModulesId(id)) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(),
                    statusCodeBundle.getMainModulesNotExistCode(),
                    statusCodeBundle.getMainModuleNotExistsMessage()));
        }
        if (!subModulesService.existsByMainModuleId(id)) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(),
                    statusCodeBundle.getFailureCode(),
                    statusCodeBundle.getGetSubModuleNotHaveMainModuleId()));
        }
        return ResponseEntity.ok(new ContentResponse<>(Constants.SUBMODULES,
                subModulesService.getAllSubModuleByMainModuleId(id),
                RequestStatus.SUCCESS.getStatus(),
                statusCodeBundle.getCommonSuccessCode(),
                statusCodeBundle.getGetSubModulesSuccessMessage()));
    }

    @GetMapping(value = EndpointURI.SUBMODULES_SEARCH)
    public ResponseEntity<Object> getALlSubModuleWithMultiSearch(@RequestParam(name = "page") int page,
                                                                 @RequestParam(name = "size") int size,
                                                                 @RequestParam(name = "direction") String direction,
                                                                 @RequestParam(name = "sortField") String sortField,
                                                                 SubModuleSearch subModuleSearch) {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.valueOf(direction), sortField);
        PaginatedContentResponse.Pagination pagination = new PaginatedContentResponse.Pagination(page, size, 0, 0L);
        return ResponseEntity.ok(new ContentResponse<>(Constants.SUBMODULES, subModulesService.multiSearchSubModule(pageable, pagination, subModuleSearch),
                RequestStatus.SUCCESS.getStatus(),
                statusCodeBundle.getCommonSuccessCode(),
                statusCodeBundle.getGetAllSubModuleSuccessMessage()));
    }

    @DeleteMapping(value = EndpointURI.SUBMODULE_BY_ID)
    public ResponseEntity<Object> deleteSubModuleById(@PathVariable Long id) {
        if (!subModulesService.existsBySubModuleId(id)) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(),
                    statusCodeBundle.getSubModulesNotExistCode(),
                    statusCodeBundle.getSubModuleNotExistsMessage()));
        }
        if (testCasesService.existsBySubModuleId(id)) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(),
                    statusCodeBundle.getSubModulesDependentCode(),
                    statusCodeBundle.getSubModulesDependentMessage()));
        }
        subModulesService.deleteSubModuleById(id);
        return ResponseEntity.ok(new BaseResponse(RequestStatus.SUCCESS.getStatus(),
                statusCodeBundle.getCommonSuccessCode(),
                statusCodeBundle.getDeleteSubModuleSuccessMessage()));
    }

    @GetMapping(EndpointURI.SUBMODULE_BY_PROJECT_ID)
    public ResponseEntity<Object> getSubModulesByProjectId(@PathVariable Long id,
                                                           @RequestParam(name = "page") int page,
                                                           @RequestParam(name = "size") int size,
                                                           @RequestParam(name = "direction") String direction,
                                                           @RequestParam(name = "sortField") String sortField) {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.valueOf(direction), sortField);
        PaginatedContentResponse.Pagination pagination = new PaginatedContentResponse.Pagination(page, size, 0, 0L);
        if (!projectService.existByProjectId(id)) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(),
                    statusCodeBundle.getProjectNotExistCode(),
                    statusCodeBundle.getProjectNotExistsMessage()));
        }
        if (!subModulesService.existsByProjectId(id)) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(),
                    statusCodeBundle.getFailureCode(),
                    statusCodeBundle.getGetSubModuleNotHaveProjectId()));
        }
        return ResponseEntity.ok(new PaginatedContentResponse<>(Constants.SUBMODULES, subModulesService.getSubModulesByProjectIdWithPagination(id, pageable, pagination),
                RequestStatus.SUCCESS.getStatus(), statusCodeBundle.getCommonSuccessCode(), statusCodeBundle.getSubModulesByProjectId(), pagination));
    }
}
