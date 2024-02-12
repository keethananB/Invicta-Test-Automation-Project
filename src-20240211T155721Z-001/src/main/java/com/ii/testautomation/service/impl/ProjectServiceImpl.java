package com.ii.testautomation.service.impl;

import com.ii.testautomation.dto.request.ProjectRequest;
import com.ii.testautomation.dto.response.ProjectResponse;
import com.ii.testautomation.dto.search.ProjectSearch;
import com.ii.testautomation.entities.*;
import com.ii.testautomation.repositories.CompanyUserRepository;
import com.ii.testautomation.repositories.LicensesRepository;
import com.ii.testautomation.repositories.ProjectRepository;
import com.ii.testautomation.repositories.TestGroupingRepository;
import com.ii.testautomation.response.common.PaginatedContentResponse.Pagination;
import com.ii.testautomation.service.ProjectService;
import com.ii.testautomation.utils.Utils;
import com.querydsl.core.BooleanBuilder;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
@Component
@PropertySource("classpath:application.properties")
public class ProjectServiceImpl implements ProjectService {
    @Value("${jar.import.file.windows.path}")
    private String fileFolder;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private LicensesRepository licensesRepository;
    @Autowired
    private TestGroupingRepository testGroupingRepository;
    @Autowired
    private CompanyUserRepository companyUserRepository;

