package com.ii.testautomation.utils;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * Specifying custom messages
 */
@Component
@PropertySource("classpath:MessagesAndCodes.properties")
@Getter
@Setter
public class StatusCodeBundle {
    // Common Success code
    @Value("${code.success.common}")
    private String commonSuccessCode;
    @Value("${code.failure.common}")
    private String failureCode;
    @Value("${code.nullValues.received}")
    private String NullValuesCode;
    @Value("${code.failure.file}")
    private String fileFailureCode;

    // Common File Failure Message
    @Value("${message.file.failure.common}")
    private String fileFailureMessage;
    @Value("${message.file.write.failure.common}")
    private String fileWriteFailureMessage;
    @Value("${message.jar.file.failure.common}")
    private String jarfileFailureMessage;
    @Value("${message.config.file.failure.common}")
    private String configFileFailureMessage;
    @Value("${message.validation.header.notExits}")
    private String headerNotExistsMessage;
    @Value("${message.failure.excelPath}")
    private String ExcelPathNotProvideMessage;
    @Value("${message.failure.file.notExits}")
    private String getFileNotExits;

    //Project Code
    @Value("${code.validation.project.alreadyExists}")
    private String projectAlReadyExistCode;
    @Value("${code.validation.project.notExists}")
    private String projectNotExistCode;
    @Value("${code.validation.project.dependent}")
    private String projectIdDependentCode;

    //Project Message
    @Value("${message.validation.project.notExits}")
    private String projectNotExistsMessage;
    @Value("${message.validation.project.dependent}")
    private String projectIdDependentMessage;
    @Value("${message.validation.project.code.alreadyExists}")
    private String projectCodeAlReadyExistMessage;
    @Value("${message.validation.project.Name.alreadyExists}")
    private String projectNameAlReadyExistMessage;
    @Value("${message.validation.project.file}")
    private String projectFileImportValidationMessage;
    @Value("${message.validation.project.file.name.empty}")
    private String projectNameEmptyMessage;
    @Value("${message.validation.project.file.empty}")
    private String projectFileEmptyMessage;
    @Value("${message.validation.project.file.code.empty}")
    private String projectCodeEmptyMessage;
    @Value("${message.validation.project.file.description.empty}")
    private String projectDescriptionEmptyMessage;
    @Value("${message.success.save.project}")
    private String saveProjectSuccessMessage;
    @Value("${message.success.update.project}")
    private String updateProjectSuccessMessage;
    @Value("${message.success.getAll.project}")
    private String getAllProjectSuccessMessage;
    @Value("${message.success.get.project}")
    private String getProjectSuccessMessage;
    @Value("${message.success.delete.project}")
    private String deleteProjectSuccessMessage;
    @Value("${message.validation.save.project}")
    private String saveProjectValidationMessage;
    @Value("${message.validation.project.name.duplicate}")
    private String projectNameDuplicateMessage;
    @Value("${message.validation.project.code.duplicate}")
    private String projectCodeDuplicateMessage;
    @Value("${message.failure.project.configPath}")
    private String projectConfigPathNotProvideMessage;
    @Value("${message.failure.project.jarPath}")
    private String projectJarPathNotProvideMessage;
    @Value("${message.validation.project}")
    private String getTotalProjectCountExceedsTheLimit;

    //SubModules Code
    @Value("${code.validation.subModules.alreadyExists}")
    private String subModulesAlReadyExistCode;
    @Value("${code.validation.subModules.notExists}")
    private String subModulesNotExistCode;
    @Value("${code.validation.subModules.dependent}")
    private String subModulesDependentCode;

