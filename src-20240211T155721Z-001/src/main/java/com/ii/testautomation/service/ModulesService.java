package com.ii.testautomation.service;

import com.ii.testautomation.dto.request.ModulesRequest;
import com.ii.testautomation.dto.response.ModulesResponse;
import com.ii.testautomation.dto.response.ProjectModuleResponse;
import com.ii.testautomation.dto.search.ModuleSearch;
import com.ii.testautomation.dto.search.TestCaseSearch;
import com.ii.testautomation.response.common.PaginatedContentResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface ModulesService {
    void saveModule(ModulesRequest modulesRequest);

    boolean isModuleExistsByName(String name, Long projectId);

    boolean isModuleExistsByPrefix(String prefix, Long projectId);

    boolean existsByModulesId(Long id);

    boolean isUpdateModuleNameExists(String name, Long id);

    boolean isUpdateModulePrefixExists(String prefix, Long id);

    List<ModulesResponse> multiSearchModules(Pageable pageable, PaginatedContentResponse.Pagination pagination, ModuleSearch moduleSearch);

    ModulesResponse getModuleById(Long id);

    List<ModulesResponse> getAllModuleByProjectIdWithPagination(Long projectId, Pageable pageable, PaginatedContentResponse.Pagination pagination);

    void deleteModuleById(Long id);

    boolean existsModuleByProjectId(Long projectId);

    boolean hasExcelFormat(MultipartFile multipartFile);

    Map<Integer, ModulesRequest> csvToModulesRequest(InputStream inputStream);

    Map<Integer, ModulesRequest> excelToModuleRequest(MultipartFile multipartFile);

    boolean isExcelHeaderMatch(MultipartFile multipartFile);

    boolean isCSVHeaderMatch(MultipartFile multipartFile);

    void addToErrorMessages(Map<String, List<Integer>> errorMessages, String key, int value);

    ProjectModuleResponse getAllByProjectIdAndSearch(Long projectId, String testCaseName);


}
