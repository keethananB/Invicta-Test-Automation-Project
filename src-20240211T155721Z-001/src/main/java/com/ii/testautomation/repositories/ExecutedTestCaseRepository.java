package com.ii.testautomation.repositories;

import com.ii.testautomation.entities.ExecutedTestCase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExecutedTestCaseRepository extends JpaRepository<ExecutedTestCase,Long> {
    List<ExecutedTestCase> findByTestGroupingId(Long id);
}