    //SubModules Message
    @Value("${message.validation.save.subModule}")
    private String saveSubModuleValidationMessage;
    @Value("${message.validation.subModule.file}")
    private String subModuleFileImportValidationMessage;
    @Value("${message.validation.subModules.file.empty}")
    private String subModulesFileEmptyMessage;
    @Value("${message.validation.subModule.file.name.empty}")
    private String subModuleNameEmptyMessage;
    @Value("${message.validation.subModule.file.prefix.empty}")
    private String subModulePrefixEmptyMessage;
    @Value("${message.validation.subModule.file.main_module_id.empty}")
    private String subModuleMainModuleIdEmptyMessage;
    @Value("${message.validation.subModule.Name.alreadyExists}")
    private String subModuleNameAlReadyExistMessage;
    @Value("${message.validation.subModule.prefix.alreadyExists}")
    private String subModulePrefixAlReadyExistMessage;
    @Value("${message.validation.subModule.notExists}")
    private String subModuleNotExistsMessage;
    @Value("${message.success.save.subModule}")
    private String saveSubModuleSuccessMessage;
    @Value("${message.success.update.subModule}")
    private String updateSubModuleSuccessMessage;
    @Value("${message.success.get.subModules}")
    private String getSubModulesSuccessMessage;
    @Value("${message.success.getAll.subModule}")
    private String getAllSubModuleSuccessMessage;
    @Value("${message.success.delete.subModule}")
    private String deleteSubModuleSuccessMessage;
    @Value("${message.validation.get.mainModule.notHave}")
    private String getSubModuleNotHaveMainModuleId;
    @Value("${message.validation.get.project.notHave}")
    private String getSubModuleNotHaveProjectId;
    @Value("${message.validation.subModules.dependent}")
    private String subModulesDependentMessage;
    @Value("${message.validation.subModule.name.duplicate}")
    private String subModuleNameDuplicateMessage;
    @Value("${message.validation.subModule.prefix.duplicate}")
    private String subModulePrefixDuplicateMessage;
    @Value("${message.success.getSubModules.project}")
    private String subModulesByProjectId;

    //MainModules
    @Value("${code.validation.mainModules.notExists}")
    private String mainModulesNotExistCode;
    @Value("${code.validation.mainModules.alreadyExists}")
    private String AlreadyExistCode;

    // Main Modules messages
    @Value("${message.validation.mainModule.notExists}")
    private String mainModuleNotExistsMessage;
    @Value("${message.failure.module.Id.NotAssigned}")
    private String ModuleIdNotAssigned;
    @Value("${Message.validation.module.Id.AssignedWithAnotherTable}")
    private String IdAssignedWithAnotherTable;
    @Value("${code.validation.mainModule.dependent}")
    private String IdAssignedWithAnotherTableCode;
    @Value("${message.success.mainModules.save}")
    private String SuccessMessageInsertMainModules;
    @Value("${message.success.mainModules.delete}")
    private String SuccessMessageDeleteMainModules;
    @Value("${message.success.mainModules.update}")
    private String SuccessUpdateMessageMainModules;
    @Value("${message.success.mainModules.view}")
    private String SuccessViewAllMessageMainModules;
    @Value("${message.failure.mainModules.name.AlreadyExist}")
    private String mainModulesNameAlreadyExistMessage;
    @Value("${message.failure.mainModules.prefix.AlreadyExist}")
    private String mainModulesPrefixAlreadyExistMessage;
    @Value("${message.failure.module.Id.NotFound}")
    private String ModuleIdNotFound;
    @Value("${message.failure.mainModulesId.NotFound}")
    private String MainModulesIdNotFound;
    @Value("${message.validation.NotSave.mainModules}")
    private String mainModulesNotSavedMessage;
    @Value("${message.validation.mainModule.NameEmpty}")
    private String mainModulesNameFiledEmptyMessage;
    @Value("${message.validation.mainModule.PrefixEmpty}")
    private String mainModulesPrefixFiledEmptyMessage;
    @Value("${message.validation.mainModule.PrefixDuplicate}")
    private String mainModulesPrefixDuplicateMessage;
    @Value("${message.validation.mainModule.NameDuplicate}")
    private String mainModulesNameDuplicateMessage;
    @Value("${message.validation.mainModule.notMapped}")
    private String mainModulesNotMappedWithProjectMessage;

    //Modules Code
    @Value("${code.validation.module.alreadyExists}")
    private String moduleAlReadyExistsCode;
    @Value("${code.validation.module.notExists}")
    private String moduleNotExistsCode;
    @Value("${code.validation.module.dependent}")
    private String moduleDependentCode;

