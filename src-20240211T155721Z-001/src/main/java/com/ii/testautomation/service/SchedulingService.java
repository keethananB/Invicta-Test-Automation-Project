package com.ii.testautomation.service;

import com.ii.testautomation.dto.request.SchedulingRequest;
import com.ii.testautomation.dto.response.ScheduleResponse;
import com.ii.testautomation.dto.response.SchedulingResponse;
import com.ii.testautomation.response.common.PaginatedContentResponse;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface SchedulingService {
    void deleteScheduling(Long schedulingId);

    ScheduleResponse getSchedulingById(Long id);

    boolean existById(Long id);

    List<SchedulingResponse> viewByProjectId(Long projectId, Pageable pageable, PaginatedContentResponse.Pagination pagination);

    void saveTestScheduling(SchedulingRequest SchedulingRequest);

    boolean existsBySchedulingNameByTestGroupingAndProjectId(String name, Long projectId);

    void updateScheduling(SchedulingRequest schedulingRequest);

    boolean isUpdateNameExists(String Name, Long projectId, Long schedulingId);

    boolean existsByTestCaseId(Long testCaseId);

    boolean existsByScheduleOption(int month, int week, int minutes, int hour, int year, LocalDateTime startTime,Long projectId);

    boolean isUpdateScheduleOptionExists(int month, int week, int minutes, int hour, int year, LocalDateTime startDateTime, Long id,Long projectId);

    boolean existsByTestGroupingId(Long id);

    boolean checkStartDate(LocalDateTime startDate);
}
