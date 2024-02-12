package com.ii.testautomation.service.impl;

import com.ii.testautomation.dto.request.ModulesRequest;
import com.ii.testautomation.dto.response.*;
import com.ii.testautomation.dto.search.ModuleSearch;
import com.ii.testautomation.entities.*;
import com.ii.testautomation.repositories.MainModulesRepository;
import com.ii.testautomation.repositories.ModulesRepository;
import com.ii.testautomation.repositories.SubModulesRepository;
import com.ii.testautomation.repositories.TestCasesRepository;
import com.ii.testautomation.response.common.PaginatedContentResponse;
import com.ii.testautomation.service.ModulesService;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

@SuppressWarnings("deprecation")
@Service
public class ModulesServiceImpl implements ModulesService {

    @Autowired
    private ModulesRepository modulesRepository;
    @Autowired
    private MainModulesRepository mainModulesRepository;

    @Autowired
    private SubModulesRepository subModulesRepository;
    @Autowired
    private TestCasesRepository testCasesRepository;

    @Override
    public void saveModule(ModulesRequest modulesRequest) {
        Modules modules = new Modules();
        Project project = new Project();
        project.setId(modulesRequest.getProject_id());
        modules.setProject(project);
        BeanUtils.copyProperties(modulesRequest, modules);
        modulesRepository.save(modules);
    }
    @Override
    public boolean isModuleExistsByName(String name, Long projectId) {
        return modulesRepository.existsByNameIgnoreCaseAndProjectId(name, projectId);
    }

    @Override
    public boolean isModuleExistsByPrefix(String prefix, Long projectId) {
        return modulesRepository.existsByPrefixIgnoreCaseAndProjectId(prefix, projectId);
    }

    @Override
    public boolean existsByModulesId(Long id) {
        return modulesRepository.existsById(id);
    }

    @Override
    public boolean isUpdateModuleNameExists(String name, Long id) {
        Long projectId = modulesRepository.findById(id).get().getProject().getId();
        return modulesRepository.existsByNameIgnoreCaseAndProjectIdAndIdNot(name, projectId, id);
    }

    @Override
    public boolean isUpdateModulePrefixExists(String prefix, Long id) {
        Long projectId = modulesRepository.findById(id).get().getProject().getId();
        return modulesRepository.existsByPrefixIgnoreCaseAndProjectIdAndIdNot(prefix, projectId, id);
    }

    @Override
    public List<ModulesResponse> multiSearchModules(Pageable pageable, PaginatedContentResponse.Pagination pagination, ModuleSearch moduleSearch) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (Utils.isNotNullAndEmpty(moduleSearch.getModuleName())) {
            booleanBuilder.and(QModules.modules.name.containsIgnoreCase(moduleSearch.getModuleName()));
        }
        if (Utils.isNotNullAndEmpty(moduleSearch.getModulePrefix())) {
            booleanBuilder.and(QModules.modules.prefix.containsIgnoreCase(moduleSearch.getModulePrefix()));
        }
        List<ModulesResponse> modulesResponseList = new ArrayList<>();
        Page<Modules> modulesPage = modulesRepository.findAll(booleanBuilder, pageable);
        pagination.setTotalRecords(modulesPage.getTotalElements());
        pagination.setPageSize(modulesPage.getTotalPages());

        for (Modules modules : modulesPage) {
            ModulesResponse modulesResponse = new ModulesResponse();
            modulesResponse.setProjectId(modules.getProject().getId());
            modulesResponse.setProjectName(modules.getProject().getName());
            BeanUtils.copyProperties(modules, modulesResponse);
            modulesResponseList.add(modulesResponse);
        }

