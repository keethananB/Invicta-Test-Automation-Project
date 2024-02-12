package com.ii.testautomation.repositories;

import com.ii.testautomation.entities.TestCases;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;


public interface TestCasesRepository extends JpaRepository<TestCases, Long>, QuerydslPredicateExecutor<TestCases> {
    boolean existsByNameIgnoreCaseAndSubModule_MainModule_Modules_Project_Id(String name, Long projectId);

    List<TestCases> findBySubModuleId(Long submoduleId);

    List<TestCases> findBySubModuleIdAndIdNot(Long submoduleId, Long id);

    boolean existsByNameIgnoreCaseAndSubModuleId(String name, Long submoduleId);

    boolean existsByNameIgnoreCaseAndSubModuleIdAndIdNot(String name, Long subModuleId, Long id);

    List<TestCases> findAllTestCasesBySubModuleId(Long id);

    List<TestCases> findBySubModule_MainModule_Modules_Id(Long ModuleId);

    List<TestCases> findBySubModule_MainModule_Id(Long MainModuleId);

    Page<TestCases> findBySubModuleMainModuleModulesProjectId(Long ProjectId, Pageable pageable);

    boolean existsBySubModuleId(Long id);

    boolean existsBySubModule_MainModule_Modules_Project_id(Long ProjectId);

    TestCases findByIdAndSubModule_MainModule_Modules_Project_Id(Long testCaseId, Long projectId);

    List<TestCases> findBySubModule_MainModule_IdAndSubModule_MainModule_Modules_Project_Id(Long mainModuleId, Long projectId);

    List<TestCases> findBySubModule_MainModule_Modules_IdAndSubModule_MainModule_Modules_Project_Id(Long moduleId, Long projectId);

    List<TestCases> findBySubModuleIdAndSubModule_MainModule_Modules_Project_Id(Long subModuleId, Long projectId);
}
