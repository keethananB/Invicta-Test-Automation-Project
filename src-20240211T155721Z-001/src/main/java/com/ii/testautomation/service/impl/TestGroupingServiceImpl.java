package com.ii.testautomation.service.impl;

import com.ii.testautomation.config.ProgressWebSocketHandler;
import com.ii.testautomation.dto.request.ExecutionRequest;
import com.ii.testautomation.dto.request.TestGroupingRequest;
import com.ii.testautomation.dto.response.*;
import com.ii.testautomation.dto.search.TestGroupingSearch;
import com.ii.testautomation.entities.*;
import com.ii.testautomation.repositories.*;
import com.ii.testautomation.response.common.PaginatedContentResponse;
import com.ii.testautomation.service.TestGroupingService;
import com.ii.testautomation.utils.Utils;
import com.querydsl.core.BooleanBuilder;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TestGroupingServiceImpl implements TestGroupingService {
    @Autowired
    private TestGroupingRepository testGroupingRepository;
    @Autowired
    private TestCasesRepository testCasesRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private TestScenariosRepository testScenarioRepository;
    @Autowired
    private TestTypesRepository testTypesRepository;
    @Autowired
    private SubModulesRepository subModulesRepository;
    @Autowired
    private ModulesRepository modulesRepository;
    @Autowired
    private MainModulesRepository mainModulesRepository;
    @Autowired
    private ExecutedTestCaseRepository executedTestCaseRepository;
    @Autowired
    private SchedulingRepository schedulingRepository;
    @Autowired
    private ProgressBarRepository progressBarRepository;
    @Autowired
    private ProgressWebSocketHandler progressWebSocketHandler;
    @Value("${jar.import.file.windows.path}")
    private String fileFolder;
    @Autowired
    private TaskScheduler taskScheduler;
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public boolean hasExcelFormat(List<MultipartFile> multipartFiles) {
        if (multipartFiles != null && !multipartFiles.isEmpty()) {
            for (MultipartFile multipartFile : multipartFiles
            ) {
                try {
                    if (Objects.requireNonNull(multipartFile.getOriginalFilename()).endsWith(".csv")) return true;
                    Workbook workbook = WorkbookFactory.create(multipartFile.getInputStream());
                    workbook.close();
                } catch (Exception e) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void saveTestGrouping(TestGroupingRequest testGroupingRequest, List<MultipartFile> excelFiles) {
        TestGrouping testGrouping = new TestGrouping();
        testGrouping.setName(testGroupingRequest.getName());
        TestTypes testTypes = new TestTypes();
        testTypes.setId(testGroupingRequest.getTestTypeId());
        testGrouping.setTestType(testTypes);
        List<TestCases> testCasesList = new ArrayList<>();
        if (testGroupingRequest.getSubModuleIds() != null && !testGroupingRequest.getSubModuleIds().isEmpty()) {
            for (Long subModuleId : testGroupingRequest.getSubModuleIds()) {
                List<TestCases> testCases = testCasesRepository.findAllTestCasesBySubModuleId(subModuleId);
                for (TestCases testCases1 : testCases) {
                    testCasesList.add(testCases1);
                }
            }
        }
        if (testGroupingRequest.getMainModuleIds() != null && !testGroupingRequest.getMainModuleIds().isEmpty()) {
            for (Long mainModuleId : testGroupingRequest.getMainModuleIds()) {
                List<TestCases> testCases = testCasesRepository.findBySubModule_MainModule_Id(mainModuleId);
                for (TestCases testCase1 : testCases) {
                    testCasesList.add(testCase1);
                }
            }
        }
        if (testGroupingRequest.getModuleIds() != null && !testGroupingRequest.getModuleIds().isEmpty()) {
            for (Long moduleId : testGroupingRequest.getModuleIds()) {
                List<TestCases> testCases = testCasesRepository.findBySubModule_MainModule_Modules_Id(moduleId);
                for (TestCases testCase1 : testCases) {
                    testCasesList.add(testCase1);
                }
            }
        }
        if (testGroupingRequest.getTestCaseId() != null && !testGroupingRequest.getTestCaseId().isEmpty()) {
            for (Long testCaseId : testGroupingRequest.getTestCaseId()) {
                TestCases testCases = testCasesRepository.findById(testCaseId).get();
                testCasesList.add(testCases);
            }
        }
        List<TestScenarios> testScenariosList = new ArrayList<>();
        if (testGroupingRequest.getTestScenarioIds() != null && !testGroupingRequest.getTestScenarioIds().isEmpty()) {
            for (Long testScenarioId : testGroupingRequest.getTestScenarioIds()) {
                TestScenarios testScenarios = testScenarioRepository.findById(testScenarioId).get();
                testScenariosList.add(testScenarios);
            }
        }
        testGrouping.setTestScenarios(testScenariosList);
        testGrouping.setTestCases(testCasesList);
        String folderPath = fileFolder + File.separator + projectRepository.findById(testGroupingRequest.getProjectId()).get().getName() + File.separator + testGroupingRequest.getName();
        List<String> filePaths = new ArrayList<>();
        try {
            File folder = new File(folderPath);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            testGrouping.setGroupPath(folderPath);
            if (excelFiles != null && !excelFiles.isEmpty()) {
                for (MultipartFile excelFile : excelFiles) {
                    String filename = excelFile.getOriginalFilename();
                    String filePath = folderPath + File.separator + filename;
                    File savedFile = new File(filePath);
                    excelFile.transferTo(savedFile);
                    filePaths.add(filePath);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        testGrouping.setExcelFilePath(filePaths);
        Project project = projectRepository.findById(testGroupingRequest.getProjectId()).get();
        testGrouping.setProject(project);
        testGroupingRepository.save(testGrouping);
    }

    @Override
    public boolean existsTestGroupingByTestScenarioId(Long id) {
        return testGroupingRepository.existsByTestScenariosId(id);
    }

    @Override
    public void updateTestGrouping(TestGroupingRequest testGroupingRequest, List<MultipartFile> excelFiles) {
        TestGrouping testGrouping = testGroupingRepository.findById(testGroupingRequest.getId()).get();
        TestTypes testTypes = testTypesRepository.findById(testGroupingRequest.getTestTypeId()).get();
        testGrouping.setTestType(testTypes);
        List<TestCases> testCasesList = new ArrayList<>();
        if (testGroupingRequest.getSubModuleIds() != null && !testGroupingRequest.getSubModuleIds().isEmpty()) {
            for (Long subModuleId : testGroupingRequest.getSubModuleIds()) {
                List<TestCases> testCases = testCasesRepository.findAllTestCasesBySubModuleId(subModuleId);
                for (TestCases testCases1 : testCases) {
                    testCasesList.add(testCases1);
                }
            }
        }
        if (testGroupingRequest.getMainModuleIds() != null && !testGroupingRequest.getMainModuleIds().isEmpty()) {
            for (Long mainModuleId : testGroupingRequest.getMainModuleIds()) {
                List<TestCases> testCases = testCasesRepository.findBySubModule_MainModule_Id(mainModuleId);
                for (TestCases testCase1 : testCases) {
                    testCasesList.add(testCase1);
                }
            }
        }
        if (testGroupingRequest.getModuleIds() != null && !testGroupingRequest.getModuleIds().isEmpty()) {
            for (Long moduleId : testGroupingRequest.getModuleIds()) {
                List<TestCases> testCases = testCasesRepository.findBySubModule_MainModule_Modules_Id(moduleId);
                for (TestCases testCase1 : testCases) {
                    testCasesList.add(testCase1);
                }
            }
        }
        if (testGroupingRequest.getTestCaseId() != null && !testGroupingRequest.getTestCaseId().isEmpty()) {
            for (Long testCaseId : testGroupingRequest.getTestCaseId()
            ) {
                TestCases testCases = testCasesRepository.findById(testCaseId).get();
                testCasesList.add(testCases);
            }
        }
        List<TestScenarios> testScenariosList = new ArrayList<>();
        if (testGroupingRequest.getTestScenarioIds() != null && !testGroupingRequest.getTestScenarioIds().isEmpty()) {
            for (Long testScenarioId : testGroupingRequest.getTestScenarioIds()) {
                TestScenarios testScenarios = testScenarioRepository.findById(testScenarioId).get();
                testScenariosList.add(testScenarios);
            }
        }
        testGrouping.setTestScenarios(testScenariosList);
        testGrouping.setTestCases(testCasesList);
        String newGroupFolderPath = fileFolder + File.separator + projectRepository.findById(testGroupingRequest.getProjectId()).get().getName() + File.separator + testGroupingRequest.getName();
        String existingGroupFolderPath = testGrouping.getGroupPath();
        File existingGroupFolder = new File(existingGroupFolderPath);
        File newGroupFolder = new File(newGroupFolderPath);
        List<String> excelPaths = testGrouping.getExcelFilePath();
        List<String> newExcelPathList = new ArrayList<>();
        if (existingGroupFolder.exists()) {
            existingGroupFolder.renameTo(newGroupFolder);
        }
        testGrouping.setGroupPath(newGroupFolderPath);
        if (excelPaths != null && !excelPaths.isEmpty()) {
            for (String excelPath : excelPaths
            ) {
                Path excel = Paths.get(excelPath);
                String excelFileName = excel.getFileName().toString();
                String newExcelPath = newGroupFolderPath + File.separator + excelFileName;
                newExcelPathList.add(newExcelPath);
            }
        }
        if (excelFiles != null && !excelFiles.isEmpty()) {
            try {
                for (MultipartFile multipartFile : excelFiles
                ) {
                    String excelFileName = multipartFile.getOriginalFilename();
                    String newExcelFilePath = newGroupFolderPath + File.separator + excelFileName;
                    File excelFile = new File(newExcelFilePath);
                    multipartFile.transferTo(excelFile);
                    newExcelPathList.add(newExcelFilePath);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        testGrouping.setExcelFilePath(newExcelPathList);
        testGrouping.setName(testGroupingRequest.getName());
        testGroupingRepository.save(testGrouping);
    }

    @Override
    public boolean allTestCasesInSameProject(List<Long> testCaseIds) {
        Set<Long> uniqueProjectIds = new HashSet<>();
        for (Long testCaseId : testCaseIds) {
            Long projectId = testCasesRepository.findById(testCaseId).get().getSubModule().getMainModule().getModules().getProject().getId();
            if (!uniqueProjectIds.contains(projectId)) {
                uniqueProjectIds.add(projectId);
            }
        }
        return uniqueProjectIds.size() == 1;
    }

    @Override
    public boolean existsByTestGroupingId(Long testGroupingId) {
        return testGroupingRepository.existsById(testGroupingId);
    }

    @Override
    public boolean existsById(Long id) {
        return testGroupingRepository.existsById(id);
    }

    @Override
    public void deleteTestGroupingById(Long id, Long projectId) {
        String projectName = projectRepository.findById(projectId).get().getName();
        String testGroupingDirectoryPath = fileFolder + File.separator + projectName + File.separator + testGroupingRepository.findById(id).get().getName();
        deleteTestGroupingFolder(testGroupingDirectoryPath);
        testGroupingRepository.deleteById(id);
    }

    private void deleteTestGroupingFolder(String folderPath) {
        File directory = new File(folderPath);
        if (directory.exists()) {
            if (directory.isDirectory()) {
                File[] files = directory.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.isDirectory()) {
                            deleteTestGroupingFolder(file.getAbsolutePath());
                        } else {
                            file.delete();
                        }
                    }
                }
                directory.delete();
            } else {
                directory.delete();
            }
        }
    }

    @Override
    public boolean existsByTestCasesId(Long testCaseId) {
        return testGroupingRepository.existsByTestCasesId(testCaseId);
    }

    @Override
    public boolean existsByTestTypesId(Long testTypeId) {
        return testGroupingRepository.existsByTestTypeId(testTypeId);
    }

    @Override
    public TestGroupingResponse getTestGroupingById(Long id) {
        TestGrouping testGrouping = testGroupingRepository.findById(id).get();

        TestGroupingResponse testGroupingResponse = new TestGroupingResponse();
        BeanUtils.copyProperties(testGrouping, testGroupingResponse);
        testGroupingResponse.setTestTypeName(testGrouping.getTestType().getName());
        testGroupingResponse.setTestTypeId(testGrouping.getTestType().getId());
        List<String> testCaseNames = new ArrayList<>();
        List<String> testScenarioNames = new ArrayList<>();
        List<Long> testCaseIds = new ArrayList<>();
        List<Long> testScenarioIds = new ArrayList<>();
        Set<String> addedTestCaseNames = new HashSet<>();
        List<TestCaseResponse> testCaseResponseList = new ArrayList<>();
        List<TestScenariosResponse> testScenariosResponseList = new ArrayList<>();

        for (TestCases testCase : testGrouping.getTestCases()) {
            TestCaseResponse testCaseResponse = new TestCaseResponse();
            String testCaseName = testCase.getName().substring(testCase.getName().lastIndexOf(".") + 1);
            if (!addedTestCaseNames.contains(testCaseName)) {
                testCaseNames.add(testCaseName);
                testCaseIds.add(testCase.getId());
                addedTestCaseNames.add(testCaseName);
            }
            BeanUtils.copyProperties(testCase, testCaseResponse);
            testCaseResponseList.add(testCaseResponse);
        }

        for (TestScenarios testScenario : testGrouping.getTestScenarios()) {
            TestScenariosResponse testScenariosResponse = new TestScenariosResponse();
            testScenarioNames.add(testScenario.getName());
            testScenarioIds.add(testScenario.getId());
            BeanUtils.copyProperties(testScenario, testScenariosResponse);
            testScenariosResponseList.add(testScenariosResponse);
        }
        List<String> excelFileNames = testGrouping.getExcelFilePath();
        List<String> newExcelFileNames = new ArrayList<>();
        if (excelFileNames != null && !excelFileNames.isEmpty()) {
            for (String excelPath : excelFileNames
            ) {
                Path excel = Paths.get(excelPath);
                String excelFileName = excel.getFileName().toString();
                newExcelFileNames.add(excelFileName);
            }
        }
        testGroupingResponse.setTestCaseResponseList(testCaseResponseList);
        testGroupingResponse.setTestScenariosResponseList(testScenariosResponseList);
        testGroupingResponse.setExcelFile(newExcelFileNames);
        testGroupingResponse.setTestCaseIds(testCaseIds);
        testGroupingResponse.setTestCaseName(testCaseNames);
        testGroupingResponse.setTestScenarioIds(testScenarioIds);
        testGroupingResponse.setTestScenarioName(testScenarioNames);
        return testGroupingResponse;
    }

    @Override
    public boolean existByProjectId(Long projectId) {
        return testGroupingRepository.existsByProjectId(projectId);
    }

    @Override
    public List<TestGroupingResponse> getAllTestGroupingByProjectId(Pageable pageable, PaginatedContentResponse.Pagination pagination, Long projectId) {
        List<TestGroupingResponse> testGroupingResponseList = new ArrayList<>();
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        BooleanBuilder testScenariosbooleanBuilder = new BooleanBuilder();
        QTestGrouping qTestGrouping = QTestGrouping.testGrouping;

        if (qTestGrouping.testCases != null &&
                qTestGrouping.testCases.any().subModule != null &&
                qTestGrouping.testCases.any().subModule.mainModule != null &&
                qTestGrouping.testCases.any().subModule.mainModule.modules != null &&
                qTestGrouping.testCases.any().subModule.mainModule.modules.name != null) {
            booleanBuilder.and(qTestGrouping.project.id.eq(projectId));

        }
        Page<TestGrouping> testGroupingPageByTestCase = testGroupingRepository.findByProjectId(projectId, pageable);
        if (qTestGrouping.testScenarios != null &&
                qTestGrouping.testScenarios.any().testCases != null &&
                qTestGrouping.testScenarios.any().testCases.any().subModule != null &&
                qTestGrouping.testScenarios.any().testCases.any().subModule.mainModule != null &&
                qTestGrouping.testScenarios.any().testCases.any().subModule.mainModule.modules != null &&
                qTestGrouping.testScenarios.any().testCases.any().subModule.mainModule.modules.name != null) {
            testScenariosbooleanBuilder.and(qTestGrouping.testCases.any().subModule.mainModule.modules.project.id.eq(projectId));

        }
        for (TestGrouping testGrouping : testGroupingPageByTestCase) {
            TestGroupingResponse testGroupingResponse = new TestGroupingResponse();
            if (testGrouping.getTestType() != null) {
                testGroupingResponse.setTestTypeName(testGrouping.getTestType().getName());
                testGroupingResponse.setTestTypeId(testGrouping.getTestType().getId());
            }
            if (testGrouping.getName() != null) {
                testGroupingResponse.setName(testGrouping.getName());
            }
            if (testGrouping.getId() != null) {
                testGroupingResponse.setId(testGrouping.getId());
            }
            List<String> testCaseNames = new ArrayList<>();
            List<Long> testCaseIds = new ArrayList<>();
            if (testGrouping.getTestCases() != null && !testGrouping.getTestCases().isEmpty()) {
                for (TestCases testCases : testGrouping.getTestCases()) {
                    if (testCases.getName() != null) {
                        testCaseNames.add(testCases.getName());
                    }
                    if (testCases.getId() != null) {
                        testCaseIds.add(testCases.getId());
                    }
                }
            }
            List<String> testScenariosNames = new ArrayList<>();
            List<Long> testScenariosIds = new ArrayList<>();
            if (testGrouping.getTestScenarios() != null && !testGrouping.getTestScenarios().isEmpty()) {
                for (TestScenarios testScenarios : testGrouping.getTestScenarios()) {
                    if (testScenarios.getName() != null) {
                        testScenariosNames.add(testScenarios.getName());
                    }
                    if (testScenarios.getId() != null) {
                        testScenariosIds.add(testScenarios.getId());
                    }
                }
            }
            List<String> sortedTestCaseNames = testCaseNames.stream().distinct().collect(Collectors.toList());
            List<String> sortedTestScenarioNames = testScenariosNames.stream().distinct().collect(Collectors.toList());
            List<Long> sortedTestScenariosIds = testScenariosIds.stream().distinct().collect(Collectors.toList());
            List<Long> sortedTestCasesIds = testCaseIds.stream().distinct().collect(Collectors.toList());

            testGroupingResponse.setTestCaseIds(sortedTestCasesIds);
            testGroupingResponse.setTestScenarioIds(sortedTestScenariosIds);
            testGroupingResponse.setTestCaseName(sortedTestCaseNames);
            testGroupingResponse.setTestScenarioName(sortedTestScenarioNames);
            testGroupingResponseList.add(testGroupingResponse);
        }

        return testGroupingResponseList;
    }

    private Page<TestGrouping> combineAndRemoveDuplicates(Page<TestGrouping> page1, Page<TestGrouping> page2) {
        Set<TestGrouping> uniqueTestGroupings = new HashSet<>(page1.getContent());
        uniqueTestGroupings.addAll(page2.getContent());

        List<TestGrouping> combinedContent = new ArrayList<>(uniqueTestGroupings);

        return new PageImpl<>(combinedContent, page1.getPageable(), combinedContent.size());
    }

    @Override
    public void execution(ExecutionRequest executionRequest) throws IOException {
        TestGrouping testGrouping = testGroupingRepository.findById(executionRequest.getTestGroupingId()).orElse(null);
        testGrouping.setExecutionStatus(true);
        testGroupingRepository.save(testGrouping);
        int mapSize = executionRequest.getTestScenario().size() + executionRequest.getTestCase().size();
        for (int i = 0; i <= mapSize; i++) {
            for (Map.Entry<Integer, Long> entry : executionRequest.getTestScenario().entrySet()) {
                if (entry.getKey() == i) {
                    TestScenarios testScenarios = testScenarioRepository.findById(entry.getValue()).get();
                    testScenarios.setExecutionStatus(true);
                    List<TestCases> testCasesList = testScenarios.getTestCases();
                    for (TestCases testCases : testCasesList) {
                        ExecutedTestCase executedTestCase = new ExecutedTestCase();
                        executedTestCase.setTestCases(testCases);
                        executedTestCase.setTestGrouping(testGrouping);
                        executedTestCaseRepository.save(executedTestCase);
                        testCases.setExecutionStatus(true);
                    }
                }
            }
            for (Map.Entry<Integer, Long> entry : executionRequest.getTestCase().entrySet()) {
                if (entry.getKey() == i) {
                    TestCases testCases = testCasesRepository.findById(entry.getValue()).get();
                    ExecutedTestCase executedTestCase = new ExecutedTestCase();
                    executedTestCase.setTestCases(testCases);
                    executedTestCase.setTestGrouping(testGrouping);
                    executedTestCaseRepository.save(executedTestCase);
                    testCases.setExecutionStatus(true);
                    break;
                }
            }
        }
        List<String> excelFiles = testGrouping.getExcelFilePath();
        String projectPath = projectRepository.findById(executionRequest.getProjectId()).get().getProjectPath();
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
        jarExecution(executionRequest.getProjectId(),executionRequest.getTestGroupingId());
    }

    private void jarExecution(Long projectId,Long groupId) {
        String savedFilePath = projectRepository.findById(projectId).get().getJarFilePath();
        File jarFile = new File(savedFilePath);
        String jarFileName = jarFile.getName();
        String jarDirectory = jarFile.getParent();
        try {
            ProgressResponse progressResponse = new ProgressResponse();
            progressResponse.setProjectId(projectId);
            simpMessagingTemplate.convertAndSend("/queue/percentage/group/"+groupId, progressResponse);
            ProcessBuilder runProcessBuilder = new ProcessBuilder("java", "-jar", jarFileName);
            runProcessBuilder.directory(new File(jarDirectory));
            runProcessBuilder.redirectErrorStream(true);
            Process runProcess = runProcessBuilder.start();
            runProcess.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
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
                simpMessagingTemplate.convertAndSend("/queue/percentage/group/" + progressBar.getTestGrouping().getId(), progressResponse);
                if (percentageInt == 100) {
                    TestGrouping testGrouping = progressBar.getTestGrouping();
                    testGrouping.setExecutionStatus(false);
                    progressBarRepository.deleteById(progressBar.getId());
                    List<ExecutedTestCase> executedTestCases = executedTestCaseRepository.findByTestGroupingId(testGrouping.getId());
                    for (ExecutedTestCase executedTestCase1 : executedTestCases) {
                        executedTestCaseRepository.deleteById(executedTestCase1.getId());
                    }
                }
            }
        }
    }

    @Override
    public boolean folderExists(Long groupId) {
        String groupPath = testGroupingRepository.findById(groupId).get().getGroupPath();
        if (groupPath != null) {
            File file = new File(groupPath);
            if (file.exists()) return true;
        }
        return false;
    }

    @Override
    public List<SchedulingGroupingTestCases> getScheduledTestCases(Long groupId) {
        List<Scheduling> schedulingList=schedulingRepository.findByTestGroupingId(groupId);
        List<SchedulingGroupingTestCases> schedulingGroupingTestCases=new ArrayList<>();
        for (Scheduling scheduling : schedulingList
        ){
            List<TestCases> testCasesList=scheduling.getTestCases();
            if(testCasesList!=null) {
                for (TestCases testCases : testCasesList
                ) {
                    SchedulingGroupingTestCases schedulingGroupingTestCases1 = new SchedulingGroupingTestCases();
                    schedulingGroupingTestCases1.setSchedulingId(scheduling.getId());
                    schedulingGroupingTestCases1.setTestCaseId(testCases.getId());
                    schedulingGroupingTestCases1.setTestCaseName(testCases.getName());
                    schedulingGroupingTestCases1.setGroupId(groupId);
                    schedulingGroupingTestCases.add(schedulingGroupingTestCases1);
                }
            }
        }
        return schedulingGroupingTestCases;
    }

    @Override
    public List<ScheduledTestScenarioResponse> getScheduledTestScenario(Long groupId) {
        List<Scheduling> schedulingList=schedulingRepository.findByTestGroupingId(groupId);
        List<ScheduledTestScenarioResponse> scheduledTestScenarioResponses=new ArrayList<>();
        for (Scheduling scheduling : schedulingList
        ) {
            List<TestScenarios> testScenariosList = scheduling.getTestScenarios();
            if (testScenariosList != null) {
                for (TestScenarios testScenarios : testScenariosList
                ) {
                    ScheduledTestScenarioResponse schedulingTestScenarioResponse = new ScheduledTestScenarioResponse();
                    schedulingTestScenarioResponse.setSchedulingId(scheduling.getId());
                    schedulingTestScenarioResponse.setGroupId(groupId);
                    schedulingTestScenarioResponse.setTestScenarioId(testScenarios.getId());
                    schedulingTestScenarioResponse.setTestScenarioName(testScenarios.getName());
                    Map<Long,String> testCasesMap=new HashMap<>();
                    for (TestCases testCases : testScenarios.getTestCases()
                    ) {
                        testCasesMap.put(testCases.getId(),testCases.getName());
                    }
                    schedulingTestScenarioResponse.setTestCases(testCasesMap);
                    scheduledTestScenarioResponses.add(schedulingTestScenarioResponse);
                }
            }
        }
        return scheduledTestScenarioResponses;
    }

    @Override
    public boolean existsByTestGroupingNameByTestCaseAndProjectId(String name, Long projectId) {
        return testGroupingRepository.existsByNameIgnoreCaseAndTestCases_SubModule_MainModule_Modules_Project_Id(name, projectId);
    }

    @Override
    public boolean existsByTestGroupingNameByTestScenarioAndProjectId(String name, Long projectId) {
        return testGroupingRepository.existsByNameIgnoreCaseAndTestScenarios_testCases_SubModule_MainModule_Modules_Project_Id(name, projectId);
    }

    @Override
    public boolean isUpdateTestGroupingNameByProjectId(String name, Long projectId, Long groupingId) {
        return testGroupingRepository.existsByNameIgnoreCaseAndTestCases_SubModule_MainModule_Modules_Project_IdAndIdNot(name, projectId, groupingId);
    }

    @Override
    public boolean hasExcelPath(Long testGroupingId) {
        List<String> excelFilePath = testGroupingRepository.findById(testGroupingId).get().getExcelFilePath();
        if (excelFilePath.isEmpty()) return false;
        return true;

    }

    @Override
    public List<TestGroupingResponse> getAllTestGroupingByTestCaseId(Long testCaseId) {
        List<TestGroupingResponse> testGroupingResponseList = new ArrayList<>();
        List<TestGrouping> testGroupingList = testGroupingRepository.findAllTestGroupingByTestCasesId(testCaseId);

        for (TestGrouping testGrouping : testGroupingList) {
            TestGroupingResponse testGroupingResponse = new TestGroupingResponse();
            BeanUtils.copyProperties(testGrouping, testGroupingResponse);
            testGroupingResponse.setTestTypeName(testGrouping.getTestType().getName());
            List<String> testCaseNames = new ArrayList<>();
            List<String> subModuleName = new ArrayList<>();
            List<String> mainModulesName = new ArrayList<>();
            List<String> modulesName = new ArrayList<>();
            for (TestCases testCase : testGrouping.getTestCases()) {
                testCaseNames.add(testCase.getName());
                subModuleName.add(testCase.getSubModule().getName());
                mainModulesName.add(testCase.getSubModule().getMainModule().getName());
                modulesName.add(testCase.getSubModule().getMainModule().getModules().getName());
            }
            testGroupingResponse.setTestCaseName(testCaseNames);
            testGroupingResponse.setSubModuleName(subModuleName);
            testGroupingResponse.setMainModuleName(mainModulesName);
            testGroupingResponse.setModuleName(modulesName);
            testGroupingResponseList.add(testGroupingResponse);
        }
        return testGroupingResponseList;
    }

    @Override
    public List<TestGroupingResponse> getAllTestGroupingByTestTypeId(Long testTypeId) {
        List<TestGroupingResponse> testGroupingResponseList = new ArrayList<>();
        List<TestGrouping> testGroupingList = testGroupingRepository.findAllTestGroupingByTestTypeId(testTypeId);
        for (TestGrouping testGrouping : testGroupingList) {
            TestGroupingResponse testGroupingResponse = new TestGroupingResponse();
            testGroupingResponse.setTestTypeName(testGrouping.getTestType().getName());
            List<TestCases> testCasesList = testGrouping.getTestCases();
            List<String> subModuleNameList = new ArrayList<>();
            List<String> mainMooduleNameList = new ArrayList<>();
            List<String> moduleNameList = new ArrayList<>();
            List<String> testCaseNameList = new ArrayList<>();

            for (TestCases testCases : testCasesList) {
                testCaseNameList.add(testCases.getName());
                subModuleNameList.add(testCases.getSubModule().getName());
                mainMooduleNameList.add(testCases.getSubModule().getMainModule().getName());
                moduleNameList.add(testCases.getSubModule().getMainModule().getModules().getName());

            }
            BeanUtils.copyProperties(testGrouping, testGroupingResponse);
            testGroupingResponse.setTestCaseName(testCaseNameList);
            testGroupingResponse.setSubModuleName(subModuleNameList);
            testGroupingResponse.setMainModuleName(mainMooduleNameList);
            testGroupingResponse.setModuleName(moduleNameList);
            testGroupingResponseList.add(testGroupingResponse);

        }
        return testGroupingResponseList;

    }

    @Override
    public List<TestGroupingResponse> multiSearchTestGrouping(Pageable pageable, PaginatedContentResponse.Pagination pagination, TestGroupingSearch testGroupingSearch) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (Utils.isNotNullAndEmpty(testGroupingSearch.getName())) {
            booleanBuilder.and(QTestGrouping.testGrouping.name.containsIgnoreCase(testGroupingSearch.getName()));
        }

        if (Utils.isNotNullAndEmpty(testGroupingSearch.getTestTypeName())) {
            booleanBuilder.and(QTestGrouping.testGrouping.testType.name.containsIgnoreCase(testGroupingSearch.getTestTypeName()));
        }
        List<TestGroupingResponse> testGroupingResponseList = new ArrayList<>();
        Page<TestGrouping> testGroupingPage = testGroupingRepository.findAll(booleanBuilder, pageable);
        pagination.setTotalRecords(testGroupingPage.getTotalElements());
        pagination.setPageSize(testGroupingPage.getTotalPages());
        for (TestGrouping testGrouping : testGroupingPage) {
            TestGroupingResponse testGroupingResponse = new TestGroupingResponse();
            testGroupingResponse.setTestTypeName(testGrouping.getTestType().getName());
            List<TestCases> testCasesList = testGrouping.getTestCases();
            List<String> subModuleNameList = new ArrayList<>();
            List<String> mainMooduleNameList = new ArrayList<>();
            List<String> moduleNameList = new ArrayList<>();
            List<String> testCaseNameList = new ArrayList<>();
            for (TestCases testCases : testCasesList) {

                testCaseNameList.add(testCases.getName());
                subModuleNameList.add(testCases.getSubModule().getName());
                mainMooduleNameList.add(testCases.getSubModule().getMainModule().getName());
                moduleNameList.add(testCases.getSubModule().getMainModule().getModules().getName());
            }
            BeanUtils.copyProperties(testGrouping, testGroupingResponse);
            testGroupingResponse.setTestCaseName(testCaseNameList);
            testGroupingResponse.setSubModuleName(subModuleNameList);
            testGroupingResponse.setMainModuleName(mainMooduleNameList);
            testGroupingResponse.setModuleName(moduleNameList);
            testGroupingResponseList.add(testGroupingResponse);
        }
        return testGroupingResponseList;
    }
}
