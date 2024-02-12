package com.ii.testautomation.repositories;

import com.ii.testautomation.entities.Modules;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;


public interface ModulesRepository extends JpaRepository<Modules, Long>, QuerydslPredicateExecutor<Modules> {
    boolean existsByNameIgnoreCaseAndProjectId(String name, Long projectId);

    boolean existsByPrefixIgnoreCaseAndProjectId(String prefix, Long projectId);

    boolean existsByNameIgnoreCaseAndProjectIdAndIdNot(String name, Long projectId, Long id);

    boolean existsByPrefixIgnoreCaseAndProjectIdAndIdNot(String prefix, Long projectId, Long id);

    Page<Modules> findAllModulesByProjectId(Long projectId, Pageable pageable);

    List<Modules> findAllModulesByProjectId(Long projectId);

    boolean existsByProjectId(Long id);
}