        return modulesResponseList;
    }

    @Override
    public ModulesResponse getModuleById(Long id) {
        ModulesResponse modulesResponse = new ModulesResponse();
        Modules module = modulesRepository.findById(id).get();
        modulesResponse.setProjectId(module.getProject().getId());
        modulesResponse.setProjectName(module.getProject().getName());
        BeanUtils.copyProperties(module, modulesResponse);
        return modulesResponse;
    }

    @Override
    public List<ModulesResponse> getAllModuleByProjectIdWithPagination(Long projectId, Pageable pageable, PaginatedContentResponse.Pagination pagination) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        List<ModulesResponse> modulesResponseList = new ArrayList<>();
        Page<Modules> modulesPage = modulesRepository.findAllModulesByProjectId(projectId, pageable);
        pagination.setTotalRecords(modulesPage.getTotalElements());
        pagination.setPageSize(modulesPage.getTotalPages());
        for (Modules module : modulesPage) {
            ModulesResponse modulesResponse = new ModulesResponse();
            modulesResponse.setProjectId(module.getProject().getId());
            modulesResponse.setProjectName(module.getProject().getName());
            BeanUtils.copyProperties(module, modulesResponse);
            modulesResponseList.add(modulesResponse);
        }
        return modulesResponseList;
    }

    @Override
    public ProjectModuleResponse getAllByProjectIdAndSearch(Long projectId, String testCaseName) {
        if (testCaseName == null && testCaseName.isEmpty()) {
            List<Modules> modulesList = modulesRepository.findAllModulesByProjectId(projectId);
            ProjectModuleResponse projectModuleResponse = new ProjectModuleResponse();
            List<ModulesResponse> modulesResponseList = new ArrayList<>();

            for (Modules module : modulesList) {
                ModulesResponse modulesResponse = new ModulesResponse();
                modulesResponse.setName(module.getName());
                modulesResponse.setId(module.getId());
                List<MainModules> mainModulesList = mainModulesRepository.findByModulesIdAndModules_ProjectId(module.getId(), projectId);

                List<MainModulesResponse> mainModulesResponseList = new ArrayList<>();

                for (MainModules mainModules : mainModulesList) {
                    MainModulesResponse mainModulesResponse = new MainModulesResponse();
                    mainModulesResponse.setId(mainModules.getId());
                    mainModulesResponse.setName(mainModules.getName());
                    List<SubModules> subModulesList = subModulesRepository.findAllSubModulesByMainModuleId(mainModules.getId());

                    List<SubModulesResponse> subModulesResponseList = new ArrayList<>();

                    for (SubModules subModules : subModulesList) {
                        SubModulesResponse subModulesResponse = new SubModulesResponse();
                        subModulesResponse.setId(subModules.getId());
                        subModulesResponse.setName(subModules.getName());
                        List<TestCases> testCasesList = testCasesRepository.findAllTestCasesBySubModuleId(subModules.getId());
                        List<TestCaseResponse> testCaseResponseList = new ArrayList<>();

                        for (TestCases testCases : testCasesList) {
                            TestCaseResponse testCaseResponse = new TestCaseResponse();
                            testCaseResponse.setId(testCases.getId());
                            testCaseResponse.setName(testCases.getName().substring(testCases.getName().lastIndexOf(".") + 1));
                            testCaseResponseList.add(testCaseResponse);

                        }
                        if (testCaseResponseList != null && !testCaseResponseList.isEmpty()) {
                            subModulesResponse.setTestCaseResponses(testCaseResponseList);
                        }
                        if (subModulesResponse.getTestCaseResponses() != null && !subModulesResponse.getTestCaseResponses().isEmpty())
                            subModulesResponseList.add(subModulesResponse);
                    }
                    if (subModulesResponseList != null && !subModulesResponseList.isEmpty()) {
                        mainModulesResponse.setSubModulesResponses(subModulesResponseList);
                    }
                    if (mainModulesResponse.getSubModulesResponses() != null && !mainModulesResponse.getSubModulesResponses().isEmpty()) {
                        mainModulesResponseList.add(mainModulesResponse);
                    }
                }
                if (mainModulesResponseList != null && !mainModulesResponseList.isEmpty()) {
                    modulesResponse.setMainModulesResponse(mainModulesResponseList);
                }
                if (modulesResponse.getMainModulesResponse() != null && !modulesResponse.getMainModulesResponse().isEmpty()) {
                    modulesResponseList.add(modulesResponse);
                }

            }
            projectModuleResponse.setModulesResponseList(modulesResponseList);
            return projectModuleResponse;

        }
        List<Modules> modulesList = modulesRepository.findAllModulesByProjectId(projectId);
        ProjectModuleResponse projectModuleResponse = new ProjectModuleResponse();
        List<ModulesResponse> modulesResponseList = new ArrayList<>();

        for (Modules module : modulesList) {
            ModulesResponse modulesResponse = new ModulesResponse();
            modulesResponse.setName(module.getName());
            modulesResponse.setId(module.getId());
            List<MainModules> mainModulesList = mainModulesRepository.findByModulesIdAndModules_ProjectId(module.getId(), projectId);

            List<MainModulesResponse> mainModulesResponseList = new ArrayList<>();

            for (MainModules mainModules : mainModulesList) {
                MainModulesResponse mainModulesResponse = new MainModulesResponse();
                mainModulesResponse.setId(mainModules.getId());
                mainModulesResponse.setName(mainModules.getName());
                List<SubModules> subModulesList = subModulesRepository.findAllSubModulesByMainModuleId(mainModules.getId());

                List<SubModulesResponse> subModulesResponseList = new ArrayList<>();

                for (SubModules subModules : subModulesList) {
                    SubModulesResponse subModulesResponse = new SubModulesResponse();
                    subModulesResponse.setId(subModules.getId());
                    subModulesResponse.setName(subModules.getName());
                    List<TestCases> testCasesList = testCasesRepository.findAllTestCasesBySubModuleId(subModules.getId());
                    List<TestCaseResponse> testCaseResponseList = new ArrayList<>();

                    for (TestCases testCases : testCasesList) {
                        if (testCases.getName().contains(testCaseName)) {

                            TestCaseResponse testCaseResponse = new TestCaseResponse();
                            testCaseResponse.setId(testCases.getId());
                            testCaseResponse.setProjectId(testCases.getSubModule().getMainModule().getModules().getProject().getId());
                            testCaseResponse.setName(testCases.getName().substring(testCases.getName().lastIndexOf(".") + 1));
                            testCaseResponseList.add(testCaseResponse);
                        }
                    }

                    if (!testCaseResponseList.isEmpty()) {
                        subModulesResponse.setTestCaseResponses(testCaseResponseList);
                        subModulesResponseList.add(subModulesResponse);
                    }
                }

                if (!subModulesResponseList.isEmpty()) {
                    mainModulesResponse.setSubModulesResponses(subModulesResponseList);
                    mainModulesResponseList.add(mainModulesResponse);
                }
            }

            if (!mainModulesResponseList.isEmpty()) {
                modulesResponse.setMainModulesResponse(mainModulesResponseList);
                modulesResponseList.add(modulesResponse);
            }
        }

        projectModuleResponse.setModulesResponseList(modulesResponseList);
        return projectModuleResponse;
    }

    @Override
    public void deleteModuleById(Long id) {
        modulesRepository.deleteById(id);
    }

    @Override
    public boolean existsModuleByProjectId(Long projectId) {
        return modulesRepository.existsByProjectId(projectId);
    }

    @Override
    public Map<Integer, ModulesRequest> csvToModulesRequest(InputStream inputStream) {
        Map<Integer, ModulesRequest> modulesRequestsList = new HashMap<>();
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)); CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();
            for (CSVRecord csvRecord : csvRecords) {

                ModulesRequest modulesRequest = new ModulesRequest();
                modulesRequest.setName(csvRecord.get("name"));
                modulesRequest.setPrefix(csvRecord.get("prefix"));
                if (!csvRecord.get("project_id").isEmpty()) {
                    modulesRequest.setProject_id(Long.parseLong(csvRecord.get("project_id")));
                } else {
                    modulesRequest.setProject_id(null);
                }
                modulesRequestsList.put(Math.toIntExact(csvRecord.getRecordNumber() + 1), modulesRequest);
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to parse CSV file: " + e.getMessage());
        }
        return modulesRequestsList;
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
    public Map<Integer, ModulesRequest> excelToModuleRequest(MultipartFile multipartFile) {
        Map<Integer, ModulesRequest> modulesRequestList = new HashMap<>();
        try {
            Workbook workbook = new XSSFWorkbook(multipartFile.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);
            Map<String, Integer> columnMap = getColumnMap(headerRow);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                ModulesRequest modulesRequest = new ModulesRequest();
                modulesRequest.setName(getStringCellValue(row.getCell(columnMap.get("name"))));
                modulesRequest.setPrefix(getStringCellValue(row.getCell(columnMap.get("prefix"))));
                modulesRequest.setProject_id(getLongCellValue(row.getCell(columnMap.get("project_id"))));
                modulesRequestList.put(row.getRowNum() + 1, modulesRequest);
            }
            workbook.close();
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse Excel file: " + e.getMessage());
        }
        return modulesRequestList;
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
            String[] expectedHeader = {"name", "prefix", "project_id"};
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
            String[] expectedHeader = {"name", "prefix", "project_id"};
            Set<String> expectedHeaderSet = new HashSet<>(Arrays.asList(expectedHeader));
            Set<String> actualHeaderSet = new HashSet<>(Arrays.asList(actualHeaders));
            return expectedHeaderSet.equals(actualHeaderSet);
        } catch (Exception e) {
            return false;
        }
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
    public void addToErrorMessages(Map<String, List<Integer>> errorMessages, String key, int value) {
        List<Integer> errorList = errorMessages.getOrDefault(key, new ArrayList<>());
        errorList.add(value);
        errorMessages.put(key, errorList);
    }
}
