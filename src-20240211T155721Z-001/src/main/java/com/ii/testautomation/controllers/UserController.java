package com.ii.testautomation.controllers;

import com.ii.testautomation.dto.request.UserRequest;
import com.ii.testautomation.dto.search.UserSearch;
import com.ii.testautomation.entities.Users;
import com.ii.testautomation.enums.LoginStatus;
import com.ii.testautomation.enums.RequestStatus;
import com.ii.testautomation.repositories.UserRepository;
import com.ii.testautomation.response.common.BaseResponse;
import com.ii.testautomation.response.common.ContentResponse;
import com.ii.testautomation.response.common.PaginatedContentResponse;
import com.ii.testautomation.service.CompanyUserService;
import com.ii.testautomation.service.DesignationService;
import com.ii.testautomation.service.EmailAndTokenService;
import com.ii.testautomation.service.ProjectService;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private StatusCodeBundle statusCodeBundle;
    @Autowired
    private CompanyUserService companyUserService;
    @Autowired
    private DesignationService designationService;
    @Autowired
    private EmailAndTokenService emailAndTokenService;

    @PutMapping(EndpointURI.USERS)
    public ResponseEntity<Object> updateUser(@RequestBody UserRequest userRequest) {
        if (userRequest.getId() == null)
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getUserIdCannotBeNullMessage()));
        if (userRequest.getCompanyUserId() == null)
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getCompanyUserIdNullMessage()));
        if (userRequest.getDesignationId() == null)
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getDesignationIdNullMessage()));
        if (!userService.existsByUserId(userRequest.getId()))
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getUserNotExistsCode(), statusCodeBundle.getUserIdNotExistMessage()));
        if (userService.existsByEmailAndIdNot(userRequest.getEmail(), userRequest.getId()))
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getUserAlreadyExistsCode(), statusCodeBundle.getUserEmailAlreadyExistMessage()));
        if (!designationService.existById(userRequest.getDesignationId()))
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getDesignationNotExistsCode(), statusCodeBundle.getDesignationNotExistsMessage()));
        if (userService.existsByContactNumberAndIdNot(userRequest.getContactNumber(), userRequest.getId()))
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getUserAlreadyExistsCode(), statusCodeBundle.getUserContactNumberAlreadyExistMessage()));
        if (userRequest.getStatus()){
            if (!userService.totalCountUser(userRequest.getCompanyUserId()))
                return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), "Total user count exceeds the limit."));
        }
            userService.updateUser(userRequest);
        return ResponseEntity.ok(new BaseResponse(RequestStatus.SUCCESS.getStatus(), statusCodeBundle.getCommonSuccessCode(), statusCodeBundle.getUserUpdateSuccessMessage()));
    }

    @PostMapping(EndpointURI.VERIFY_USER)
    public ResponseEntity<Object> verifyUser(@PathVariable String token) {
        if (emailAndTokenService.verifyToken(token).equals(statusCodeBundle.getTokenExpiredMessage()))
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getTokenExpiredMessage()));
        if (emailAndTokenService.verifyToken(token).equals(statusCodeBundle.getEmailVerificationFailureMessage()))
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getEmailVerificationFailureMessage()));
        if (emailAndTokenService.verifyToken(token).equals(statusCodeBundle.getTokenAlreadyUsedMessage()))
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getTokenAlreadyUsedMessage()));
        emailAndTokenService.sendTempPasswordToEmail(token);
        return ResponseEntity.ok(new BaseResponse(RequestStatus.SUCCESS.getStatus(), statusCodeBundle.getCommonSuccessCode(), statusCodeBundle.getEmailVerificationSuccessMessage()));
    }

    @PostMapping(value = EndpointURI.USERS)
    public ResponseEntity<Object> saveUser(@RequestBody UserRequest userRequest) {
        if (userRequest.getDesignationId() == null) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getUserDesignationIdNotGiven()));
        }
        if (userRequest.getEmail() == null || userRequest.getEmail().isEmpty()) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getUserEmailNotGiven()));
        }
        if (userRequest.getFirstName() == null || userRequest.getFirstName().isEmpty()) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getUserFirstNameNotGiven()));
        }
        if (userRequest.getContactNumber() == null || userRequest.getContactNumber().isEmpty()) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getUserContactNumberNotGiven()));
        }
        if (!designationService.existById(userRequest.getDesignationId())) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getDesignationNotExistsCode(), statusCodeBundle.getDesignationNotExistsMessage()));
        }
        if (userService.existsByEmail(userRequest.getEmail())) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getUserEmailAlreadyExistMessage()));
        }
        if (userService.existsByContactNo(userRequest.getContactNumber())) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getUserContactNoAlReadyExistsMessage()));
        }
        if (!userService.totalCountUser(userRequest.getCompanyUserId())) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), "Total user count exceeds the limit."));
        }
        userService.saveUser(userRequest);
        return ResponseEntity.ok(new BaseResponse(RequestStatus.SUCCESS.getStatus(), statusCodeBundle.getCommonSuccessCode(), statusCodeBundle.getSaveUserSuccessMessage()));
    }

    @GetMapping(value = EndpointURI.USERS_BY_COMPANY_ID)
    public ResponseEntity<Object> getAllUserByCompanyIdWithPagination(@PathVariable Long id, @RequestParam(name = "page") int page, @RequestParam(name = "size") int size, @RequestParam(name = "direction") String direction, @RequestParam(name = "sortField") String sortField, UserSearch userSearch) {
        if (!companyUserService.existsById(id))
        return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getCompanyUserIdNotExistMessage()));
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.valueOf(direction), sortField);
        PaginatedContentResponse.Pagination pagination = new PaginatedContentResponse.Pagination(page, size, 0, 0L);
        return ResponseEntity.ok(new ContentResponse<>(Constants.USERS, userService.getAllUserByCompanyUserId(pageable, pagination, id, userSearch), RequestStatus.SUCCESS.getStatus(), statusCodeBundle.getCommonSuccessCode(), statusCodeBundle.getAllUserByCompanyIdMessage()));
    }

    @DeleteMapping(value = EndpointURI.USERS_DELETE)
    public ResponseEntity<Object> deleteUser(@PathVariable Long id) {
        if (!userService.existsByUsersId(id)) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getUserNotExistsCode(), statusCodeBundle.getUserIdExistMessage()));
        }
        if (projectService.existsByUsersId(id)) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getUsersDeleteDependentCode(), statusCodeBundle.getUsersDeleteDependentMessage()));
        }
        userService.deleteUserById(id);
        return ResponseEntity.ok(new BaseResponse(RequestStatus.SUCCESS.getStatus(), statusCodeBundle.getCommonSuccessCode(), statusCodeBundle.getUserDeleteSuccessMessage()));
    }

    @GetMapping(value = EndpointURI.USER_BY_ID)
    public ResponseEntity<Object> getUserById(@PathVariable Long id) {
        if (!userService.existsByUserId(id))
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getUserNotExistsCode(), statusCodeBundle.getUserIdNotExistMessage()));
        return ResponseEntity.ok(new ContentResponse<>(Constants.USERS, userService.getUserById(id), RequestStatus.SUCCESS.getStatus(), statusCodeBundle.getCommonSuccessCode(), statusCodeBundle.getGetUserByIdSuccessMessage()));
    }

    @PostMapping(EndpointURI.USER_LOGIN)
    public ResponseEntity<Object> loginUser(@RequestBody UserRequest userRequest) {
           if (userRequest.getEmail() == null || userRequest.getEmail().isEmpty())
            return ResponseEntity.ok(new BaseResponse(RequestStatus.ERROR.getStatus(), statusCodeBundle.getNullValuesCode(), statusCodeBundle.getEmailCannotNullMessage()));
        else if (userRequest.getPassword() == null || userRequest.getPassword().isEmpty())
            return ResponseEntity.ok(new BaseResponse(RequestStatus.ERROR.getStatus(), statusCodeBundle.getNullValuesCode(), statusCodeBundle.getPasswordCannotNullMessage()));
        else if (!userService.existsByEmail(userRequest.getEmail()))
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getUserNotExistsCode(), statusCodeBundle.getInvalidUserNamePasswordMessage()));
        else if (userService.existsByStatusAndEmail(LoginStatus.DEACTIVATE.getStatus(), userRequest.getEmail()))
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getUserDeactivatedMessage()));
           else if (userService.existsByStatusAndEmail(LoginStatus.MAILED.getStatus(), userRequest.getEmail()))
               return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getUserVerificationPendingMessage()));
           else if (companyUserService.existsByStatusAndEmail(false, userRequest.getEmail()))
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getUserDeactivatedMessage()));
        else if (userService.existsByStatusAndEmail(LoginStatus.LOCKED.getStatus(), userRequest.getEmail()))
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getUserLockedMessage()));
        else if (userService.existsByEmailAndPassword(userRequest.getEmail(), userRequest.getPassword())) {
               if (userService.existsByStatusAndEmail(LoginStatus.ACTIVE.getStatus(), userRequest.getEmail()))
                   return ResponseEntity.ok(new ContentResponse<>(Constants.TOKEN, userService.generateNonExpiringToken(userRequest.getEmail()), RequestStatus.SUCCESS.getStatus(), statusCodeBundle.getCommonSuccessCode(), statusCodeBundle.getLoginSuccessMessage()));
               if (userService.existsByStatusAndEmail(LoginStatus.PENDING.getStatus(), userRequest.getEmail()))
                   return ResponseEntity.ok(new BaseResponse(RequestStatus.SUCCESS.getStatus(), statusCodeBundle.getCommonSuccessCode(), statusCodeBundle.getTempPasswordLoginSuccessMessage()));
        } else if (userService.existsByEmail(userRequest.getEmail()) && !userService.existsByEmailAndPassword(userRequest.getEmail(), userRequest.getPassword())) {
            userService.invalidPassword(userRequest.getEmail());
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getInvalidUserNamePasswordMessage()));
        }
        return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getInvalidUserNamePasswordMessage()));
    }

    @PostMapping(EndpointURI.USERS_PASSWORD)
    public ResponseEntity<Object> createPassword(@RequestHeader(name = "token", required = false) String token, @RequestBody UserRequest userRequest) {
        if (token != null) {
            if (emailAndTokenService.verifyToken(token).equals(statusCodeBundle.getTokenExpiredMessage()))
                return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getTokenExpiredMessage()));
            if (emailAndTokenService.verifyToken(token).equals(statusCodeBundle.getEmailVerificationFailureMessage()))
                return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getEmailVerificationFailureMessage()));
            if (emailAndTokenService.verifyToken(token).equals(statusCodeBundle.getTokenAlreadyUsedMessage()))
                return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getTokenAlreadyUsedMessage()));
        }
        userService.changePassword(token, userRequest.getEmail(), userRequest.getPassword());
        return ResponseEntity.ok(new BaseResponse(RequestStatus.SUCCESS.getStatus(), statusCodeBundle.getCommonSuccessCode(), statusCodeBundle.getUserPasswordCreateSuccessMessage()));

    }

    @PostMapping(EndpointURI.USERS_SENDEMAIL)
    public ResponseEntity<Object> sendMail(@RequestBody UserRequest userRequest) {
        String email = userRequest.getEmail();
        if (!userService.existsByEmail(email))
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getUserNotExistsCode(),statusCodeBundle.getEmailNotExistMessage()));
        if(companyUserService.existsByStatusAndEmail(false,email))
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getUserDeactivatedMessage()));
        if(userService.existsByStatusAndEmail(LoginStatus.DEACTIVATE.getStatus(), email))
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getUserDeactivatedMessage()));
        userService.sendMail(email);
        return ResponseEntity.ok(new BaseResponse(RequestStatus.SUCCESS.getStatus(), statusCodeBundle.getCommonSuccessCode(),statusCodeBundle.getEmailSuccessFullySend()));
    }
}