package com.ii.testautomation.service.impl;
import com.ii.testautomation.dto.request.MainModulesRequest;
import com.ii.testautomation.dto.response.MainModulesResponse;
import com.ii.testautomation.dto.search.MainModuleSearch;
import com.ii.testautomation.entities.MainModules;
import com.ii.testautomation.entities.QMainModules;
import com.ii.testautomation.entities.Modules;
import com.ii.testautomation.repositories.MainModulesRepository;
import com.ii.testautomation.repositories.ModulesRepository;
import com.ii.testautomation.repositories.SubModulesRepository;
import com.ii.testautomation.response.common.PaginatedContentResponse;
import com.ii.testautomation.service.MainModulesService;
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
import java.util.*;

@SuppressWarnings("ALL")
@Service
public class MainModulesServiceImp implements MainModulesService {
    @Autowired
    private MainModulesRepository mainModulesRepository;
    @Autowired
    private ModulesRepository modulesRepository;
    @Autowired
    private SubModulesRepository subModulesRepository;

    @Override
    public void saveMainModules(MainModulesRequest mainModulesRequest) {
        MainModules mainModules = new MainModules();
        Modules modules1 = modulesRepository.findById(mainModulesRequest.getModuleId()).get();
        mainModules.setModules(modules1);
        BeanUtils.copyProperties(mainModulesRequest, mainModules);
        mainModulesRepository.save(mainModules);
    }

    @Override
    public void deleteMainModules(Long id) {
        mainModulesRepository.deleteById(id);
    }

    @Override
    public MainModulesResponse getByMainModulesId(Long id) {
        MainModulesResponse mainModulesResponse = new MainModulesResponse();
        MainModules mainModules = mainModulesRepository.findById(id).get();
        mainModulesResponse.setModuleId(mainModules.getModules().getId());
        mainModulesResponse.setModulesName(mainModules.getModules().getName());
        BeanUtils.copyProperties(mainModules, mainModulesResponse);
        return mainModulesResponse;
    }

    @Override
    public List<MainModulesResponse> getMainModulesByModuleId(Long id) {
        List<MainModulesResponse> mainModulesResponseList = new ArrayList<>();
        List<MainModules> mainModulesList = mainModulesRepository.findAllByModulesId(id);
        for (MainModules mainModules : mainModulesList) {
            MainModulesResponse mainModulesResponse = new MainModulesResponse();
            mainModulesResponse.setModuleId(mainModules.getModules().getId());
            mainModulesResponse.setModulesName(mainModules.getModules().getName());
            BeanUtils.copyProperties(mainModules, mainModulesResponse);
            mainModulesResponseList.add(mainModulesResponse);
        }
        return mainModulesResponseList;
    }

    @Override
    public List<MainModulesResponse> getMainModulesByProjectId(Pageable pageable, PaginatedContentResponse.Pagination pagination,Long id) {
        List<MainModulesResponse> mainModulesResponseList = new ArrayList<>();
        Page<MainModules> mainModulesPage = mainModulesRepository.findByModules_ProjectId(id,pageable);
        pagination.setTotalRecords(mainModulesPage.getTotalElements());
        pagination.setPageSize(mainModulesPage.getTotalPages());
        for (MainModules mainModules : mainModulesPage)
        {
            MainModulesResponse mainModulesResponse = new MainModulesResponse();
            mainModulesResponse.setModuleId(mainModules.getModules().getId());
            mainModulesResponse.setModulesName(mainModules.getModules().getName());
            BeanUtils.copyProperties(mainModules,mainModulesResponse);
            mainModulesResponseList.add(mainModulesResponse);
        }
        return mainModulesResponseList;
    }

