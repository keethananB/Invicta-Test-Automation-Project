package com.ii.testautomation.service.impl;

import com.ii.testautomation.dto.request.TestCaseRequest;
import com.ii.testautomation.dto.response.TestCaseResponse;
import com.ii.testautomation.dto.search.TestCaseSearch;
import com.ii.testautomation.entities.SubModules;
import com.ii.testautomation.entities.TestCases;
import com.ii.testautomation.entities.QTestCases;
import com.ii.testautomation.entities.QMainModules;
import com.ii.testautomation.repositories.SubModulesRepository;
import com.ii.testautomation.repositories.TestCasesRepository;
import com.ii.testautomation.response.common.PaginatedContentResponse;
import com.ii.testautomation.service.TestCasesService;
import com.ii.testautomation.utils.Utils;
import com.querydsl.core.BooleanBuilder;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

@SuppressWarnings("ALL")
@Service
public class TestCasesServiceImpl implements TestCasesService {
    @Autowired
    private TestCasesRepository testCasesRepository;
    @Autowired
    private SubModulesRepository subModulesRepository;

    @Override
    public void saveTestCase(TestCaseRequest testCaseRequest) {

        TestCases testCases = new TestCases();
        SubModules subModules = subModulesRepository.findById(testCaseRequest.getSubModuleId()).get();
        testCases.setSubModule(subModules);
        BeanUtils.copyProperties(testCaseRequest, testCases);
        testCasesRepository.save(testCases);
    }

    @Override
    public boolean existsByTestCasesId(Long id) {
        if (id == null) {
            return false;
        }
        return testCasesRepository.existsById(id);
    }

    @Override
    public boolean existsByTestCasesName(String testCaseName, Long subModulesId) {
        return testCasesRepository.existsByNameIgnoreCaseAndSubModuleId(testCaseName, subModulesId);
    }

    @Override
    public TestCaseResponse getById(Long id) {
        TestCaseResponse testCaseResponse = new TestCaseResponse();
        TestCases testCases = testCasesRepository.findById(id).get();
        testCaseResponse.setProjectId(testCases.getSubModule().getMainModule().getModules().getProject().getId());
        testCaseResponse.setProjectName(testCases.getSubModule().getMainModule().getModules().getProject().getName());
        testCaseResponse.setModuleId(testCases.getSubModule().getMainModule().getModules().getId());
        testCaseResponse.setModuleId(testCases.getSubModule().getId());
        testCaseResponse.setMainModuleId(testCases.getSubModule().getMainModule().getId());
        testCaseResponse.setModuleName(testCases.getSubModule().getMainModule().getModules().getName());
        testCaseResponse.setMainModuleName(testCases.getSubModule().getMainModule().getName());
        testCaseResponse.setSubModuleName(testCases.getSubModule().getName());
        testCaseResponse.setName(testCases.getName());
        testCaseResponse.setId(testCases.getId());
        testCaseResponse.setDescription(testCases.getDescription());
        return testCaseResponse;
    }

    @Override
    public boolean isUpdateTestCaseNameExists(String name, Long id, Long subModuleId) {
        return testCasesRepository.existsByNameIgnoreCaseAndSubModuleIdAndIdNot(name, subModuleId, id);
    }