    //Modules Message
    @Value("${message.validation.module.file.empty}")
    private String moduleFileEmptyMessage;
    @Value("${message.success.update.module}")
    private String updateModuleSuccessMessage;
    @Value("${message.validation.module.file.prefix.empty}")
    private String modulePrefixEmptyMessage;
    @Value("${message.validation.module.file.name.empty}")
    private String moduleNameEmptyMessage;
    @Value("${message.validation.module.name.alreadyExists}")
    private String moduleNameAlReadyExistsMessage;
    @Value("${message.success.save.module}")
    private String saveModuleSuccessMessage;
    @Value("${message.validation.module.prefix.alreadyExists}")
    private String modulePrefixAlReadyExistsMessage;
    @Value("${message.validation.module.notExists}")
    private String moduleNotExistsMessage;
    @Value("${message.validation.module.notHaveProject}")
    private String moduleNotHaveProjectMessage;
    @Value("${message.success.delete.module}")
    private String deleteModuleSuccessMessage;
    @Value("${message.success.getAll.module}")
    private String getAllModuleSuccessMessage;
    @Value("${message.success.getById.module}")
    private String getModuleByIdSuccessMessage;
    @Value("${message.success.getByProjectId.module}")
    private String getModuleByProjectIdSuccessMessage;
    @Value("${message.validation.module.assigned}")
    private String getValidationModuleAssignedMessage;
    @Value("${message.validation.save.module}")
    private String saveModuleValidationMessage;
    @Value("${message.validation.module.file.error}")
    private String moduleFileErrorMessage;
    @Value("${message.validation.module.file.projectId.empty}")
    private String moduleProjectIdEmptyMessage;
    @Value("${message.validation.module.name.duplicate}")
    private String moduleNameDuplicateMessage;
    @Value("${message.validation.module.prefix.duplicate}")
    private String modulePrefixDuplicateMessage;
    @Value("${message.success.getAll.module.ByProjectId}")
    private String getAllModulesByProjectId;

    // Test Types Codes
    @Value("${code.validation.testType.alreadyExists}")
    private String TestTypeAlReadyExistCode;
    @Value("${code.validation.testTypes.notExists}")
    private String TestTypeNotExistCode;
    @Value("${code.validation.testTypes.notExists}")
    private String testTypesNotExistCode;
    @Value("${code.validation.testType.dependent}")
    private String testTypeDependentCode;

    //Test Type Messages
    @Value("${message.validation.testTypes.notExists}")
    private String testTypeNotExistMessage;
    @Value("${message.failure.alreadyExist.TestTypeId}")
    private String TestTypeIdAlReadyExistMessage;
    @Value("${message.failure.alreadyExist.TestTypename}")
    private String TestTypeNameAlReadyExistMessage;
    @Value("${message.failure.TestTypeId.NotFound}")
    private String TestTypeIdNotFoundMessage;
    @Value("${message.success.insert.TestTypes}")
    private String insertTestTypesSuccessMessage;
    @Value("${message.success.update.TestTypes}")
    private String updateTestTypeSuccessMessage;
    @Value("${message.success.view.TestTypeForId}")
    private String viewTestTypeforIdSuccessMessage;
    @Value("${message.success.getType.project}")
    private String viewTestTypeByProjectIdSuccessMessage;
    @Value("${message.success.viewAll.TestTypes}")
    private String viewAllTestTypesSuccessMessage;
    @Value("${message.success.delete.TestTypes}")
    private String deleteTestTypesSuccessMessage;
    @Value("${message.validation.testTypes.notExists}")
    private String testTypesNotExistsMessage;
    @Value("${message.validation.NotSave.TestTypes}")
    private String testTypesNotSavedMessage;
    @Value("${Message.validation.testType.Id.AssignedWithAnotherTable}")
    private String testTypeDependentMessage;
    @Value("${message.validation.testType.Name.Duplicate}")
    private String testTypeNameDuplicateMessage;
    @Value("${message.validation.testType.Name.Empty}")
    private String testTypeNameEmptyMessage;
    @Value("${message.validation.testType.Description.Empty}")
    private String testTypeDescriptionEmptyMessage;
    @Value("${message.validation.testType.DoesNot.mapped}")
    private String TestTypeNotMappedMessage;
    @Value("${message.success.getType.project}")
    private String testTypeByProjectId;
    @Value("${message.success.getType.comapanyUser}")
    private String viewTestTypeByCompanyUSerIdSuccessMessage;

    //TestCases code
    @Value("${code.validation.testCases.notExists}")
    private String testCasesNotExistCode;
    @Value("${code.validation.testCases.alreadyExists}")
    private String testCasesAlreadyExistsCode;
    @Value("${code.validation.testcases.dependent}")
    private String testCasesDependentCode;

