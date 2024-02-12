package com.ii.testautomation.repositories;

import com.ii.testautomation.entities.Scheduling;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.time.LocalDateTime;
import java.util.List;

public interface SchedulingRepository extends JpaRepository<Scheduling, Long>, QuerydslPredicateExecutor<Scheduling> {

    Page<Scheduling> findByTestGrouping_ProjectId(Pageable pageable, Long projectId);

    boolean existsByTestGroupingId(Long testGroupingId);

    boolean existsByNameIgnoreCaseAndTestGrouping_TestCases_SubModule_MainModule_Modules_Project_Id(String name, Long projectId);

    boolean existsByTestGrouping_TestCases_Id(Long id);

    boolean existsByNameIgnoreCaseAndTestGrouping_TestCases_SubModule_MainModule_Modules_Project_IdAndIdNot(String name, Long projectId, Long id);

    boolean existsByStartDateTimeAndYearAndMonthAndWeekAndHourAndMinutesAndTestGroupingProjectId(LocalDateTime startDateTime, int year, int month, int week, int hour, int minutes, Long projectId);

    boolean existsByStartDateTimeAndYearAndMonthAndWeekAndHourAndMinutesAndTestGroupingProjectIdAndIdNot(LocalDateTime startDateTime, int year, int month, int week, int hour, int minutes, Long id, Long projectId);

    List<Scheduling> findByTestGroupingId(Long testGroupingId);
}