    @Override
    public boolean isUpdateTestCaseNameExistsSubString(String name, Long id, Long subModuleId) {
        List<TestCases> testCasesList = testCasesRepository.findBySubModuleIdAndIdNot(subModuleId, id);
        for (TestCases testCases : testCasesList
        ) {
            String listTestCaseName = testCases.getName().substring(testCases.getName().lastIndexOf(".") + 1);
            if (listTestCaseName.equals(name)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<TestCaseResponse> multiSearchTestCase(Pageable pageable, PaginatedContentResponse.Pagination pagination, TestCaseSearch testCaseSearch) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QTestCases qTestCases = QTestCases.testCases;
        QMainModules qMainModule = QMainModules.mainModules;
        if (Utils.isNotNullAndEmpty(testCaseSearch.getName())) {
            booleanBuilder.and(qTestCases.name.containsIgnoreCase(testCaseSearch.getName()));
        }
        if (Utils.isNotNullAndEmpty(testCaseSearch.getSubModuleName())) {
            booleanBuilder.and(qTestCases.subModule.name.containsIgnoreCase(testCaseSearch.getSubModuleName()));
        }

        if (Utils.isNotNullAndEmpty(testCaseSearch.getMainModuleName())) {
            booleanBuilder.and(qTestCases.subModule.mainModule.name.containsIgnoreCase(testCaseSearch.getMainModuleName()));
        }
        if (Utils.isNotNullAndEmpty(testCaseSearch.getModuleName())) {
            if (qTestCases.subModule != null &&
                    qTestCases.subModule.mainModule != null &&
                    qTestCases.subModule.mainModule.modules != null &&
                    qTestCases.subModule.mainModule.modules.name != null) {

                booleanBuilder.and(
                        qTestCases.subModule.mainModule.modules.name.containsIgnoreCase(testCaseSearch.getModuleName())
                );
            }
        }
        if (Utils.isNotNullAndEmpty(testCaseSearch.getProjectName())) {
            if (qTestCases.subModule != null &&
                    qTestCases.subModule.mainModule != null &&
                    qTestCases.subModule.mainModule.modules != null &&
                    qTestCases.subModule.mainModule.modules.project != null &&
                    qTestCases.subModule.mainModule.modules.project.name != null) {

                booleanBuilder.and(
                        qTestCases.subModule.mainModule.modules.project.name.containsIgnoreCase(testCaseSearch.getProjectName())
                );
            }
        }

        List<TestCaseResponse> testCaseResponseList = new ArrayList<>();
        Page<TestCases> testCasesPage = testCasesRepository.findAll(booleanBuilder, pageable);
        pagination.setTotalRecords(testCasesPage.getTotalElements());
        pagination.setPageSize(testCasesPage.getTotalPages());
        for (TestCases testCases : testCasesPage) {
            TestCaseResponse testCaseResponse = new TestCaseResponse();
            testCaseResponse.setProjectId(testCases.getSubModule().getMainModule().getModules().getProject().getId());
            testCaseResponse.setProjectName(testCases.getSubModule().getMainModule().getModules().getProject().getName());
            testCaseResponse.setModuleId(testCases.getSubModule().getMainModule().getModules().getId());
            testCaseResponse.setSubModuleId(testCases.getSubModule().getId());
            testCaseResponse.setMainModuleId(testCases.getSubModule().getMainModule().getId());
            testCaseResponse.setModuleName(testCases.getSubModule().getMainModule().getModules().getName());
            testCaseResponse.setMainModuleName(testCases.getSubModule().getMainModule().getName());
            testCaseResponse.setSubModuleName(testCases.getSubModule().getName());
            testCaseResponse.setName(testCases.getName().substring(testCases.getName().lastIndexOf(".") + 1));
            testCaseResponse.setId(testCases.getId());
            testCaseResponse.setDescription(testCases.getDescription());
            testCaseResponseList.add(testCaseResponse);
        }
        return testCaseResponseList;
    }

    @Override
    public List<TestCaseResponse> getAllTestCaseBySubModuleId(Long subModuleId) {
        List<TestCaseResponse> testCaseResponseList = new ArrayList<>();
        List<TestCases> testCasesList = testCasesRepository.findAllTestCasesBySubModuleId(subModuleId);
        for (TestCases testCases : testCasesList) {
            TestCaseResponse testCaseResponse = new TestCaseResponse();
            testCaseResponse.setProjectId(testCases.getSubModule().getMainModule().getModules().getProject().getId());
            testCaseResponse.setProjectName(testCases.getSubModule().getMainModule().getModules().getProject().getName());
            testCaseResponse.setModuleId(testCases.getSubModule().getMainModule().getModules().getId());
            testCaseResponse.setSubModuleId(testCases.getSubModule().getId());
            testCaseResponse.setMainModuleId(testCases.getSubModule().getMainModule().getId());
            testCaseResponse.setModuleName(testCases.getSubModule().getMainModule().getModules().getName());
            testCaseResponse.setMainModuleName(testCases.getSubModule().getMainModule().getName());
            testCaseResponse.setSubModuleName(testCases.getSubModule().getName());
            BeanUtils.copyProperties(testCases, testCaseResponse);
            testCaseResponseList.add(testCaseResponse);
        }
        return testCaseResponseList;
    }

    @Override
    public void DeleteTestCaseById(Long id) {
        testCasesRepository.deleteById(id);
    }

    @Override
    public boolean existsBySubModuleId(Long subModuleId) {
        return testCasesRepository.existsBySubModuleId(subModuleId);
    }

    @Override
    public boolean hasExcelFormat(MultipartFile multipartFile) {
        try {
            Workbook workbook = WorkbookFactory.create(multipartFile.getInputStream());
            workbook.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Map<Integer, TestCaseRequest> csvToTestCaseRequest(InputStream inputStream, Long projectId) {
        Map<Integer, TestCaseRequest> testCaseRequestList = new HashMap<>();
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();
            for (CSVRecord csvRecord : csvRecords) {
                TestCaseRequest testCaseRequest = new TestCaseRequest();
                testCaseRequest.setDescription(csvRecord.get("description"));
                testCaseRequest.setName(csvRecord.get("name"));
                testCaseRequest.setSubModuleName(csvRecord.get("submodule_name"));
                if (!csvRecord.get("submodule_name").isEmpty()) {
                    Long subModuleId = subModulesRepository.findByNameIgnoreCaseAndMainModule_Modules_ProjectId(csvRecord.get("submodule_name"), projectId).getId();
                    testCaseRequest.setSubModuleId(subModuleId);
                } else {
                    testCaseRequest.setSubModuleId(null);
                }
                testCaseRequestList.put(Math.toIntExact(csvRecord.getRecordNumber() + 1), testCaseRequest);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse CSV file: " + e.getMessage());
        }
        return testCaseRequestList;
    }

    @Override
    public Map<Integer, TestCaseRequest> excelToTestCaseRequest(MultipartFile multipartFile, Long projectId) {
        Map<Integer, TestCaseRequest> testCaseRequestList = new HashMap<>();
        try {
            Workbook workbook = new XSSFWorkbook(multipartFile.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);
            Map<String, Integer> columnMap = getColumnMap(headerRow);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                TestCaseRequest testCaseRequest = new TestCaseRequest();
                testCaseRequest.setDescription(getStringCellValue(row.getCell(columnMap.get("description"))));
                testCaseRequest.setName(getStringCellValue(row.getCell(columnMap.get("name"))));
                testCaseRequest.setSubModuleName(getStringCellValue(row.getCell(columnMap.get("submodule_name"))));
                Long subModuleId = subModulesRepository.findByNameIgnoreCaseAndMainModule_Modules_ProjectId(getStringCellValue(row.getCell(columnMap.get("submodule_name"))), projectId).getId();
                testCaseRequest.setSubModuleId(subModuleId);
                testCaseRequest.setProject_id(projectId);
                testCaseRequestList.put(row.getRowNum() + 1, testCaseRequest);
            }
            workbook.close();
        } catch (IOException e) {
            throw new RuntimeException("Failed to Parse Excel File: " + e.getMessage());
        }
        return testCaseRequestList;
    }

    @Override
    public boolean isExcelHeaderMatch(MultipartFile multipartFile) {
        try (InputStream inputStream = multipartFile.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);
            String[] actualHeaders = new String[headerRow.getLastCellNum()];
            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                Cell cell = headerRow.getCell(i);
                actualHeaders[i] = cell.getStringCellValue().toLowerCase();
            }
            String[] expectedHeader = {"description", "name", "submodule_name"};
            Set<String> expectedHeaderSet = new HashSet<>(Arrays.asList(expectedHeader));
            Set<String> actualHeaderSet = new HashSet<>(Arrays.asList(actualHeaders));
            return expectedHeaderSet.equals(actualHeaderSet);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isCSVHeaderMatch(MultipartFile multipartFile) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(multipartFile.getInputStream()))) {
            String line = reader.readLine();
            String[] actualHeaders = line.split(",");
            for (int i = 0; i < actualHeaders.length; i++) {
                actualHeaders[i] = actualHeaders[i].toLowerCase();
            }
            String[] expectedHeader = {"description", "name", "submodule_name"};
            Set<String> expectedHeaderSet = new HashSet<>(Arrays.asList(expectedHeader));
            Set<String> actualHeaderSet = new HashSet<>(Arrays.asList(actualHeaders));
            return expectedHeaderSet.equals(actualHeaderSet);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void addToErrorMessages(Map<String, List<Integer>> errorMessages, String key, int value) {
        List<Integer> errorList = errorMessages.getOrDefault(key, new ArrayList<>());
        errorList.add(value);
        errorMessages.put(key, errorList);
    }

    @Override
    public List<TestCaseResponse> getAllTestcasesByProjectIdWithPagination(Long projectId, Pageable pageable, PaginatedContentResponse.Pagination pagination) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        List<TestCaseResponse> testCaseResponseList = new ArrayList<>();
        Page<TestCases> testCasesPage = testCasesRepository.findBySubModuleMainModuleModulesProjectId(projectId, pageable);
        pagination.setTotalRecords(testCasesPage.getTotalElements());
        pagination.setPageSize(testCasesPage.getTotalPages());
        for (TestCases testCases : testCasesPage) {
            TestCaseResponse testCaseResponse = new TestCaseResponse();
            testCaseResponse.setProjectId(testCases.getSubModule().getMainModule().getModules().getProject().getId());
            testCaseResponse.setProjectName(testCases.getSubModule().getMainModule().getModules().getProject().getName());
            testCaseResponse.setModuleId(testCases.getSubModule().getMainModule().getModules().getId());
            testCaseResponse.setSubModuleId(testCases.getSubModule().getId());
            testCaseResponse.setMainModuleId(testCases.getSubModule().getMainModule().getId());
            testCaseResponse.setModuleName(testCases.getSubModule().getMainModule().getModules().getName());
            testCaseResponse.setMainModuleName(testCases.getSubModule().getMainModule().getName());
            testCaseResponse.setSubModuleName(testCases.getSubModule().getName());
            testCaseResponse.setName(testCases.getName().substring(testCases.getName().lastIndexOf(".") + 1));
            testCaseResponse.setId(testCases.getId());
            testCaseResponse.setDescription(testCases.getDescription());
            testCaseResponseList.add(testCaseResponse);
        }
        return testCaseResponseList;
    }

    @Override
    public List<TestCaseResponse> getAllTestCasesByModuleId(Long moduleId) {
        List<TestCaseResponse> testCaseResponseList = new ArrayList<>();
        List<TestCases> testCasesList = testCasesRepository.findBySubModule_MainModule_Modules_Id(moduleId);
        for (TestCases testCases : testCasesList) {
            TestCaseResponse testCaseResponse = new TestCaseResponse();
            testCaseResponse.setProjectId(testCases.getSubModule().getMainModule().getModules().getProject().getId());
            testCaseResponse.setProjectName(testCases.getSubModule().getMainModule().getModules().getProject().getName());
            testCaseResponse.setModuleId(testCases.getSubModule().getMainModule().getModules().getId());
            testCaseResponse.setModuleName(testCases.getSubModule().getMainModule().getModules().getName());
            testCaseResponse.setMainModuleId(testCases.getSubModule().getMainModule().getId());
            testCaseResponse.setMainModuleName(testCases.getSubModule().getMainModule().getName());
            testCaseResponse.setSubModuleId(testCases.getSubModule().getId());
            testCaseResponse.setSubModuleName(testCases.getSubModule().getName());
            BeanUtils.copyProperties(testCases, testCaseResponse);
            testCaseResponseList.add(testCaseResponse);

        }
        return testCaseResponseList;
    }

    @Override
    public List<TestCaseResponse> getAllTestCasesByMainModuleId(Long MainModuleId) {
        List<TestCaseResponse> testCaseResponseList = new ArrayList<>();
        List<TestCases> testCasesList = testCasesRepository.findBySubModule_MainModule_Id(MainModuleId);
        for (TestCases testCases : testCasesList) {
            TestCaseResponse testCaseResponse = new TestCaseResponse();
            testCaseResponse.setProjectId(testCases.getSubModule().getMainModule().getModules().getProject().getId());
            testCaseResponse.setProjectName(testCases.getSubModule().getMainModule().getModules().getProject().getName());
            testCaseResponse.setModuleId(testCases.getSubModule().getMainModule().getModules().getId());
            testCaseResponse.setModuleName(testCases.getSubModule().getMainModule().getModules().getName());
            testCaseResponse.setMainModuleId(testCases.getSubModule().getMainModule().getId());
            testCaseResponse.setMainModuleName(testCases.getSubModule().getMainModule().getName());
            testCaseResponse.setSubModuleId(testCases.getSubModule().getId());
            testCaseResponse.setSubModuleName(testCases.getSubModule().getName());
            BeanUtils.copyProperties(testCases, testCaseResponse);
            testCaseResponseList.add(testCaseResponse);

        }
        return testCaseResponseList;
    }

    @Override
    public boolean existsTestCaseByProjectId(Long projectId) {
        return testCasesRepository.existsBySubModule_MainModule_Modules_Project_id(projectId);
    }

    @Override
    public void updateExecutionStatus(Long testCaseId) {
        TestCases testCases = testCasesRepository.findById(testCaseId).get();
        testCases.setExecutionStatus(true);
        testCasesRepository.save(testCases);
    }

    private String getStringCellValue(Cell cell) {
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            return null;
        }
        cell.setCellType(CellType.STRING);
        return cell.getStringCellValue();
    }

    private Long getLongCellValue(Cell cell) {
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            return null;
        }
        cell.setCellType(CellType.NUMERIC);
        return (long) cell.getNumericCellValue();
    }

    private Map<String, Integer> getColumnMap(Row headerRow) {
        Map<String, Integer> columnMap = new HashMap<>();

        for (Cell cell : headerRow) {
            String cellValue = cell.getStringCellValue().toLowerCase();
            int columnIndex = cell.getColumnIndex();
            columnMap.put(cellValue, columnIndex);
        }

        return columnMap;
    }

    @Override
    public boolean existsTestCaseNameSubString(String testCaseName, Long subModuleId) {
        List<TestCases> testCasesList = testCasesRepository.findBySubModuleId(subModuleId);
        for (TestCases testCases : testCasesList
        ) {
            String listTestCaseName = testCases.getName().substring(testCases.getName().lastIndexOf(".") + 1);
            if (listTestCaseName.equals(testCaseName)) {
                return true;
            }
        }
        return false;
    }
}