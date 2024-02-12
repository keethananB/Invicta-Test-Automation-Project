package com.ii.testautomation.service.impl;

import com.ii.testautomation.dto.request.TestScenariosRequest;
import com.ii.testautomation.dto.response.TestScenariosResponse;
import com.ii.testautomation.entities.TestCases;
import com.ii.testautomation.entities.TestScenarios;
import com.ii.testautomation.repositories.ProjectRepository;
import com.ii.testautomation.repositories.TestCasesRepository;
import com.ii.testautomation.repositories.TestScenariosRepository;
import com.ii.testautomation.response.common.PaginatedContentResponse;
import com.ii.testautomation.service.TestScenariosService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TestScenariosServiceImpl implements TestScenariosService {
    @Autowired
    private TestScenariosRepository testScenariosRepository;
    @Autowired
    private TestCasesRepository testCasesRepository;
    @Autowired
    private ProjectRepository projectRepository;

    @Override
    public boolean isUpdateTestScenariosNameExists(Long id, String name, Long projectId) {
        return testScenariosRepository.existsByNameIgnoreCaseAndTestCases_SubModule_MainModule_Modules_Project_IdAndIdNot(name, projectId, id);

    }

    @Override
    public TestScenariosResponse viewScenarioById(Long id) {
        TestScenariosResponse testScenariosResponse = new TestScenariosResponse();
        List<String> testCaseNames = new ArrayList<>();
        List<Long> testCaseIds = new ArrayList<>();
        List<Long> mainModuleIds = new ArrayList<>();
        List<Long> subModuleIds = new ArrayList<>();
        List<Long> moduleIds = new ArrayList<>();
        TestScenarios testScenarios = testScenariosRepository.findById(id).get();
        BeanUtils.copyProperties(testScenarios, testScenariosResponse);
        for (TestCases testCases : testScenarios.getTestCases()) {
            testCaseNames.add(testCases.getName().substring(testCases.getName().lastIndexOf(".") + 1));
            testCaseIds.add(testCases.getId());
            subModuleIds.add(testCases.getSubModule().getId());
            mainModuleIds.add(testCases.getSubModule().getMainModule().getId());
            moduleIds.add(testCases.getSubModule().getMainModule().getModules().getId());
        }
        List<Long> sortedMainModuleIds = mainModuleIds.stream().distinct().collect(Collectors.toList());
        List<Long> sortedSubModuleIds = subModuleIds.stream().distinct().collect(Collectors.toList());
        List<Long> sortedModuleIds = moduleIds.stream().distinct().collect(Collectors.toList());
        List<Long> sortedTestCaseIds = testCaseIds.stream().distinct().collect(Collectors.toList());
        List<String> sortedTestCaseNames = testCaseNames.stream().distinct().collect(Collectors.toList());

        testScenariosResponse.setTestCasesName(sortedTestCaseNames);
        testScenariosResponse.setTestCasesId(sortedTestCaseIds);
        testScenariosResponse.setModuleId(sortedModuleIds);
        testScenariosResponse.setMainModuleId(sortedMainModuleIds);
        testScenariosResponse.setSubModuleId(sortedSubModuleIds);

        return testScenariosResponse;
    }

    @Override
    public boolean existsByTestScenarioId(Long testScenarioId) {
        return testScenariosRepository.existsById(testScenarioId);
    }

    @Override
    public boolean existsByTestScenarioNameIgnoreCase(String name, Long projectId) {
        return testScenariosRepository.existsByNameIgnoreCaseAndTestCases_SubModule_MainModule_Modules_Project_Id(name, projectId);
    }

    @Override
    public boolean existByTestCaseList(TestScenariosRequest testScenariosRequest) {
        List<Boolean> booleans = new ArrayList<>();
        List<TestCases> testCasesList = new ArrayList<>();
        for (Long testCaseId : testScenariosRequest.getTestCasesId()) {
            testCasesList.add(testCasesRepository.findById(testCaseId).get());
        }
        List<TestScenarios> testScenariosList = testScenariosRepository.findAll();
        for (TestScenarios testScenarios : testScenariosList) {
            if (testScenarios.getTestCases().containsAll(testCasesList)) booleans.add(true);
            else booleans.add(false);
        }
        if (booleans.contains(true)) return true;
        else return false;
    }

    @Override
    public boolean existByProjectId(Long projectId) {
        return testScenariosRepository.existsByTestCasesSubModuleMainModuleModulesProject_id(projectId);

    }

    @Override
    public void saveTestScenario(TestScenariosRequest testScenariosRequest) {

        TestScenarios testScenarios = new TestScenarios();
        List<TestCases> testCasesList = new ArrayList<>();

        if (testScenariosRequest.getTestCasesId() != null) {
            for (Long testCaseId : testScenariosRequest.getTestCasesId())
                testCasesList.add(testCasesRepository.findByIdAndSubModule_MainModule_Modules_Project_Id(testCaseId, testScenariosRequest.getProjectId()));
        }
        if (testScenariosRequest.getSubModuleIds() != null) {
            for (Long subModuleId : testScenariosRequest.getSubModuleIds())
                testCasesRepository.findBySubModuleIdAndSubModule_MainModule_Modules_Project_Id(subModuleId, testScenariosRequest.getProjectId()).forEach(TestCases -> testCasesList.add(TestCases));
        }
        if (testScenariosRequest.getMainModuleIds() != null) {
            for (Long mainModuleId : testScenariosRequest.getMainModuleIds())
                testCasesRepository.findBySubModule_MainModule_IdAndSubModule_MainModule_Modules_Project_Id(mainModuleId, testScenariosRequest.getProjectId()).forEach(TestCases -> testCasesList.add(TestCases));
        }
        if (testScenariosRequest.getModuleIds() != null) {
            for (Long moduleId : testScenariosRequest.getModuleIds())
                testCasesRepository.findBySubModule_MainModule_Modules_IdAndSubModule_MainModule_Modules_Project_Id(moduleId, testScenariosRequest.getProjectId()).forEach(TestCases -> testCasesList.add(TestCases));
        }

        List<TestCases> sortedTestCaseList = testCasesList.stream().distinct().collect(Collectors.toList());
        BeanUtils.copyProperties(testScenariosRequest, testScenarios);
        testScenarios.setTestCases(sortedTestCaseList);
        testScenariosRepository.save(testScenarios);
    }

    @Override
    public void updateTestScenario(TestScenariosRequest testScenariosRequest) {
        List<TestCases> testCasesList = new ArrayList<>();
        //BeanUtils.copyProperties(testScenariosRequest, testScenarios);
        if (testScenariosRequest.getTestCasesId() != null) {
            for (Long testCaseId : testScenariosRequest.getTestCasesId())
                testCasesList.add(testCasesRepository.findById(testCaseId).get());
        }
        if (testScenariosRequest.getSubModuleIds() != null) {
            for (Long subModuleId : testScenariosRequest.getSubModuleIds())
                testCasesRepository.findAllTestCasesBySubModuleId(subModuleId).forEach(TestCases -> testCasesList.add(TestCases));
        }
        if (testScenariosRequest.getMainModuleIds() != null) {
            for (Long mainModuleId : testScenariosRequest.getMainModuleIds())
                testCasesRepository.findBySubModule_MainModule_Id(mainModuleId).forEach(TestCases -> testCasesList.add(TestCases));
        }
        if (testScenariosRequest.getModuleIds() != null) {
            for (Long moduleId : testScenariosRequest.getModuleIds())
                testCasesRepository.findBySubModule_MainModule_Modules_Id(moduleId).forEach(TestCases -> testCasesList.add(TestCases));
        }
        TestScenarios testScenarios = testScenariosRepository.findByIdAndTestCases_SubModule_MainModule_Modules_Project_Id(testScenariosRequest.getId(), testScenariosRequest.getProjectId());

        for (TestCases testCases : testScenarios.getTestCases()) {
            testCasesList.add(testCasesRepository.findById(testCases.getId()).get());
        }
        List<TestCases> sortedTestCaseList = testCasesList.stream().distinct().collect(Collectors.toList());
        testScenarios.setName(testScenariosRequest.getName());
        testScenarios.setTestCases(sortedTestCaseList);
        testScenariosRepository.save(testScenarios);
    }

    @Override
    public List<TestScenariosResponse> getAllTestScenariosByProjectIdWithPagination(Long projectId, Pageable pageable, PaginatedContentResponse.Pagination pagination) {
        List<TestScenariosResponse> testScenariosResponseList = new ArrayList<>();
        Page<TestScenarios> testScenariosPage = testScenariosRepository.findDistinctTestScenariosByTestCases_SubModule_MainModule_Modules_Project_Id(projectId, pageable);
        pagination.setTotalRecords(testScenariosPage.getTotalElements());
        pagination.setPageSize(testScenariosPage.getTotalPages());

        for (TestScenarios testScenarios : testScenariosPage) {
            TestScenariosResponse testScenariosResponse = new TestScenariosResponse();
            BeanUtils.copyProperties(testScenarios, testScenariosResponse);
            List<String> testCasesNames = new ArrayList<>();
            List<Long> testCasesIds = new ArrayList<>();
            for (TestCases testCase : testScenarios.getTestCases()) {
                String testCaseName = testCase.getName().substring(testCase.getName().lastIndexOf(".") + 1);
                if (!testCasesNames.contains(testCaseName)) {
                    testCasesNames.add(testCaseName);
                    testCasesIds.add(testCase.getId());
                }
            }
            testScenariosResponse.setTestCasesName(testCasesNames);
            testScenariosResponse.setTestCasesId(testCasesIds);
            testScenariosResponseList.add(testScenariosResponse);
        }
        return testScenariosResponseList;
    }

    @Override
    public void DeleteTestScenariosById(Long id) {
        testScenariosRepository.deleteById(id);
    }

}
