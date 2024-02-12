package com.ii.testautomation.service.impl;

import com.ii.testautomation.dto.request.SchedulingRequest;
import com.ii.testautomation.dto.response.ProgressResponse;
import com.ii.testautomation.dto.response.ScheduleResponse;
import com.ii.testautomation.dto.response.SchedulingResponse;
import com.ii.testautomation.entities.*;
import com.ii.testautomation.repositories.*;
import com.ii.testautomation.response.common.PaginatedContentResponse;
import com.ii.testautomation.service.SchedulingService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@PropertySource("classpath:application.properties")
@Service
public class SchedulingServiceImpl implements SchedulingService {
    @Autowired
    private SchedulingRepository schedulingRepository;
    @Autowired
    private TestCasesRepository testCasesRepository;
    @Autowired
    private TestScenariosRepository testScenariosRepository;
    @Autowired
    private TestGroupingRepository testGroupingRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private ExecutedTestCaseRepository executedTestCaseRepository;
    @Autowired
    private SequenceRepository sequenceRepository;
    @Autowired
    private ProgressBarRepository progressBarRepository;
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public void saveTestScheduling(SchedulingRequest schedulingRequest) {
        Scheduling scheduling = new Scheduling();
        BeanUtils.copyProperties(schedulingRequest, scheduling);
        String random = UUID.randomUUID().toString().replace("-", "");
        scheduling.setSchedulingCode(random);
        TestGrouping testGrouping = testGroupingRepository.findById(schedulingRequest.getGroupId()).get();
        scheduling.setTestGrouping(testGrouping);
        List<Long> testCasesId = new ArrayList<>();
        List<TestScenarios> testScenariosList = new ArrayList<>();
        List<TestCases> testCasessList = new ArrayList<>();
        int mapSize = schedulingRequest.getTestScenario().size() + schedulingRequest.getTestCase().size();
        for (int i = 0; i <= mapSize; i++) {
            for (Map.Entry<Integer, Long> entry : schedulingRequest.getTestScenario().entrySet()) {
                if (entry.getKey() == i) {
                    TestScenarios testScenarios = testScenariosRepository.findById(entry.getValue()).get();
                    testScenariosList.add(testScenarios);
                    Sequence sequence = new Sequence();
                    sequence.setCount(i);
                    sequence.setTestCaseId(null);
                    sequence.setTestScenarioId(testScenarios.getId());
                    sequence.setGroupId(testGrouping.getId());
                    sequence.setSchedulingCode(random);
                    sequenceRepository.save(sequence);
                    List<TestCases> testCasesList = testScenarios.getTestCases();
                    for (TestCases testCases : testCasesList) {
                        testCasesId.add(testCases.getId());
                    }
                }
            }
            for (Map.Entry<Integer, Long> entry : schedulingRequest.getTestCase().entrySet()) {
                if (entry.getKey() == i) {
                    TestCases testCases = testCasesRepository.findById(entry.getValue()).get();
                    testCasessList.add(testCases);
                    testCasesId.add(testCases.getId());
                    Sequence sequence = new Sequence();
                    sequence.setCount(i);
                    sequence.setTestCaseId(testCases.getId());
                    sequence.setTestScenarioId(null);
                    sequence.setGroupId(testGrouping.getId());
                    sequence.setSchedulingCode(random);
                    sequenceRepository.save(sequence);
                    break;
                }
            }
        }
        scheduling.setTestCasesIds(testCasesId);
        scheduling.setTestCases(testCasessList);
        scheduling.setTestScenarios(testScenariosList);
        scheduling.setNextExecutionTime(schedulingRequest.getStartDateTime());
        schedulingRepository.save(scheduling);
    }

    @Override
    public boolean existsBySchedulingNameByTestGroupingAndProjectId(String name, Long projectId) {
        return schedulingRepository.existsByNameIgnoreCaseAndTestGrouping_TestCases_SubModule_MainModule_Modules_Project_Id(name, projectId);
    }

