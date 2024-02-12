package com.ii.testautomation.repositories;

import com.ii.testautomation.entities.SubModules;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import java.util.List;

public interface SubModulesRepository extends JpaRepository<SubModules, Long>, QuerydslPredicateExecutor<SubModules> {

    boolean existsByNameIgnoreCaseAndMainModule_Modules_ProjectId(String name, Long projectId);

    boolean existsByPrefixIgnoreCaseAndMainModule_Modules_ProjectId(String prefix, Long projectId);

    boolean existsByNameIgnoreCaseAndMainModule_Modules_ProjectIdAndIdNot(String name, Long projectId, Long id);

    boolean existsByPrefixIgnoreCaseAndMainModule_Modules_ProjectIdAndIdNot(String prefix, Long projectId, Long id);

    List<SubModules> findAllSubModulesByMainModuleId(Long id);

    Page<SubModules> findByMainModule_Modules_Project_Id(Long id, Pageable pageable);

    boolean existsByMainModuleId(Long id);

    boolean existsByMainModule_Modules_ProjectId(Long projectId);

    SubModules findByNameIgnoreCaseAndMainModule_Modules_ProjectId(String subModuleName, Long projectId);
}
