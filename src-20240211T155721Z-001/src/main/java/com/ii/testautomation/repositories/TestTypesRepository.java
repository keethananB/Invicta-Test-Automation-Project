package com.ii.testautomation.repositories;

import com.ii.testautomation.entities.MainModules;
import com.ii.testautomation.entities.TestTypes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface TestTypesRepository extends JpaRepository<TestTypes, Long>, QuerydslPredicateExecutor<TestTypes> {
    List<TestTypes> findAllByCompanyUserId(Long id);

    boolean existsByNameIgnoreCaseAndCompanyUserId(String name, Long companyUserId);

    boolean existsByNameIgnoreCaseAndCompanyUserIdAndIdNot(String name, Long companyUserId, Long id);
}
