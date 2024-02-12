package com.ii.testautomation.service;

import com.ii.testautomation.dto.request.ProjectRequest;
import com.ii.testautomation.dto.response.ProjectResponse;
import com.ii.testautomation.dto.search.ProjectSearch;
import com.ii.testautomation.response.common.PaginatedContentResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface ProjectService {
    boolean checkJarFile(MultipartFile jarFile);

    boolean checkPropertiesFile(MultipartFile propertiesFile);

    void saveProject(ProjectRequest projectRequest, MultipartFile jarFile, MultipartFile configFile);

    void updateProject(ProjectRequest projectRequest, MultipartFile jarFile, MultipartFile configFile);

    boolean existByProjectNameAndCompanyId(String projectName, Long companyUserId);

    boolean existByProjectCodeAndCompanyId(String projectCode,Long companyUserId);

    boolean isUpdateProjectNameExist(String projectName, Long projectId);

    boolean isUpdateProjectCodeExist(String projectCode, Long projectId);

    boolean existByProjectId(Long projectId);

    ProjectResponse getProjectById(Long projectId) throws IOException;

    List<ProjectResponse> multiSearchProject(Pageable pageable, PaginatedContentResponse.Pagination pagination, ProjectSearch projectSearch);

    void deleteProject(Long projectId);

    Map<Integer, ProjectRequest> csvToProjectRequest(InputStream inputStream);

    boolean hasExcelFormat(MultipartFile multipartFile);

    Map<Integer, ProjectRequest> excelToProjectRequest(MultipartFile multipartFile);

    void addToErrorMessages(Map<String, List<Integer>> errorMessages, String key, int value);

    boolean isExcelHeaderMatch(MultipartFile multipartFile);

    boolean isCSVHeaderMatch(MultipartFile multipartFile);

    boolean hasJarPath(Long projectId);

    boolean existsByUsersId(Long usersId);

    public boolean hasConfigPath(Long projectId);

    boolean projectCount(Long companyId);

    List<ProjectResponse> getProjectByCompanyId(Long companyUserId,Pageable pageable,PaginatedContentResponse.Pagination pagination);
}