package com.ii.testautomation.service;

import com.ii.testautomation.dto.request.TestScenariosRequest;
import com.ii.testautomation.dto.response.TestScenariosResponse;
import com.ii.testautomation.response.common.PaginatedContentResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TestScenariosService {
    boolean existsByTestScenarioId(Long testScenarioId);


    boolean existsByTestScenarioNameIgnoreCase(String name, Long projectId);

    boolean existByProjectId(Long projectId);

    void updateTestScenario(TestScenariosRequest testScenariosRequest);

    List<TestScenariosResponse> getAllTestScenariosByProjectIdWithPagination(Long projectId, Pageable pageable, PaginatedContentResponse.Pagination pagination);

    void DeleteTestScenariosById(Long id);

    boolean existByTestCaseList(TestScenariosRequest testScenariosRequest);

    void saveTestScenario(TestScenariosRequest testScenariosRequest);

    boolean isUpdateTestScenariosNameExists(Long id, String name, Long projectId);

    TestScenariosResponse viewScenarioById(Long id);
}