    //TestCase Message
    @Value("${message.validation.testcase.file.name.empty}")
    private String testCaseNameEmptyMessage;
    @Value("${message.validation.testCases.notExists}")
    private String testCasesNotExistsMessage;
    @Value("${message.validation.testCases.notExists.project}")
    private String testCasesAndProjectNotExistsSameProjectMessage;
    @Value("${message.validation.testCases.name.alreadyExists}")
    private String testCaseNameAlreadyExistsMessage;
    @Value("${message.success.save.testcases}")
    private String saveTestCaseSuccessMessage;
    @Value("${message.success.getById.testCase}")
    private String getTestCaseByIdSuccessMessage;
    @Value("${message.success.delete.testCase}")
    private String deleteTestCaseSuccessMessage;
    @Value("${message.success.deleteAll.testCase}")
    private String onlyDeleteIndependentTestCasesSuccessfullyMessage;
    @Value("${message.success.update.testCase}")
    private String updateTestCaseSuccessMessage;
    @Value("${message.success.getAll.testCases}")
    private String getAllTestCasesSuccessMessage;
    @Value("${message.success.getBySubModuleId.testCase}")
    private String getTestCaseBySubModuleIdSuccessMessage;
    @Value("${message.validation.testCase.notHaveSubModule}")
    private String getTestCaseNotHaveSubModuleIdMessage;
    @Value("${message.validation.testCase.assigned}")
    private String getValidationTestCaseAssignedMessage;
    @Value("${message.validation.testcase.file.error}")
    private String testCaseFileErrorMessage;
    @Value("${message.validation.save.testcase}")
    private String testCaseValidationSaveMessage;
    @Value("${message.validation.testcase.name.duplicate}")
    private String testCaseNameDuplicateMessage;
    @Value("${message.validation.testcase.file.submoduleId.empty}")
    private String testcaseSubModuleIdEmptyMessage;
    @Value("${message.validation.testcase.file.empty}")
    private String testcaseFileEmptyMessage;
    @Value("${message.success.testcase.byProjectId}")
    private String getAllTestCasesSuccessGivenProjectId;
    @Value("${message.validation.testCase.getProject.notHave}")
    private String getTestCaseNotHaveProjectId;
    @Value("${message.validation.testCase.getByModule.notHave}")
    private String getTestCasesNotHaveModuleIdMessage;
    @Value("${message.validation.testCase.getByMainModule.notHave}")
    private String getTestCaseNotHaveMainModuleId;
    @Value("${message.success.testcase.byMainModuleId}")
    private String getAllTestCasesSuccessMainModuleIdMessage;
    @Value("${message.success.testcase.byModuleId}")
    private String getTestCasesByModuleIdSuccessMessage;

    //TestGrouping Code
    @Value("${code.validation.testGrouping.alreadyExists}")
    private String testGroupingAlReadyExistCode;
    @Value("${code.validation.testGrouping.notExists}")
    private String testGroupingNotExistCode;
    @Value("${code.validation.testScenario.notExists}")
    private String testScenarioNotExistCode;
    @Value("${code.validation.testGrouping.Dependent}")
    private String testGroupingDependentCode;

    //TestGrouping Message
    @Value("${message.validation.testGrouping.Name.alreadyExists}")
    private String testGroupingNameAlReadyExistMessage;
    @Value("${message.validation.testGrouping.notExists}")
    private String testGroupingNotExistsMessage;
    @Value("${message.success.save.testGrouping}")
    private String saveTestGroupingSuccessMessage;
    @Value("${message.success.update.testGrouping}")
    private String updateTestGroupingSuccessMessage;
    @Value("${message.success.get.testGrouping}")
    private String getTestGroupingSuccessMessage;
    @Value("${message.success.getAll.testGrouping}")
    private String getAllTestGroupingSuccessMessage;
    @Value("${message.validation.testGrouping.file.empty}")
    private String testGroupingFileEmptyMessage;
    @Value("${message.validation.get.testGrouping.notHave.testCase}")
    private String getTestGroupingNotHaveTestCaseId;
    @Value("${message.validation.get.testGrouping.notHave.project}")
    private String getTestGroupingNotHaveProjectId;
    @Value("${message.validation.get.testGrouping.notHave.testType}")
    private String getTestGroupingNotHaveTestTypeId;
    @Value("${message.validation.delete.testGrouping}")
    private String deleteTestGroupingSuccessMessage;
    @Value("${message.validation.save.testGroup}")
    private String saveTestGroupValidationMessage;
    @Value("${message.validation.testGroup.file}")
    private String TestGroupFileImportValidationMessage;
    @Value("${message.validation.testGroup.file.name.empty}")
    private String TestGroupNameEmptyMessage;
    @Value("${message.validation.testGroup.file.test_case_id.empty}")
    private String TestGroupTestCaseIdEmptyMessage;
    @Value("${message.validation.testGroup.file.test_type_id.empty}")
    private String TestGroupTestTypeIdEmptyMessage;
    @Value("${message.validation.testGrouping.name.duplicate}")
    private String testGroupingNameDuplicateMessage;
    @Value("${message.success.getTestGrouping.project}")
    private String testGroupingByProjectId;
    @Value("${message.success.getTestGrouping.testType}")
    private String testGroupingByTestType;
    @Value("${message.validation.testGrouping.have.testCases}")
    private String WantToOneHaveOneTestScenarioOrOneTestCase;
    @Value("${message.validation.testGrouping.assigned}")
    private String TestGroupingDeleteDependentMessage;
    @Value("${message.success.execution}")
    private String executionSuccessMessage;
    @Value("${message.validation.testGrouping.scheduledTestCases}")
    private String ScheduledTestCasesRemoveMessage;
    @Value("${message.success.testGrouping.scheduledTestCases}")
    private String TestGroupingTestCasesSuccessfully;
    @Value("${message.validation.testGrouping.notHave.scheduledTestCases}")
    private String groupingNotHaveTScheduledTestCases;

