package com.ii.testautomation.service;

import com.ii.testautomation.dto.request.MainModulesRequest;
import com.ii.testautomation.dto.response.MainModulesResponse;
import com.ii.testautomation.dto.search.MainModuleSearch;
import com.ii.testautomation.response.common.PaginatedContentResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface MainModulesService {
    void saveMainModules(MainModulesRequest mainModulesRequest);

    void deleteMainModules(Long id);

    MainModulesResponse getByMainModulesId(Long id);

    List<MainModulesResponse> getMainModulesByModuleId(Long id);

    List<MainModulesResponse> SearchMainModulesWithPagination(Pageable pageable, PaginatedContentResponse.Pagination pagination, MainModuleSearch mainModuleSearch);

    Map<Integer, MainModulesRequest> csvProcess(InputStream inputStream);

    Map<Integer, MainModulesRequest> excelProcess(MultipartFile multipartFile);

    List<MainModulesResponse> getMainModulesByProjectId(Pageable pageable, PaginatedContentResponse.Pagination pagination, Long id);

    void addToErrorMessages(Map<String, List<Integer>> errorMessages, String key, int value);

    boolean isExistMainModulesId(Long id);

    boolean isExistMainModulesName(String name, Long id);

    boolean isExistPrefix(String prefix, Long id);

    boolean isExistModulesId(Long id);

    boolean isExistMainModulesByProjectId(Long id);

    boolean isUpdateMainModulesNameExist(String mainModuleName,Long mainModuleId);

    boolean isUpdateMainModulesPrefixExist(String mainModuleprefix,Long mainModuleId);

    boolean existsMainModuleByModuleId(Long id);

    boolean hasExcelFormat(MultipartFile multipartFile);

    boolean isExcelHeaderMatch(MultipartFile multipartFile);

    boolean isCSVHeaderMatch(MultipartFile multipartFile);
}