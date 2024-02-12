package com.ii.testautomation.controllers;

import com.ii.testautomation.dto.request.CompanyUserRequest;
import com.ii.testautomation.dto.search.CompanyUserSearch;
import com.ii.testautomation.entities.CompanyUser;
import com.ii.testautomation.enums.RequestStatus;
import com.ii.testautomation.response.common.BaseResponse;
import com.ii.testautomation.response.common.ContentResponse;
import com.ii.testautomation.response.common.PaginatedContentResponse;
import com.ii.testautomation.service.CompanyUserService;
import com.ii.testautomation.service.DesignationService;
import com.ii.testautomation.service.LicenseService;
import com.ii.testautomation.service.UserService;
import com.ii.testautomation.utils.Constants;
import com.ii.testautomation.utils.EndpointURI;
import com.ii.testautomation.utils.StatusCodeBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@CrossOrigin
public class CompanyUserController {
    @Autowired
    private LicenseService licenseService;
    @Autowired
    private StatusCodeBundle statusCodeBundle;
    @Autowired
    private CompanyUserService companyUserService;
    @Autowired
    private UserService userService;
    @Autowired
    private DesignationService designationService;

    @PostMapping(EndpointURI.COMPANY_USERS)
    public ResponseEntity<Object> saveCompanyUser(@RequestBody CompanyUserRequest companyUserRequest) {

        if(companyUserRequest.getLicenses_id()==null) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getCompanyUserLicenseIdNotGivenMessage()));
        }
        if(companyUserRequest.getContactNumber()==null){
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(),statusCodeBundle.getCompanyUserContactNumberNotGiven()));
        }
        if(companyUserRequest.getEmail()==null){
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(),statusCodeBundle.getCompanyUserEmailNotGiven()));
        }
        if(companyUserRequest.getCompanyName()==null){
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(),statusCodeBundle.getCompanyNameNotGivenMessage()));
        }
        if (companyUserRequest.getStartDate() == null ) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getStartDateNotGiven()));
        }
        if (!licenseService.existsById(companyUserRequest.getLicenses_id())) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getLicenseNotExistCode(), statusCodeBundle.getLicenseIdNotFoundMessage()));
        }
        if (companyUserService.isExistCompanyUserName(companyUserRequest.getCompanyName())) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getCompanyUserNotExistsCode(), statusCodeBundle.getCompanyUserNameAlreadyExistMessage()));
        }
        if (companyUserService.isExistByCompanyUserEmail(companyUserRequest.getEmail())) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getCompanyUserEmailAlreadyExistsCode(), statusCodeBundle.getCompanyUserEmailAlreadyExistMessage()));
        }
        if (companyUserService.isExistByCompanyUserContactNumber(companyUserRequest.getContactNumber())) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getCompanyUserContactNumberAlreadyExistsCode(), statusCodeBundle.getCompanyUserContactNumberAlreadyExistMessage()));
        }
        companyUserService.saveCompanyUser(companyUserRequest);
        return ResponseEntity.ok(new BaseResponse(RequestStatus.SUCCESS.getStatus(), statusCodeBundle.getCommonSuccessCode(), statusCodeBundle.getCompanyUserSuccessfullyInsertedMessage()));
    }

    @PutMapping(value = EndpointURI.COMPANY_USERS)
    public ResponseEntity<Object> UpdateCompanyUser(@RequestBody CompanyUserRequest companyUserRequest) {
        if(companyUserRequest.getId()==null){
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(),statusCodeBundle.getCompanyUserIdNotGivenMessage()));
        }
        if(!(companyUserRequest.getEmail()==null)){
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(),statusCodeBundle.getCompanyUserEmailNotGiven()));
        }
        if (!licenseService.existsById(companyUserRequest.getLicenses_id())) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getLicenseNotExistCode(), statusCodeBundle.getLicenseIdNotExistMessage()));
        }
        if (!companyUserService.existsByCompanyUserId(companyUserRequest.getId())) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getCompanyUserNotExistsCode(), statusCodeBundle.getCompanyUserIdNotExistMessage()));
        }
        if (companyUserService.isUpdateCompanyUserNameExists(companyUserRequest.getCompanyName(), companyUserRequest.getLicenses_id(), companyUserRequest.getId())) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getCompanyUserAlReadyExistsCode(), statusCodeBundle.getCompanyUserNameAlReadyExistsMessage()));
        }
        if (companyUserRequest.getCompanyName() == null || companyUserRequest.getCompanyName().isEmpty()) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getCompanyUserNameNull()));
        }
        if (companyUserService.isUpdateEmailExists(companyUserRequest.getEmail(), companyUserRequest.getId())) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getCompanyUserAlReadyExistsCode(), statusCodeBundle.getCompanyUserEmailAlReadyExistsMessage()));
        }
        if (companyUserService.isUpdateCompanyUserContactNumberExists(companyUserRequest.getContactNumber(), companyUserRequest.getId())) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getCompanyUserAlReadyExistsCode(), statusCodeBundle.getCompanyUserContactNoAlReadyExistsMessage()));
        }
        if (!licenseService.checkLicenseReduce(companyUserService.findByCompanyUserId(companyUserRequest.getId()).getLicenses().getId(), companyUserRequest.getLicenses_id()))
            return ResponseEntity.ok(new BaseResponse(RequestStatus.ERROR.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getLicenseReducingNotAllowedMessage()));

        companyUserService.updateCompanyUser(companyUserRequest);
        return ResponseEntity.ok(new BaseResponse(RequestStatus.SUCCESS.getStatus(), statusCodeBundle.getCommonSuccessCode(), statusCodeBundle.getUpdateCompanyUserSuccessMessage()));
    }

    @DeleteMapping(EndpointURI.COMPANY_USER_BY_ID)
    public ResponseEntity<Object> deleteById(@PathVariable Long id) {
        if (!companyUserService.existsById(id))
       return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getCompanyUserIdNotExistMessage()));
        if(designationService.existByCompanyAdminId(id))
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(),statusCodeBundle.getCompanyUserDependentCode(), statusCodeBundle.getCompanyUserDeleteDependentMessage()));
        companyUserService.deleteById(id);
        return ResponseEntity.ok(new BaseResponse(RequestStatus.SUCCESS.getStatus(), statusCodeBundle.getCommonSuccessCode(), statusCodeBundle.getCompanyUserDeleteSuccessMessage()));
    }

    @GetMapping(EndpointURI.COMPANY_USERS)
    public ResponseEntity<Object> getAllCompanyUsers(@RequestParam(name = "page") int page, @RequestParam(name = "size") int size, @RequestParam(name = "direction") String direction, @RequestParam(name = "sortField") String sortField, CompanyUserSearch companyUserSearch) {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.valueOf(direction), sortField);
        PaginatedContentResponse.Pagination pagination = new PaginatedContentResponse.Pagination(page, size, 0, 0L);
        return ResponseEntity.ok(new PaginatedContentResponse<>(Constants.COMPANY_USERS, companyUserService.getAllCompanyUserWithMultiSearch(pageable, pagination, companyUserSearch), RequestStatus.SUCCESS.getStatus(), statusCodeBundle.getCommonSuccessCode(), statusCodeBundle.getAllCompanyUserSuccessfully, pagination));
    }

    @GetMapping(value = EndpointURI.COMPANY_USER_BY_ID)
    public ResponseEntity<Object> GetCompanyUserById(@PathVariable Long id) {
        if (!companyUserService.existsById(id)) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getCompanyUserNotExistsCode(), statusCodeBundle.getCompanyUserIdNotExistMessage()));
        }
        return ResponseEntity.ok(new ContentResponse<>(Constants.COMPANY_USERS, companyUserService.getCompanyUserById(id), RequestStatus.SUCCESS.getStatus(), statusCodeBundle.getCommonSuccessCode(), statusCodeBundle.getGetCompanyUserByIdSuccessMessage()));
    }
}