    @Override
    public List<MainModulesResponse> SearchMainModulesWithPagination(Pageable pageable, PaginatedContentResponse.Pagination pagination, MainModuleSearch mainModuleSearch) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (Utils.isNotNullAndEmpty(mainModuleSearch.getName())) {
            booleanBuilder.and(QMainModules.mainModules.name.containsIgnoreCase(mainModuleSearch.getName()));
        }
        if (Utils.isNotNullAndEmpty(mainModuleSearch.getPrefix())) {
            booleanBuilder.and(QMainModules.mainModules.prefix.containsIgnoreCase(mainModuleSearch.getPrefix()));
        }
        if (Utils.isNotNullAndEmpty(mainModuleSearch.getModulesName())) {
            booleanBuilder.and(QMainModules.mainModules.modules.name.containsIgnoreCase(mainModuleSearch.getModulesName()));
        }
        List<MainModulesResponse> mainModulesResponseList = new ArrayList<>();
        Page<MainModules> mainModulesPage = mainModulesRepository.findAll(booleanBuilder, pageable);
        pagination.setTotalRecords(mainModulesPage.getTotalElements());
        pagination.setPageSize(mainModulesPage.getTotalPages());
        for (MainModules mainModules : mainModulesPage) {
            MainModulesResponse mainModulesResponse = new MainModulesResponse();
            mainModulesResponse.setModuleId(mainModules.getModules().getId());
            mainModulesResponse.setModulesName(mainModules.getModules().getName());
            BeanUtils.copyProperties(mainModules, mainModulesResponse);
            mainModulesResponseList.add(mainModulesResponse);
        }
        return mainModulesResponseList;
    }

    @Override
    public boolean isExistModulesId(Long id) {
        return modulesRepository.existsById(id);
    }

    @Override
    public boolean isExistMainModulesName(String name, Long moduleId) {
        Long projectId=modulesRepository.findById(moduleId).get().getProject().getId();
        return mainModulesRepository.existsByNameIgnoreCaseAndModules_ProjectId(name,projectId);
    }

    @Override
    public boolean isExistPrefix(String prefix, Long moduleId) {
        Long projectId=modulesRepository.findById(moduleId).get().getProject().getId();
        return mainModulesRepository.existsByPrefixIgnoreCaseAndModules_ProjectId(prefix,projectId);
    }

    @Override
    public boolean isExistMainModulesId(Long id) {
        return mainModulesRepository.existsById(id);
    }

    @Override
    public boolean existsMainModuleByModuleId(Long id) {
        if (id == null)
            return false;
        return mainModulesRepository.existsByModulesId(id);
    }

    @Override
    public boolean isUpdateMainModulesNameExist(String mainModuleName,Long mainModuleId) {
        Long projectId=mainModulesRepository.findById(mainModuleId).get().getModules().getProject().getId();
        return mainModulesRepository.existsByNameIgnoreCaseAndModules_ProjectIdAndIdNot(mainModuleName,projectId,mainModuleId);
    }

    @Override
    public boolean isUpdateMainModulesPrefixExist(String mainModuleprefix,Long mainModuleId) {
        Long projectId=mainModulesRepository.findById(mainModuleId).get().getModules().getProject().getId();
        return mainModulesRepository.existsByPrefixIgnoreCaseAndModules_ProjectIdAndIdNot(mainModuleprefix,projectId,mainModuleId);
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
    public boolean isExistMainModulesByProjectId(Long id) {
        return mainModulesRepository.existsByModules_ProjectId(id);
    }

    @Override
    public Map<Integer, MainModulesRequest> csvProcess(InputStream inputStream) {
        Map<Integer, MainModulesRequest> mainModulesRequestList = new HashMap<>();
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8")); CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {

            Iterable<CSVRecord> csvRecords = csvParser.getRecords();

            for (CSVRecord csvRecord : csvRecords) {
                MainModulesRequest mainModulesRequest = new MainModulesRequest();

                if (csvRecord.get("module_id").isEmpty() || csvRecord.get("module_id").isBlank()) {
                    mainModulesRequest.setModuleId(null);
                } else mainModulesRequest.setModuleId(Long.parseLong(csvRecord.get("module_id")));

                mainModulesRequest.setPrefix(csvRecord.get("prefix"));
                mainModulesRequest.setName(csvRecord.get("name"));
                mainModulesRequestList.put(Math.toIntExact(csvRecord.getRecordNumber()) + 1,mainModulesRequest);
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to parse CSV file: " + e.getMessage());
        }
        return mainModulesRequestList;
    }

    @Override
    public Map<Integer, MainModulesRequest> excelProcess(MultipartFile multipartFile) {
        Map<Integer, MainModulesRequest> mainModulesRequestList = new HashMap<>();
        try {
            Workbook workbook = new XSSFWorkbook(multipartFile.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);
            Map<String, Integer> columnMap = getColumnMap(headerRow);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                MainModulesRequest mainModulesRequest = new MainModulesRequest();
                mainModulesRequest.setModuleId(getLongCellValue(row.getCell(columnMap.get("module_id"))));
                mainModulesRequest.setName(getStringCellValue(row.getCell(columnMap.get("name"))));
                mainModulesRequest.setPrefix(getStringCellValue(row.getCell(columnMap.get("prefix"))));
                mainModulesRequestList.put(row.getRowNum() + 1,mainModulesRequest);
            }
            workbook.close();
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse Excel file: " + e.getMessage());
        }
        return mainModulesRequestList;
    }

    @Override
    public void addToErrorMessages(Map<String, List<Integer>> errorMessages, String key, int value) {
        List<Integer> errorList = errorMessages.getOrDefault(key, new ArrayList<>());
        errorList.add(value);
        errorMessages.put(key, errorList);
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
            String[] expectedHeader = {"name", "prefix", "module_id"};
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
            String[] expectedHeader = {"name", "prefix", "module_id"};
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
}