    @Override
    public boolean checkJarFile(MultipartFile jarFile) {
        if (jarFile != null && !jarFile.isEmpty()) {
            String jarFilename = jarFile.getOriginalFilename();
            String jarFileExtension = jarFilename.substring(jarFilename.lastIndexOf(".") + 1);
            if (!"jar".equalsIgnoreCase(jarFileExtension)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean checkPropertiesFile(MultipartFile propertiesFile) {
        if (propertiesFile != null && !propertiesFile.isEmpty()) {
            String configFilename = propertiesFile.getOriginalFilename();
            String configFileExtension = configFilename.substring(configFilename.lastIndexOf(".") + 1);

            if (!"properties".equalsIgnoreCase(configFileExtension)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void saveProject(ProjectRequest projectRequest, MultipartFile jarFile, MultipartFile configFile) {
        Project project = new Project();
        CompanyUser companyUser = new CompanyUser();
        companyUser.setId(projectRequest.getCompanyUserId());
        project.setCompanyUser(companyUser);
        BeanUtils.copyProperties(projectRequest, project);
        String directoryPath = fileFolder + projectRequest.getName();
        String uploadedJarFilePath = null;
        String uploadedConfigFilePath = null;
        File jarDirectory = new File(directoryPath);
        if (!jarDirectory.exists()) {
            jarDirectory.mkdirs();
        }
        project.setProjectPath(directoryPath);
        try {
            if (jarFile != null && !jarFile.isEmpty()) {
                String jarFilename = jarFile.getOriginalFilename();
                uploadedJarFilePath = directoryPath + File.separator + jarFilename;
                File savedJarFile = new File(uploadedJarFilePath);
                jarFile.transferTo(savedJarFile);

            }
            if (configFile != null && !configFile.isEmpty()) {
                String configFilename = configFile.getOriginalFilename();
                uploadedConfigFilePath = directoryPath + File.separator + configFilename;
                File savedConfigFile = new File(uploadedConfigFilePath);
                configFile.transferTo(savedConfigFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        project.setJarFilePath(uploadedJarFilePath);
        project.setConfigFilePath(uploadedConfigFilePath);
        projectRepository.save(project);
    }

    @Override
    public void updateProject(ProjectRequest projectRequest, MultipartFile jarFile, MultipartFile configFile) {
        Project eixstingProject = projectRepository.findById(projectRequest.getId()).get();
        BeanUtils.copyProperties(projectRequest, eixstingProject);
        String existingFolderPath = eixstingProject.getProjectPath();
        String newProjectFolderPath = fileFolder + File.separator + projectRequest.getName();
        File existingFolder = new File(existingFolderPath);
        File newFolder = new File(newProjectFolderPath);
        String uploadedJarFilePath = eixstingProject.getJarFilePath();
        String uploadedConfigPath = eixstingProject.getConfigFilePath();
        if (existingFolder.exists()) {
            try {
                if (jarFile != null && !jarFile.isEmpty()) {
                    if (uploadedJarFilePath != null) {
                        File existingJarFolder = new File(uploadedJarFilePath);
                        existingJarFolder.delete();
                    }
                    String jarFilename = jarFile.getOriginalFilename();
                    existingFolder.renameTo(newFolder);
                    eixstingProject.setProjectPath(newProjectFolderPath);
                    uploadedJarFilePath = newProjectFolderPath + File.separator + jarFilename;
                    File savedJarFile = new File(uploadedJarFilePath);
                    jarFile.transferTo(savedJarFile);
                    eixstingProject.setJarFilePath(uploadedJarFilePath);
                } else {
                    if (uploadedJarFilePath != null) {
                        existingFolder.renameTo(newFolder);
                        eixstingProject.setProjectPath(newProjectFolderPath);
                        Path path = Paths.get(uploadedJarFilePath);
                        String fileName = path.getFileName().toString();
                        String newJarFilePath = newProjectFolderPath + File.separator + fileName;
                        eixstingProject.setJarFilePath(newJarFilePath);
                    }

                }

                if (configFile != null && !configFile.isEmpty()) {
                    if (uploadedConfigPath != null) {
                        File existingConfigFolder = new File(uploadedConfigPath);
                        existingConfigFolder.delete();
                    }

                    String configFilename = configFile.getOriginalFilename();
                    existingFolder.renameTo(newFolder);
                    eixstingProject.setProjectPath(newProjectFolderPath);
                    uploadedConfigPath = newProjectFolderPath + File.separator + configFilename;
                    File savedConfigFile = new File(uploadedConfigPath);
                    configFile.transferTo(savedConfigFile);
                    eixstingProject.setConfigFilePath(uploadedConfigPath);
                } else {
                    if (uploadedConfigPath != null) {
                        existingFolder.renameTo(newFolder);
                        eixstingProject.setProjectPath(newProjectFolderPath);
                        Path path = Paths.get(uploadedConfigPath);
                        String fileName = path.getFileName().toString();
                        String newConfigPath = newProjectFolderPath + File.separator + fileName;
                        eixstingProject.setConfigFilePath(newConfigPath);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        List<TestGrouping> testGroupingList = testGroupingRepository.findDistinctByTestCases_SubModule_MainModule_Modules_Project_Id(projectRequest.getId());
        for (TestGrouping testGrouping : testGroupingList) {
            Path groupingPath = Paths.get(testGrouping.getGroupPath());
            String groupName = groupingPath.getFileName().toString();
            String newGroupingPath = newProjectFolderPath + File.separator + groupName;
            testGrouping.setGroupPath(newGroupingPath);
            List<String> excelPathList = testGrouping.getExcelFilePath();
            List<String> newExcelPathList = new ArrayList<>();
            for (String excelPath : excelPathList) {
                Path groupingExcelPath = Paths.get(excelPath);
                String newExcelName = groupingExcelPath.getFileName().toString();
                String newExcelPath = newGroupingPath + File.separator + newExcelName;
                newExcelPathList.add(newExcelPath);
            }
            testGrouping.setExcelFilePath(newExcelPathList);
            testGroupingRepository.save(testGrouping);
        }
        BeanUtils.copyProperties(projectRequest, eixstingProject);
        projectRepository.save(eixstingProject);

    }

    @Override
    public boolean existByProjectNameAndCompanyId(String projectName, Long companyUserId) {
        return projectRepository.existsByNameIgnoreCaseAndCompanyUserId(projectName,companyUserId);
    }

    @Override
    public boolean existByProjectCodeAndCompanyId(String projectCode, Long companyUserId) {
        return projectRepository.existsByCodeIgnoreCaseAndCompanyUserId(projectCode,companyUserId);
    }

    @Override
    public boolean isUpdateProjectNameExist(String projectName, Long projectId) {
        return projectRepository.existsByNameIgnoreCaseAndIdNot(projectName, projectId);
    }

    @Override
    public boolean isUpdateProjectCodeExist(String projectCode, Long projectId) {
        return projectRepository.existsByCodeIgnoreCaseAndIdNot(projectCode, projectId);
    }

    @Override
    public boolean existByProjectId(Long projectId) {
        if (projectId == null) {
            return false;
        }
        return projectRepository.existsById(projectId);
    }

    @Override
    public ProjectResponse getProjectById(Long projectId) throws IOException {
        Project project;
        project = projectRepository.findById(projectId).get();
        ProjectResponse projectResponse = new ProjectResponse();
        BeanUtils.copyProperties(project, projectResponse);
        String existingConfigFile = project.getConfigFilePath();
        String existingJarFile = project.getJarFilePath();
        if (existingConfigFile != null && !existingConfigFile.isEmpty()) {
            Path config = Paths.get(existingConfigFile);
            String jarFileName = config.getFileName().toString();
            projectResponse.setConfigFile(jarFileName);
        }
        if (existingJarFile != null && !existingJarFile.isEmpty()) {
            Path jar = Paths.get(existingJarFile);
            String configFileName = jar.getFileName().toString();
            projectResponse.setJarFile(configFileName);
        }
        return projectResponse;
    }

    public List<ProjectResponse> multiSearchProject(Pageable pageable, Pagination pagination, ProjectSearch projectSearch) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (Utils.isNotNullAndEmpty(projectSearch.getName())) {
            booleanBuilder.and(QProject.project.name.containsIgnoreCase(projectSearch.getName()));
        }
        if (Utils.isNotNullAndEmpty(projectSearch.getCode())) {
            booleanBuilder.and(QProject.project.code.containsIgnoreCase(projectSearch.getCode()));
        }
        List<ProjectResponse> projectResponseList = new ArrayList<>();
        Page<Project> projectPage = projectRepository.findAll(booleanBuilder, pageable);
        List<Project> projectList = projectPage.getContent();
        pagination.setPageSize(projectPage.getTotalPages());
        pagination.setTotalRecords(projectPage.getTotalElements());
        for (Project project : projectList) {
            ProjectResponse projectResponse = new ProjectResponse();
            BeanUtils.copyProperties(project, projectResponse);
            String existingConfigFile = project.getConfigFilePath();
            String existingJarFile = project.getJarFilePath();
            if (existingConfigFile != null && !existingConfigFile.isEmpty()) {
                Path config = Paths.get(existingConfigFile);
                String jarFileName = config.getFileName().toString();
                projectResponse.setConfigFile(jarFileName);
            }
            if (existingJarFile != null && !existingJarFile.isEmpty()) {
                Path jar = Paths.get(existingJarFile);
                String configFileName = jar.getFileName().toString();
                projectResponse.setJarFile(configFileName);
            }
            projectResponseList.add(projectResponse);
        }
        return projectResponseList;
    }

    @Override
    public void deleteProject(Long projectId) {
        String projectDirectoryPath = fileFolder + File.separator + projectRepository.findById(projectId).get().getName();
        deleteProjectFolder(projectDirectoryPath);
        projectRepository.deleteById(projectId);
    }

    private void deleteProjectFolder(String folderPath) {
        File directory = new File(folderPath);
        if (directory.exists()) {
            if (directory.isDirectory()) {
                File[] files = directory.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.isDirectory()) {
                            deleteProjectFolder(file.getAbsolutePath());
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
    public Map<Integer, ProjectRequest> csvToProjectRequest(InputStream inputStream) {
        Map<Integer, ProjectRequest> projectRequestList = new HashMap<>();
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)); CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {

            Iterable<CSVRecord> csvRecords = csvParser.getRecords();

            for (CSVRecord csvRecord : csvRecords) {
                ProjectRequest projectRequest = new ProjectRequest();
                projectRequest.setCode(csvRecord.get("code"));
                projectRequest.setDescription(csvRecord.get("description"));
                projectRequest.setName(csvRecord.get("name"));
                projectRequestList.put(Math.toIntExact(csvRecord.getRecordNumber()) + 1, projectRequest);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse CSV file: " + e.getMessage());
        }
        return projectRequestList;
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
    public Map<Integer, ProjectRequest> excelToProjectRequest(MultipartFile multipartFile) {
        Map<Integer, ProjectRequest> projectRequestList = new HashMap<>();
        try {
            Workbook workbook = new XSSFWorkbook(multipartFile.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter dataFormatter = new DataFormatter();
            Row headerRow = sheet.getRow(0);
            Map<String, Integer> columnMap = getColumnMap(headerRow);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                ProjectRequest projectRequest = new ProjectRequest();
                projectRequest.setCode(dataFormatter.formatCellValue(row.getCell(columnMap.get("code"))));
                projectRequest.setDescription(dataFormatter.formatCellValue(row.getCell(columnMap.get("description"))));
                projectRequest.setName(dataFormatter.formatCellValue(row.getCell(columnMap.get("name"))));
                projectRequestList.put(row.getRowNum() + 1, projectRequest);
            }
            workbook.close();
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse Excel file: " + e.getMessage());
        }
        return projectRequestList;
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
    public boolean isExcelHeaderMatch(MultipartFile multipartFile) {
        try (InputStream inputStream = multipartFile.getInputStream(); Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);
            String[] actualHeaders = new String[headerRow.getLastCellNum()];
            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                Cell cell = headerRow.getCell(i);
                actualHeaders[i] = cell.getStringCellValue().toLowerCase();
            }
            String[] expectedHeader = {"code", "name", "description"};
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
            String[] expectedHeader = {"code", "name", "description"};
            Set<String> expectedHeaderSet = new HashSet<>(Arrays.asList(expectedHeader));
            Set<String> actualHeaderSet = new HashSet<>(Arrays.asList(actualHeaders));
            return expectedHeaderSet.equals(actualHeaderSet);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean hasJarPath(Long projectId) {
        Project project = projectRepository.findById(projectId).get();
        if (project.getJarFilePath() != null) return true;
        return false;
    }

    @Override
    public boolean hasConfigPath(Long projectId) {
        Project project = projectRepository.findById(projectId).get();
        if (project.getConfigFilePath() != null) return true;
        return false;
    }

    @Override
    public void addToErrorMessages(Map<String, List<Integer>> errorMessages, String key, int value) {
        List<Integer> errorList = errorMessages.getOrDefault(key, new ArrayList<>());
        errorList.add(value);
        errorMessages.put(key, errorList);
    }

    @Override
    public boolean existsByUsersId(Long usersId) {
        return projectRepository.existsByUsersId(usersId);
    }

    @Override
    public boolean projectCount(Long companyId) {
        Long project = projectRepository.findByCompanyUserId(companyId).stream().count();
        CompanyUser companyUser = companyUserRepository.findById(companyId).get();
        Long number = companyUser.getLicenses().getNoOfProjects();
        if (project < number) {
            return true;
        }
        return false;
    }

    @Override
    public List<ProjectResponse> getProjectByCompanyId(Long companyUserId, Pageable pageable,
        Pagination pagination) {
        List<ProjectResponse> projectResponseList = new ArrayList<>();
        Page<Project> projectPage = projectRepository.findByCompanyUserId(companyUserId,pageable);
        pagination.setTotalRecords(projectPage.getTotalElements());
        pagination.setPageSize(projectPage.getTotalPages());
        for (Project project : projectPage){
            ProjectResponse projectResponse = new ProjectResponse();
            BeanUtils.copyProperties(project,projectResponse);
            String existingConfigFile = project.getConfigFilePath();
            String existingJarFile = project.getJarFilePath();
            if (existingConfigFile != null && !existingConfigFile.isEmpty()) {
                Path config = Paths.get(existingConfigFile);
                String jarFileName = config.getFileName().toString();
                projectResponse.setConfigFile(jarFileName);
            }
            if (existingJarFile != null && !existingJarFile.isEmpty()) {
                Path jar = Paths.get(existingJarFile);
                String configFileName = jar.getFileName().toString();
                projectResponse.setJarFile(configFileName);
            }
            projectResponseList.add(projectResponse);
        }
        return projectResponseList;
    }
}