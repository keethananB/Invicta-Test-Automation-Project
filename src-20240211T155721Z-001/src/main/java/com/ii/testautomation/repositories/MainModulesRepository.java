package com.ii.testautomation.repositories;

import com.ii.testautomation.entities.MainModules;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface MainModulesRepository extends JpaRepository<MainModules, Long>, QuerydslPredicateExecutor<MainModules> {
    boolean existsByNameIgnoreCaseAndModules_ProjectId(String name,Long projectId);

    boolean existsByPrefixIgnoreCaseAndModules_ProjectId(String prefix, Long projectId);

    boolean existsByNameIgnoreCaseAndModules_ProjectIdAndIdNot(String name,Long projectId, Long id);

    boolean existsByPrefixIgnoreCaseAndModules_ProjectIdAndIdNot(String prefix,Long projectId,Long id);

    boolean existsByModulesId(Long id);

    boolean existsByModules_ProjectId(Long id);

    Page<MainModules> findByModules_ProjectId(Long id, Pageable pageable);

    List<MainModules> findAllByModulesId(Long id);

    List<MainModules> findByModulesIdAndModules_ProjectId(Long id, Long projectId);
}