    @Override
    public ScheduleResponse getSchedulingById(Long id) {
        ScheduleResponse scheduleResponse = new ScheduleResponse();
        Scheduling scheduling = schedulingRepository.findById(id).get();
        BeanUtils.copyProperties(scheduling,scheduleResponse);
        scheduleResponse.setTestGroupingName(scheduling.getTestGrouping().getName());
        scheduleResponse.setTestGroupingId(scheduling.getTestGrouping().getId());
        Map<Integer, Long> testScenarios = new HashMap<>();
        Map<Integer, Long> testCase = new HashMap<>();
        String schedulingCode = scheduling.getSchedulingCode();
        scheduleResponse.setSchedulingCode(schedulingCode);
        List<Sequence> sequences = sequenceRepository.findBySchedulingCode(schedulingCode);
        for (Sequence sequence : sequences) {
            Integer count = sequence.getCount();
            if (sequence.getTestCaseId() != null) {
                testCase.put(count, sequence.getTestCaseId());
            } else if (sequence.getTestScenarioId() != null) {
                testScenarios.put(count, sequence.getTestScenarioId());
            }
        }
        scheduleResponse.setTestScenario(testScenarios);
        scheduleResponse.setTestCase(testCase);
        return scheduleResponse;
    }

    @Override
    public boolean existById(Long id) {
        return schedulingRepository.existsById(id);
    }

    @Override
    public void deleteScheduling(Long schedulingId) {
        schedulingRepository.deleteById(schedulingId);
    }

    @Override
    public List<SchedulingResponse> viewByProjectId(Long projectId, Pageable pageable, PaginatedContentResponse.Pagination pagination) {
        List<SchedulingResponse> schedulingResponseList = new ArrayList<>();
        Page<Scheduling> schedulingList = schedulingRepository.findByTestGrouping_ProjectId(pageable, projectId);
        pagination.setTotalRecords(schedulingList.getTotalElements());
        pagination.setTotalPages(schedulingList.getTotalPages());

        for (Scheduling scheduling : schedulingList) {
            SchedulingResponse schedulingResponse = new SchedulingResponse();
            BeanUtils.copyProperties(scheduling,schedulingResponse);
            schedulingResponse.setTestGroupingId(scheduling.getTestGrouping().getId());
            schedulingResponse.setTestGroupingName(scheduling.getTestGrouping().getName());
            List<String> testCaseNames = new ArrayList<>();
            List<Long> testScenariosId = new ArrayList<>();
            List<String> testScenariosNames = new ArrayList<>();
            List<Long> testCaseIds = scheduling.getTestCasesIds();
            for (TestCases testCases : scheduling.getTestCases()) {
                testCaseNames.add(testCases.getName().substring(testCases.getName().lastIndexOf(".") + 1));
            }
            for (TestScenarios testScenarios : scheduling.getTestScenarios()) {
                testScenariosId.add(testScenarios.getId());
                testScenariosNames.add(testScenarios.getName());
            }
            testScenariosId = testScenariosId.stream().distinct().collect(Collectors.toList());
            testScenariosNames = testScenariosNames.stream().distinct().collect(Collectors.toList());
            schedulingResponse.setTestCasesIds(testCaseIds);
            schedulingResponse.setTestCasesNames(testCaseNames);
            schedulingResponse.setTestScenarioIds(testScenariosId);
            schedulingResponse.setTestScenarioNames(testScenariosNames);
            schedulingResponseList.add(schedulingResponse);
        }
        return schedulingResponseList;
    }

