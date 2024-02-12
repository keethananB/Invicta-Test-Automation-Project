package com.ii.testautomation.repositories;

import com.ii.testautomation.entities.Licenses;
import com.querydsl.core.BooleanBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface LicensesRepository extends JpaRepository<Licenses, Long>, QuerydslPredicateExecutor<Licenses> {
    boolean existsByNameIgnoreCase(String name);

    boolean existsByDurationAndNoOfProjectsAndNoOfUsers(Long duration, Long noOfProjects, Long noOfUsers);

    boolean existsByDurationAndNoOfProjectsAndNoOfUsersAndIdNot(Long duration, Long noOfProjects, Long noOfUsers, Long Id);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

}
