package com.ii.testautomation.repositories;

import com.ii.testautomation.entities.ExecutionHistory;
import com.ii.testautomation.entities.MainModules;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

public interface ExecutionHistoryRepository extends JpaRepository<ExecutionHistory, Long>, QuerydslPredicateExecutor<MainModules> {

    boolean existsByTestGroupingId(Long id);

    List<ExecutionHistory> findAllByTestGroupingIdOrderByCreatedAtDesc(Long testGroupingId);

    ExecutionHistory findFirstByTestGroupingIdOrderByCreatedAtDesc(Long id);

    List<ExecutionHistory> findByTestGroupingIdAndCreatedAtBetweenOrderByCreatedAtDesc(Long id, Timestamp startDate, Timestamp endDate);
}