    @Override
    public void updateScheduling(SchedulingRequest schedulingRequest) {
        Scheduling scheduling = schedulingRepository.findById(schedulingRequest.getId()).get();
        BeanUtils.copyProperties(schedulingRequest, scheduling);
        List<Sequence> sequences = sequenceRepository.findBySchedulingCode(scheduling.getSchedulingCode());
        for (Sequence sequence : sequences) {
            sequenceRepository.deleteById(sequence.getId());
        }
        String random = UUID.randomUUID().toString().replace("-", "");
        scheduling.setSchedulingCode(random);
        TestGrouping testGrouping = testGroupingRepository.findById(schedulingRequest.getGroupId()).get();
        List<TestScenarios> testScenariosList = new ArrayList<>();
        List<Long> testCasesId = new ArrayList<>();
        List<TestCases> testCasesList = new ArrayList<>();
        int mapSize = schedulingRequest.getTestScenario().size() + schedulingRequest.getTestCase().size();
        for (int i = 0; i <= mapSize; i++) {
            for (Map.Entry<Integer, Long> entry : schedulingRequest.getTestScenario().entrySet()) {
                if (entry.getKey() == i) {
                    TestScenarios testScenarios = testScenariosRepository.findById(entry.getValue()).get();
                    testScenariosList.add(testScenarios);
                    List<TestCases> testCasesList1 = testScenarios.getTestCases();
                    Sequence sequence = new Sequence();
                    sequence.setCount(i);
                    sequence.setTestCaseId(null);
                    sequence.setTestScenarioId(testScenarios.getId());
                    sequence.setGroupId(testGrouping.getId());
                    sequence.setSchedulingCode(scheduling.getSchedulingCode());
                    sequenceRepository.save(sequence);
                    for (TestCases testCases : testCasesList1) {
                        testCasesId.add(testCases.getId());
                    }
                }
            }
            for (Map.Entry<Integer, Long> entry : schedulingRequest.getTestCase().entrySet()) {
                if (entry.getKey() == i) {
                    TestCases testCases = testCasesRepository.findById(entry.getValue()).get();
                    testCasesList.add(testCases);
                    testCasesId.add(testCases.getId());
                    Sequence sequence = new Sequence();
                    sequence.setCount(i);
                    sequence.setTestCaseId(testCases.getId());
                    sequence.setTestScenarioId(null);
                    sequence.setGroupId(testGrouping.getId());
                    sequence.setSchedulingCode(scheduling.getSchedulingCode());
                    sequenceRepository.save(sequence);
                    break;
                }
            }
        }
        scheduling.setTestGrouping(testGrouping);
        scheduling.setTestCasesIds(testCasesId);
        scheduling.setTestCases(testCasesList);
        scheduling.setTestScenarios(testScenariosList);
        scheduling.setNextExecutionTime(schedulingRequest.getStartDateTime());
        schedulingRepository.save(scheduling);
    }

    @Override
    public boolean isUpdateNameExists(String Name, Long projectId,Long schedulingId) {
        return schedulingRepository.existsByNameIgnoreCaseAndTestGrouping_TestCases_SubModule_MainModule_Modules_Project_IdAndIdNot(Name,projectId,schedulingId);
    }

    @Transactional
    @Scheduled(fixedRate = 5000)
    public void staticScheduling() throws IOException {
        System.out.println("==========================================DYNAMIC===============");
        List<Scheduling> schedulingList = schedulingRepository.findAll();
        if (schedulingList != null && !schedulingList.isEmpty()) {
            for (Scheduling scheduling : schedulingList) {
                LocalDateTime nextExecutionTime = scheduling.getNextExecutionTime();
                LocalDateTime startDateTime = LocalDateTime.parse(nextExecutionTime.toString());
                startDateTime = startDateTime.withSecond(0).withNano(0);
                System.out.println(startDateTime);
                LocalDateTime currentTime = LocalDateTime.parse(LocalDateTime.now().toString());
                currentTime = currentTime.withSecond(0).withNano(0);
                System.out.println(currentTime);
                if (scheduling.getCount() < scheduling.getNoOfTimes()) {
                    if (currentTime.equals(startDateTime)) {
                        autoExecution(scheduling);
                        System.out.println("triggered " + scheduling.getCount() + scheduling.getName());
                    }
                }
            }
        }
    }

    private void autoExecution(Scheduling scheduling) throws IOException {
        Long projectId = null;
        Long groupId = null;
        if (scheduling.isStatus()) {
            groupId = scheduling.getTestGrouping().getId();
            if (scheduling.getTestCasesIds() != null && !scheduling.getTestCasesIds().isEmpty()) {
                for (Long testCaseId : scheduling.getTestCasesIds()) {
                    projectId = testCasesRepository.findById(testCaseId).get().getSubModule().getMainModule().getModules().getProject().getId();
                    break;
                }
            }
        }
        schedulingExecution(scheduling.getTestCasesIds(), projectId, groupId, scheduling.getId());
    }