    //TestScenario Codes
    @Value("${code.validation.testScenario.alreadyExists}")
    private String testScenariosAlreadyExistCode;
    @Value("${code.validation.testScenario.notExists}")
    private String testScenariosNotExistCode;
    @Value("${code.validation.testScenario.dependent}")
    private String testScenarioDependentCode;

    //TestScenario Messages
    @Value("${message.success.insert.testScenario}")
    private String testScenariosSaveMessage;
    @Value("${message.failure.name.AlreadyExist.testScenario}")
    private String testScenariosNameAlreadyExistMessage;
    @Value("${message.failure.Id.notExist.testScenario}")
    private String testScenariosIdNotExistMessage;
    @Value("${message.success.update.testScenario}")
    private String updateTestScenarioSuccessMessage;
    @Value("${message.failure.name.AlreadyExist.testCases.testScenario}")
    private String testCasesListAlreadyExistMessage;
    @Value("${message.success.view.testScenario}")
    private String testScenarioViewMessage;
    @Value("${code.validation.testScenario.dependent}")
    private String testScenarioIdDependentCode;
    @Value("${message.validation.testScenario.dependent}")
    private String testScenarioIdDependentMessage;
    @Value("${message.validation.testScenario.getProject.notHave}")
    private String getTestScenarioNotHaveProjectId;
    @Value("${message.success.testScenario.byProjectId}")
    private String getAllTestScenarioSuccessGivenProjectId;
    @Value("${message.success.delete.testScenario}")
    private String deleteTestScenarioSuccessMessage;
    @Value("${message.validation.testScenario.notExists}")
    private String testScenarioNotExistsMessage;
    @Value("${message.validation.testScenario.testCasesNil}")
    private String testCasesNotProvidedMessage;
    @Value("${message.validation.testScenario.nameAndId.null}")
    private String testScenarioNameAndIdNullMessage;

    //Execution Code
    @Value("${code.failure.executionHistory.notExist}")
    private String executionHistoryNotExistsCode;
    @Value("${code.failure.executionHistory.email}")
    private String executionHistoryMailFailureCode;
    @Value("${code.error.executionHistory.dates}")
    private String executionHistoryDateErrorCode;

    //Execution History
    @Value("${message.success.executionHistory.Null}")
    private String executionHistoryIdNull;
    @Value("${message.success.executionHistory.notExist}")
    private String executionHistoryNotFound;
    @Value("${message.success.testGrouping.notMapped}")
    private String TestGroupingNotMappedMessage;
    @Value("${message.success.executionHistory.view}")
    private String viewExecutionHistoryMessage;
    @Value("${message.success.delete.executionHistory}")
    private String executionHistoryDeleteSuccessMessage;
    @Value("${message.success.executionHistory.mail}")
    private String executionHistoryMailSuccessMessage;
    @Value("${message.failure.executionHistory.null.empty}")
    private String executionHistoryMailFailureMessage;
    @Value("${message.error.executionHistory.endDate.empty}")
    private String executionHistoryEndDateEmptyMessage;
    @Value("${message.error.endDate}")
    private String executionHistoryEndDateBeforeStartDateMessage;

    //Scheduling Code
    @Value("${code.failure.Id.notExist.scheduling}")
    private String schedulingNotExistCode;

