package com.ii.testautomation.service;

import com.ii.testautomation.dto.request.SubModulesRequest;
import com.ii.testautomation.dto.request.TestCaseRequest;
import com.ii.testautomation.dto.response.SubModulesResponse;
import com.ii.testautomation.dto.search.SubModuleSearch;
import com.ii.testautomation.response.common.PaginatedContentResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface SubModulesService {
    void saveSubModules(SubModulesRequest subModulesRequest);

    boolean existsBySubModulesName(String subModuleName, Long mainModuleId);

    boolean existsBySubModulesPrefix(String subModulePrefix, Long mainModuleId);

    boolean isUpdateSubModuleNameExits(String subModuleName, Long subModuleId);

    boolean isUpdateSubModulePrefixExits(String subModulePrefix, Long subModuleId);

    boolean existsBySubModuleId(Long subModuleId);

    SubModulesResponse getSubModuleById(Long subModuleId);

    List<SubModulesResponse> getAllSubModuleByMainModuleId(Long id);

    List<SubModulesResponse> multiSearchSubModule(Pageable pageable, PaginatedContentResponse.Pagination pagination, SubModuleSearch subModuleSearch);

    void deleteSubModuleById(Long subModuleId);

    boolean existsByMainModuleId(Long mainModuleId);

    Map<Integer, SubModulesRequest> csvToSubModuleRequest(InputStream inputStream);

    Map<Integer, SubModulesRequest> excelToSubModuleRequest(MultipartFile multipartFile);

    boolean hasExcelFormat(MultipartFile multipartFile);

    void addToErrorMessages(Map<String, List<Integer>> errorMessages, String key, int value);

    boolean isExcelHeaderMatch(MultipartFile multipartFile);

    boolean isCSVHeaderMatch(MultipartFile multipartFile);

    List<SubModulesResponse> getSubModulesByProjectIdWithPagination(Long id, Pageable pageable, PaginatedContentResponse.Pagination pagination);

    boolean existsByProjectId(Long projectId);

    Long getSubModuleIdByNameForProject(String subModuleName, Long projectId);

    boolean existsBySubModulesNameForProject(String subModuleName, Long projectId);


}
