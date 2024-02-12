package com.ii.testautomation.service.impl;

import com.ii.testautomation.dto.request.SubModulesRequest;
import com.ii.testautomation.dto.response.SubModulesResponse;
import com.ii.testautomation.dto.search.SubModuleSearch;
import com.ii.testautomation.entities.MainModules;
import com.ii.testautomation.entities.SubModules;
import com.ii.testautomation.entities.QSubModules;
import com.ii.testautomation.repositories.MainModulesRepository;
import com.ii.testautomation.repositories.SubModulesRepository;
import com.ii.testautomation.response.common.PaginatedContentResponse;
import com.ii.testautomation.service.SubModulesService;
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

@SuppressWarnings("ALL")
@Service
public class SubModulesServiceImpl implements SubModulesService {

    @Autowired
    private SubModulesRepository subModulesRepository;
    @Autowired
    private MainModulesRepository mainModulesRepository;

    @Override
    public void saveSubModules(SubModulesRequest subModulesRequest) {
        SubModules subModules = new SubModules();
        MainModules mainModules = new MainModules();
        mainModules.setId(subModulesRequest.getMain_module_Id());
        subModules.setMainModule(mainModules);
        BeanUtils.copyProperties(subModulesRequest, subModules);
        subModulesRepository.save(subModules);
    }

    @Override
    public boolean existsBySubModulesName(String subModuleName, Long mainModuleId) {
        Long projectId = mainModulesRepository.findById(mainModuleId).get().getModules().getProject().getId();
        return subModulesRepository.existsByNameIgnoreCaseAndMainModule_Modules_ProjectId(subModuleName, projectId);
    }

    @Override
    public boolean existsBySubModulesPrefix(String subModulePrefix, Long mainModuleId) {
        Long projectId = mainModulesRepository.findById(mainModuleId).get().getModules().getProject().getId();
        return subModulesRepository.existsByPrefixIgnoreCaseAndMainModule_Modules_ProjectId(subModulePrefix, projectId);
    }

    @Override
    public boolean isUpdateSubModuleNameExits(String subModuleName, Long subModuleId) {
        Long projectId = subModulesRepository.findById(subModuleId).get().getMainModule().getModules().getProject().getId();
        return subModulesRepository.existsByNameIgnoreCaseAndMainModule_Modules_ProjectIdAndIdNot(subModuleName, projectId, subModuleId);
    }

    @Override
    public boolean isUpdateSubModulePrefixExits(String subModulePrefix, Long subModuleId) {
        Long projectId = subModulesRepository.findById(subModuleId).get().getMainModule().getModules().getProject().getId();
        return subModulesRepository.existsByPrefixIgnoreCaseAndMainModule_Modules_ProjectIdAndIdNot(subModulePrefix, projectId, subModuleId);
    }

    @Override
    public boolean existsBySubModuleId(Long subModuleId) {
        return subModulesRepository.existsById(subModuleId);
    }

    @Override
    public SubModulesResponse getSubModuleById(Long subModuleId) {
        SubModules subModules = subModulesRepository.findById(subModuleId).get();
        SubModulesResponse subModulesResponse = new SubModulesResponse();
        subModulesResponse.setMainModuleName(subModules.getMainModule().getName());
        subModulesResponse.setModuleName(subModules.getMainModule().getModules().getName());
        subModulesResponse.setModuleId(subModules.getMainModule().getModules().getId());
        subModulesResponse.setMainModuleId(subModules.getMainModule().getId());
        BeanUtils.copyProperties(subModules, subModulesResponse);
        return subModulesResponse;
    }

    @Override
    public List<SubModulesResponse> getAllSubModuleByMainModuleId(Long mainModuleId) {
        List<SubModules> subModulesList = subModulesRepository.findAllSubModulesByMainModuleId(mainModuleId);
        List<SubModulesResponse> subModulesResponseList = new ArrayList<>();
        for (SubModules subModules : subModulesList) {
            SubModulesResponse subModulesResponse = new SubModulesResponse();
            subModulesResponse.setModuleName(subModules.getMainModule().getModules().getName());
            subModulesResponse.setMainModuleName(subModules.getMainModule().getName());
            subModulesResponse.setModuleId(subModules.getMainModule().getModules().getId());
            subModulesResponse.setMainModuleId(subModules.getMainModule().getId());
            BeanUtils.copyProperties(subModules, subModulesResponse);
            subModulesResponseList.add(subModulesResponse);
        }
        return subModulesResponseList;
    }
    @Override
    public List<SubModulesResponse> multiSearchSubModule(Pageable pageable, PaginatedContentResponse.Pagination pagination, SubModuleSearch subModuleSearch) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (Utils.isNotNullAndEmpty(subModuleSearch.getMainModuleName())) {
            booleanBuilder.and(QSubModules.subModules.mainModule.name.containsIgnoreCase(subModuleSearch.getMainModuleName()));
        }
        if (Utils.isNotNullAndEmpty(subModuleSearch.getMainModulePrefix())) {
            booleanBuilder.and(QSubModules.subModules.mainModule.prefix.containsIgnoreCase(subModuleSearch.getMainModulePrefix()));
        }
        if (Utils.isNotNullAndEmpty(subModuleSearch.getName())) {
            booleanBuilder.and(QSubModules.subModules.name.containsIgnoreCase(subModuleSearch.getName()));
        }
        if (Utils.isNotNullAndEmpty(subModuleSearch.getPrefix())) {
            booleanBuilder.and(QSubModules.subModules.prefix.containsIgnoreCase(subModuleSearch.getPrefix()));
        }
        if (Utils.isNotNullAndEmpty(subModuleSearch.getModuleName())) {
            booleanBuilder.and(QSubModules.subModules.mainModule.modules.name.containsIgnoreCase(subModuleSearch.getModuleName()));
        }
        if (Utils.isNotNullAndEmpty(subModuleSearch.getModulePrefix())) {
            booleanBuilder.and(QSubModules.subModules.mainModule.modules.prefix.containsIgnoreCase(subModuleSearch.getModulePrefix()));
        }
        List<SubModulesResponse> subModulesResponseList = new ArrayList<>();
        Page<SubModules> subModulesPage = subModulesRepository.findAll(booleanBuilder, pageable);
        pagination.setTotalRecords(subModulesPage.getTotalElements());
        pagination.setPageSize(subModulesPage.getTotalPages());