    //Scheduling Messages
    @Value("${message.failure.Id.notExist.scheduling}")
    private String schedulingNotExistMessage;
    @Value("${message.success.delete.scheduling}")
    private String deleteSchedulingSuccessMessage;
    @Value("${message.success.scheduling.viewBY.projectId}")
    private String scheduleViewSuccessMessage;
    @Value("${message.success.save.scheduling}")
    private String saveTestSchedulingSuccessMessage;
    @Value("${message.failure.name.AlreadyExist.Scheduling}")
    private String schedulingNameAlreadyExists;
    @Value("${message.validation.AlreadyExist.Scheduling}")
    private String schedulingAlreadyExists;
    @Value("${message.failure.scheduling.Empty}")
    private String schedulingTestCasesAndScenarioEmpty;
    @Value("${message.success.update.scheduling}")
    private String schedulingUpdateSuccessMessage;
    @Value("${message.failure.Id.notExist.scheduling}")
    private String schedulingIdNotExistMessage;
    @Value("${message.success.get.scheduling}")
    private String getSchedulingSuccessMessage;
    @Value("${message.validation.startDate.null.scheduling}")
    private String StartDateCannotNull;
    @Value("${message.validation.noOfTimes.null.scheduling}")
    private String noOfTimesCannotNull;
    @Value("${message.validation.minutes.selected.scheduling}")
    private String minutesWiseSelected;
    @Value("${message.validation.week.selected.scheduling}")
    private String weekWiseSelected;
    @Value("${message.validation.month.selected.scheduling}")
    private String monthWiseSelected;
    @Value("${message.validation.year.selected.scheduling}")
    private String yearWiseSelected;
    @Value("${message.validation.hour.selected.scheduling}")
    private String hourWiseSelected;
    @Value("${message.validation.select.atleast.scheduling}")
    private String selectAtleastOne;
    @Value("${message.validation.timeAfter.Scheduling}")
    private String startTimeAfterCurrentTime;

    //login Codes
    @Value("${code.validation.user.alreadyExists}")
    private String userAlreadyExistCode;
    @Value("${code.validation.user.notExists}")
    private String userNotExistCode;
    @Value("${code.validation.users.dependent}")
    private String usersDeleteDependentCode;
    @Value("${message.validation.users.dependent}")
    private String usersDeleteDependentMessage;

    //login Messages
    @Value("${message.success.verify.registered}")
    private String RegistrationSuccessMessage;
    @Value("${message.success.email.verify}")
    private String EmailVerificationSuccessMessage;
    @Value("${message.failure.email.verify}")
    private String EmailVerificationFailureMessage;
    @Value("${message.failure.email.notExist}")
    private String EmailNotExistMessage;
    @Value("${message.failure.token.expired}")
    private String TokenExpiredMessage;
    @Value("${message.failure.token.alreadyUsed}")
    private String TokenAlreadyUsedMessage;
    @Value("${message.failure.userName.Password}")
    private String InvalidUserNamePasswordMessage;
    @Value("${message.Success.userName.Password}")
    private String LoginSuccessMessage;
    @Value("${message.failure.email.null}")
    private String EmailCannotNullMessage;
    @Value("${message.failure.password.null}")
    private String PasswordCannotNullMessage;
    @Value("${message.failure.user.deActive}")
    private String UserDeactivatedMessage;
    @Value("${message.failure.user.locked}")
    private String UserLockedMessage;
    @Value("${message.Success.userName.tempPassword}")
    private String TempPasswordLoginSuccessMessage;
    @Value("${message.success.verificationLink.send}")
    private String ResetLinkForwardSuccessMessage;
    @Value("${message.failure.user.mailed}")
    private String UserVerificationPendingMessage;
    @Value("${message.success.validation.user.password.create}")
    private String UserPasswordCreateSuccessMessage;

    // Ragex
    @Value("${message.failure.space}")
    private String SpacesNotAllowedMessage;

    // License Codes
    @Value("${code.validation.License.alreadyExists}")
    private String LicenseAlreadyExistCode;
    @Value("${code.validation.License.notExists}")
    private String LicenseNotExistCode;
    @Value("${code.validation.License.assigned}")
    private String LicenseDeleteDependentCode;

