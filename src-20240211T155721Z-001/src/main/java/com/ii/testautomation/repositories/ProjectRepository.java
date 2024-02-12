package com.ii.testautomation.repositories;

import com.ii.testautomation.entities.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long>, QuerydslPredicateExecutor<Project> {
    boolean existsByNameIgnoreCase(String name);

    boolean existsByCodeIgnoreCase(String code);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    boolean existsByCodeIgnoreCaseAndIdNot(String code, Long id);

    boolean existsByUsersId(Long usersId);

    List<Project> findByCompanyUserId(Long companyId);

    Page<Project> findByCompanyUserId(Long companyUserId, Pageable pageable);

    boolean existsByNameIgnoreCaseAndCompanyUserId(String projectName, Long companyUserId);

    boolean existsByCodeIgnoreCaseAndCompanyUserId(String projectCode, Long companyUserId);
}