        for (SubModules subModules : subModulesPage) {
            SubModulesResponse subModulesResponse = new SubModulesResponse();
            subModulesResponse.setMainModuleName(subModules.getMainModule().getName());
            subModulesResponse.setModuleName(subModules.getMainModule().getModules().getName());
            subModulesResponse.setModuleId(subModules.getMainModule().getModules().getId());
            subModulesResponse.setMainModuleId(subModules.getMainModule().getId());
            BeanUtils.copyProperties(subModules, subModulesResponse);
            subModulesResponseList.add(subModulesResponse);
        }
        return subModulesResponseList;
    }

    @Override
    public List<SubModulesResponse> getSubModulesByProjectIdWithPagination(Long id, Pageable pageable, PaginatedContentResponse.Pagination pagination) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        List<SubModulesResponse> subModulesResponseList = new ArrayList<>();

        Page<SubModules> subModulesPage = subModulesRepository.findByMainModule_Modules_Project_Id(id, pageable);
        pagination.setTotalRecords(subModulesPage.getTotalElements());
        pagination.setPageSize(subModulesPage.getTotalPages());

        for (SubModules subModules : subModulesPage) {
            SubModulesResponse subModulesResponse = new SubModulesResponse();
            BeanUtils.copyProperties(subModules, subModulesResponse);
            subModulesResponse.setMainModuleName(subModules.getMainModule().getName());
            subModulesResponse.setModuleId(subModules.getMainModule().getModules().getId());
            subModulesResponse.setMainModuleId(subModules.getMainModule().getId());
            subModulesResponse.setModuleName(subModules.getMainModule().getModules().getName());
            subModulesResponseList.add(subModulesResponse);
        }
        return subModulesResponseList;
    }

    @Override
    public void deleteSubModuleById(Long subModuleId) {
        subModulesRepository.deleteById(subModuleId);
    }

    @Override
    public boolean existsByMainModuleId(Long mainModuleId) {
        return subModulesRepository.existsByMainModuleId(mainModuleId);
    }

    @Override
    public boolean existsByProjectId(Long projectId) {
        return subModulesRepository.existsByMainModule_Modules_ProjectId(projectId);
    }

    @Override
    public Long getSubModuleIdByNameForProject(String subModuleName, Long projectId) {
        return subModulesRepository.findByNameIgnoreCaseAndMainModule_Modules_ProjectId(subModuleName, projectId).getId();
    }
    @Override
    public boolean existsBySubModulesNameForProject(String subModuleName, Long projectId) {
        return subModulesRepository.existsByNameIgnoreCaseAndMainModule_Modules_ProjectId(subModuleName, projectId);
    }
    @Override
    public Map<Integer, SubModulesRequest> csvToSubModuleRequest(InputStream inputStream) {
        Map<Integer, SubModulesRequest> subModulesRequestList = new HashMap<>();
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)); CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {

            Iterable<CSVRecord> csvRecords = csvParser.getRecords();

            for (CSVRecord csvRecord : csvRecords) {
                SubModulesRequest subModulesRequest = new SubModulesRequest();
                subModulesRequest.setName(csvRecord.get("name"));
                subModulesRequest.setPrefix(csvRecord.get("prefix"));
                if (!csvRecord.get("main_module_id").isEmpty()) {
                    subModulesRequest.setMain_module_Id(Long.parseLong(csvRecord.get("main_module_id")));
                } else {
                    subModulesRequest.setMain_module_Id(null);
                }
                subModulesRequestList.put(Math.toIntExact(csvRecord.getRecordNumber()) + 1, subModulesRequest);
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to parse CSV file: " + e.getMessage());
        }
        return subModulesRequestList;
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
    public Map<Integer, SubModulesRequest> excelToSubModuleRequest(MultipartFile multipartFile) {
        Map<Integer, SubModulesRequest> subModulesRequestList = new HashMap<>();
        try {
            Workbook workbook = new XSSFWorkbook(multipartFile.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);
            Map<String, Integer> columnMap = getColumnMap(headerRow);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                SubModulesRequest subModulesRequest = new SubModulesRequest();
                subModulesRequest.setMain_module_Id(getLongCellValue(row.getCell(columnMap.get("main_module_id"))));
                subModulesRequest.setPrefix(getStringCellValue(row.getCell(columnMap.get("prefix"))));
                subModulesRequest.setName(getStringCellValue(row.getCell(columnMap.get("name"))));
                subModulesRequestList.put(row.getRowNum() + 1, subModulesRequest);
            }
            workbook.close();
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse Excel file: " + e.getMessage());
        }
        return subModulesRequestList;
    }
    @Override
    public boolean isExcelHeaderMatch(MultipartFile multipartFile) {
        try (InputStream inputStream = multipartFile.getInputStream(); Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);
            String[] actualHeaders = new String[headerRow.getLastCellNum()];
            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                Cell cell = headerRow.getCell(i);
                actualHeaders[i] = cell.getStringCellValue().toLowerCase();
            }
            String[] expectedHeader = {"name", "prefix", "main_module_id"};
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
            String[] expectedHeader = {"name", "prefix", "main_module_id"};
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