    // License Messages
    @Value("${message.success.insert.License}")
    private String LicenseInsertSuccessMessage;
    @Value("${message.failure.name.AlreadyExist.License}")
    private String LicenseNameAlreadyExistMessage;
    @Value("${message.failure.package.AlreadyExist.License}")
    private String LicensePackageAlreadyExistMessage;
    @Value("${message.failure.package.NotExist.License}")
    private String LicensePackageNotExistMessage;
    @Value("${message.Success.package.Update.License}")
    private String LicenseSuccessfullyUpdatedMessage;
    @Value("${message.Success.package.Delete.License}")
    private String LicenseSuccessfullyDeletedMessage;
    @Value("${message.validation.License.assigned}")
    private String LicenseDeleteDependentMessage;
    @Value("${message.success.get.licenses}")
    private String getLicenseSuccessMessage;
    @Value("${message.success.view.License}")
    private String LicenseViewSuccessMessage;
    @Value("${message.failure.id.notExist.License}")
    private String LicenseIdNotExistMessage;
    @Value("${message.failure.License.name.null}")
    private String LicenseNameNullOrEmptyMessage;
    @Value("${message.failure.LicenceId.null}")
    private String LicenseIdNullMessage;
    @Value("${message.error.reduce.notAllowed.License}")
    private String LicenseReducingNotAllowedMessage;

    //Designation Code
    @Value("${code.validation.designation.alreadyExist}")
    private String DesignationAlreadyExistsCode;
    @Value("${code.validation.Designation.dependent}")
    private String DesignationDependentCode;
    @Value("${code.validation.Designation.notExists}")
    private String DesignationNotExistsCode;
    @Value("${message.success.getById.License}")
    private String LicenseGetByIdSuccessMessage;
    @Value("${code.validation.CompanyUser.dependent}")
    private String CompanyUserDeleteDependentCode;

    // Company User Messages
    @Value("${message.failure.id.notExist.CompanyUser}")
    private String CompanyUserIdNotExistMessage;
    @Value("${message.success.delete.CompanyUser}")
    private String CompanyUserDeleteSuccessMessage;
    @Value("${message.validation.companyUser.dependent}")
    private String CompanyUserDeleteDependentMessage;
    @Value("${message.success.companyUser.getById}")
    private String getCompanyUserByIdSuccessMessage;
    @Value("${message.validation.companyUser.name.alreadyExists}")
    private String companyUserNameAlReadyExistsMessage;
    @Value("${message.validation.companyUser.email.alreadyExists}")
    private String companyUserEmailAlReadyExistsMessage;
    @Value("${message.validation.companyUser.contactNo.alreadyExists}")
    private String companyUserContactNoAlReadyExistsMessage;
    @Value("${message.success.update.companyUser}")
    private String updateCompanyUserSuccessMessage;
    @Value("${message.validation.get.designation.notHave}")
    private String getCompanyuserIdNotHaveDesignation;
    @Value("${message.failure.package.NotExist.designation}")
    private String designationNotExistMessage;
    @Value("${message.Success.package.Delete.Designation}")
    private String designationSuccessfullyDeletedMessage;
    @Value("${message.success.designation.getById}")
    private String getDesignationByIdSuccessMessage;
    @Value("${message.success.companyUser.getAll}")
    public String getAllCompanyUserSuccessfully;
    @Value("${message.success.insert.CompanyUser}")
    private String CompanyUserSuccessfullyInsertedMessage;
    @Value("${message.failure.name.AlreadyExist.CompanyUser}")
    private String CompanyUserNameAlreadyExistMessage;
    @Value("${message.failure.license.Id.NotFound}")
    private String LicenseIdNotFoundMessage;
    @Value("${message.failure.contactNumber.AlreadyExist.CompanyUser}")
    private String CompanyUserContactNumberAlreadyExistMessage;
    @Value("${message.failure.email.AlreadyExist.CompanyUser}")
    private String CompanyUserEmailAlreadyExistMessage;
    @Value("${message.failure.CompanyUser.StartDate}")
    private String StartDateNotGiven;
    @Value("${message.failure.companyUserId.null}")
    private String CompanyUserIdNullMessage;
    @Value("${message.validation.companyUser.name.null}")
    private String companyUserNameNull;