    private void schedulingExecution(List<Long> testCaseIds, Long projectId, Long groupingId, Long schedulingId) throws IOException {
        TestGrouping testGrouping = testGroupingRepository.findById(groupingId).get();
        testGrouping.setExecutionStatus(true);
        testGroupingRepository.save(testGrouping);
        for (Long testCaseId : testCaseIds) {
            TestCases testCases = testCasesRepository.findById(testCaseId).get();
            ExecutedTestCase executedTestCase = new ExecutedTestCase();
            executedTestCase.setTestCases(testCases);
            executedTestCase.setTestGrouping(testGrouping);
            executedTestCaseRepository.save(executedTestCase);
        }
        List<String> excelFiles = testGroupingRepository.findById(groupingId).get().getExcelFilePath();
        String projectPath = projectRepository.findById(projectId).get().getProjectPath();
        if (excelFiles != null) {
            for (String excel : excelFiles) {
                Path excelPath = Path.of(excel);
                try {
                    byte[] excelBytes = Files.readAllBytes(excelPath);
                    String excelFileName = excelPath.getFileName().toString();
                    Path destinationPath = Path.of(projectPath, excelFileName);
                    Files.write(destinationPath, excelBytes);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        jarExecution(projectId, schedulingId);
    }

    private void jarExecution(Long projectId, Long schedulingId) {
        String savedFilePath = projectRepository.findById(projectId).get().getJarFilePath();
        File jarFile = new File(savedFilePath);
        String jarFileName = jarFile.getName();
        String jarDirectory = jarFile.getParent();
        Scheduling scheduling = schedulingRepository.findById(schedulingId).get();
        int executionCount = scheduling.getCount();
        try {
            ProgressResponse progressResponse = new ProgressResponse();
            progressResponse.setProjectId(projectId);
            simpMessagingTemplate.convertAndSend("/queue/percentage/schedule/"+projectId, progressResponse);
            ProcessBuilder runProcessBuilder = new ProcessBuilder("java", "-jar", jarFileName);
            runProcessBuilder.directory(new File(jarDirectory));
            runProcessBuilder.redirectErrorStream(true);
            Process runProcess = runProcessBuilder.start();
            runProcess.waitFor();
            simpMessagingTemplate.convertAndSend("/queue/percentage/schedule/"+projectId, progressResponse);
            executionCount = executionCount + 1;
            scheduling.setCount(executionCount);
            schedulingRepository.save(scheduling);
            updateNextExecutionTime(scheduling.getId());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void updateNextExecutionTime(Long id) {
        Scheduling scheduling = schedulingRepository.findById(id).get();
        int year = scheduling.getNextExecutionTime().getYear();
        int month = scheduling.getNextExecutionTime().getMonthValue();
        int day = scheduling.getNextExecutionTime().getDayOfMonth();
        int hour = scheduling.getNextExecutionTime().getHour();
        int minute = scheduling.getNextExecutionTime().getMinute();
        int second = scheduling.getNextExecutionTime().getSecond();
        YearMonth yearMonth = YearMonth.of(year, month);
        int totalDaysInMonth = yearMonth.lengthOfMonth();
        if (scheduling.getYear() > 0) {
            year = year + scheduling.getYear();
        }
        if (scheduling.getMonth() > 0) {
            month = month + scheduling.getMonth();
            if (month > 12) {
                month = month - 12;
                year++;
            }
        }
        if (scheduling.getWeek() > 0) {
            day = day + 7;
            if (day >= totalDaysInMonth) {
                day = day - totalDaysInMonth;
                month++;
                if (month > 12) {
                    month = month - 12;
                    year++;
                }
            }
        }
        if (scheduling.getHour() > 0) {
            hour = hour + scheduling.getHour();
            if (hour >= 24) {
                hour = hour - 24;
                day++;
                if (day >= totalDaysInMonth) {
                    day = day - totalDaysInMonth;
                    month++;
                    if (month > 12) {
                        month = month - 12;
                        year++;
                    }
                }
            }
        }
        if (scheduling.getMinutes() > 0) {
            minute = minute + scheduling.getMinutes();
            if (minute >= 60) {
                minute = minute - 60;
                hour++;
                if (hour >= 24) {
                    hour = 0;
                    day++;
                    if (day >= totalDaysInMonth) {
                        day = day - totalDaysInMonth;
                        month++;
                        if (month > 12) {
                            month = month - 12;
                            year++;
                        }
                    }
                }
            }
        }
        if (second >= 60) {
            second = second - 60;
            minute++;
            if (minute >= 60) {
                minute = minute - 60;
                hour++;
                if (hour >= 24) {
                    hour = 0;
                    day++;
                    if (day >= totalDaysInMonth) {
                        day = day - totalDaysInMonth;
                        month++;
                        if (month > 12) {
                            month = month - 12;
                            year++;
                        }
                    }
                }
            }
        }
        LocalDateTime nextExecutionTime = LocalDateTime.of(year, month, day, hour, minute, second);
        scheduling.setNextExecutionTime(nextExecutionTime);
        schedulingRepository.save(scheduling);
    }

    @Transactional
   @Scheduled(fixedRate = 1000)
    public void calculateAndPrintPercentage() {
        List<ProgressBar> progressBarList = progressBarRepository.findAll();
        for (ProgressBar progressBar : progressBarList) {
            Long totalNoOfTestCases = progressBar.getTotalNoOfTestCases();
            Long executedTestCase = progressBar.getExecutedTestCaseCount();
            if (totalNoOfTestCases >= executedTestCase) {
                double percentage = ((double) executedTestCase / totalNoOfTestCases) * 100.0;
                int percentageInt = (int) percentage;
                ProgressResponse progressResponse = new ProgressResponse();
                progressResponse.setPercentage(percentageInt);
                progressResponse.setGroupName(progressBar.getTestGrouping().getName());
                progressResponse.setGroupId(progressBar.getTestGrouping().getId());
                simpMessagingTemplate.convertAndSend("/queue/percentage/schedule/" + progressBar.getTestGrouping().getProject().getId(), progressResponse);
                if (percentageInt == 100) {
                    TestGrouping testGrouping = progressBar.getTestGrouping();
                    testGrouping.setExecutionStatus(false);
                    progressBarRepository.deleteById(progressBar.getId());
                    List<ExecutedTestCase> executedTestCases = executedTestCaseRepository.findByTestGroupingId(testGrouping.getId());
                    for (ExecutedTestCase executedTestCase1 : executedTestCases) {
                        executedTestCaseRepository.deleteById(executedTestCase1.getId());
                    }
                }
                System.out.println("Percentage: " + progressResponse + "%");
            } else {
                System.out.println("Total number of test cases is zero.");
            }
        }
    }

    @Override
    public boolean checkStartDate(LocalDateTime startDate) {
        if(startDate.isAfter(LocalDateTime.now())) return true;
        return false;
    }

    @Override
    public boolean existsByTestCaseId(Long testCaseId) {
        return schedulingRepository.existsByTestGrouping_TestCases_Id(testCaseId);
    }

    @Override
    public boolean existsByTestGroupingId(Long id) {
        return schedulingRepository.existsByTestGroupingId(id);
    }

    @Override
    public boolean existsByScheduleOption(int month, int week, int minutes, int hour, int year,LocalDateTime startTime,Long projectId) {
        return schedulingRepository.existsByStartDateTimeAndYearAndMonthAndWeekAndHourAndMinutesAndTestGroupingProjectId(startTime,year,month,week,hour,minutes,projectId);
    }

    @Override
    public boolean isUpdateScheduleOptionExists(int month, int week, int minutes, int hour, int year, LocalDateTime startDateTime, Long id,Long projectId) {
        return schedulingRepository.existsByStartDateTimeAndYearAndMonthAndWeekAndHourAndMinutesAndTestGroupingProjectIdAndIdNot( startDateTime, year, month, week, hour, minutes, id,projectId);
    }
}