    //Designation Message
    @Value("${message.validation.designation.notExists}")
    private String designationNotExistsMessage;
    @Value("${message.validation.designation.alreadyExist}")
    private String DesignationAlreadyExistsMessage;
    @Value("${message.success.designation.save}")
    private String designationSaveSuccessMessage;
    @Value("${message.success.get.designation}")
    private String getDesignationSuccessMessage;
    @Value("${message.success.designation.update}")
    private String designationUpdateSuccessMessage;
    @Value("${message.validation.designation.NotExist}")
    private String DesignationNotExistsMessage;
    @Value("${message.failure.DesignationId.null}")
    private String DesignationIdNullMessage;
    @Value("${message.validation.Designation.assigned}")
    private String designationDeleteDependentMessage;
    @Value("${message.failure.designation.nullValues}")
    private String DesignationNullValuesMessage;
    @Value("${message.failure.designation.notCompanyAdmin}")
    private String itsNotCompanyAdminMessage;

    //companyUser code
    @Value("${code.validation.companyUser.alreadyExists}")
    private String companyUserAlReadyExistsCode;
    @Value("${code.validation.companyUser.notExists}")
    private String companyUserNotExistsCode;
    @Value("${code.validation.CompanyUser.dependent}")
    private String CompanyUserDependentCode;
    @Value("${code.validation.CompanyUser.emailAlreadyExists}")
    private String CompanyUserEmailAlreadyExistsCode;
    @Value("${code.validation.CompanyUser.contactNumberAlreadyExists}")
    private String CompanyUserContactNumberAlreadyExistsCode;

    //CompanyUser Message
    @Value("${message.failure.companyUser.licenseId}")
    private String CompanyUserLicenseIdNotGivenMessage;
    @Value("${message.failure.companyUser.contactNumber}")
    private String CompanyUserContactNumberNotGivenMessage;
    @Value("${message.failure.CompanyUser.email}")
    private String CompanyUserEmailNotGiven;
    @Value("${message.validation.CompanyUser.contactNumber.not}")
    private String CompanyUserContactNumberNotGiven;
    @Value("${message.failure.companyUser.Id}")
    private String CompanyUserIdNotGivenMessage;
    @Value("${message.failure.companyUser.companyname}")
    private String CompanyNameNotGivenMessage;
    //User Codes
    @Value("${code.validation.user.alreadyExists}")
    private String UserAlreadyExistsCode;
    @Value("${code.validation.user.notExists}")
    private String UserNotExistsCode;
    @Value("${message.failure.ComapanyUser.Id.NotFound}")
    private String comapanyUserIdNotFound;

    // User Messages
    @Value("${message.validation.user.email.alreadyExists}")
    private String UserEmailAlreadyExistMessage;
    @Value("${message.validation.user.name.alreadyExists}")
    private String UserNameAlreadyExistMessage;
    @Value("${message.validation.user.notExist}")
    private String UserIdNotExistMessage;
    @Value("${message.success.update.user}")
    private String UserUpdateSuccessMessage;
    @Value("${message.validation.user.contactNumber.alreadyExists}")
    private String UserContactNumberAlreadyExistMessage;
    @Value("${message.failure.userId.null}")
    private String UserIdCannotBeNullMessage;
    @Value("${message.failure.companyIdNot.Assigned}")
    private String CompanyIdNotAssignedForUserMessage;
    @Value("${message.success.GetAllUser.ByCompanyId}")
    private String AllUserByCompanyIdMessage;
    @Value("${message.success.getById.user}")
    private String getUserByIdSuccessMessage;
    @Value("${message.success.save.user}")
    private String saveUserSuccessMessage;
    @Value("${message.validation.user.name.alreadyExists}")
    private String userIdExistMessage;
    @Value("${message.validation.user.contactNo.alreadyExists}")
    private String UserContactNoAlReadyExistsMessage;
    @Value("${message.success.delete.user}")
    private String userDeleteSuccessMessage;
    @Value("${message.validation.user.contact}")
    private String UserContactNumberNotGiven;
    @Value("${message.failure.user.email.not}")
    private String UserEmailNotGiven;
    @Value("${message.validation.user.firstName}")
    private String UserFirstNameNotGiven;
    @Value("${message.validation.user.companyUserId}")
    private String UserCompanyUserIdNotGiven;
    @Value("${message.validation.user.designationId}")
    private String UserDesignationIdNotGiven;
    @Value("${message.validation.user.email.cannot.change}")
    private String UserEmailCannotChangeMessage;
    @Value("${message.success.getId.user}")
    private String getUserDetailsSuccessMessage;
    @Value("${message.validation.user}")
    private String getTotalUserCountExceedsTheLimit;
    @Value("${message.Success.Email.Password}")
    private String EmailSuccessFullySend;

}