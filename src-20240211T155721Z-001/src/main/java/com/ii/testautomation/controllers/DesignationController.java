package com.ii.testautomation.controllers;

import com.ii.testautomation.dto.request.DesignationRequest;
import com.ii.testautomation.dto.response.DesignationResponse;
import com.ii.testautomation.enums.RequestStatus;
import com.ii.testautomation.response.common.BaseResponse;
import com.ii.testautomation.response.common.ContentResponse;
import com.ii.testautomation.service.CompanyUserService;
import com.ii.testautomation.service.DesignationService;
import com.ii.testautomation.service.UserService;
import com.ii.testautomation.utils.Constants;
import com.ii.testautomation.utils.EndpointURI;
import com.ii.testautomation.utils.StatusCodeBundle;
import com.ii.testautomation.utils.Utils;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class DesignationController {
    @Autowired
    private StatusCodeBundle statusCodeBundle;
    @Autowired
    private UserService userService;
    @Autowired
    private DesignationService designationService;
    @Autowired
    private CompanyUserService companyUserService;

    @PostMapping(EndpointURI.DESIGNATION)
    public ResponseEntity<Object> saveDesignation(@RequestBody DesignationRequest designationRequest) {
        if (designationRequest.getName().isEmpty() || designationRequest.getName() == null) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getDesignationNullValuesMessage()));
        }
        if (!Utils.checkRegexBeforeAfterWords(designationRequest.getName())) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(),
                    statusCodeBundle.getFailureCode(),
                    statusCodeBundle.getSpacesNotAllowedMessage()));
        }
        if (!companyUserService.existsByCompanyUserId(designationRequest.getCompanyUserId())) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getCompanyUserNotExistsCode(), statusCodeBundle.getCompanyUserIdNotExistMessage()));
        }
        if (designationService.existsByNameAndCompanyAdminId(designationRequest.getName(),designationRequest.getCompanyUserId())) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getDesignationAlreadyExistsCode(), statusCodeBundle.getDesignationAlreadyExistsMessage()));
        }
        designationService.saveDesignation(designationRequest);
        return ResponseEntity.ok(new BaseResponse(RequestStatus.SUCCESS.getStatus(), statusCodeBundle.getCommonSuccessCode(), statusCodeBundle.getDesignationSaveSuccessMessage()));
    }

    @GetMapping(value = EndpointURI.DESIGNATION_BY_COMPANY_ID)
    public ResponseEntity<Object> getAllDesignationsByCompanyAdminId(@PathVariable Long userId) {

        if (!companyUserService.existsByCompanyUserId(userId)) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getCompanyUserNotExistsCode(), statusCodeBundle.getCompanyUserIdNotExistMessage()));
        }
        List<DesignationResponse> designations = designationService.getAllDesignationByCompanyAdminId(userId);

        if (designations.isEmpty() && designations.equals(null)) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getGetCompanyuserIdNotHaveDesignation()));
        }
        return ResponseEntity.ok(new ContentResponse<>(Constants.DESIGNATIONS, designationService.getAllDesignationByCompanyAdminId(userId), RequestStatus.SUCCESS.getStatus(), statusCodeBundle.getCommonSuccessCode(), statusCodeBundle.getGetDesignationSuccessMessage()));
    }

    @DeleteMapping(EndpointURI.DESIGNATION_BY_ID)
    public ResponseEntity<Object> deleteDesignation(@PathVariable Long id) {

        if (!designationService.existById(id)) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getDesignationNotExistsCode(), statusCodeBundle.getDesignationNotExistMessage()));
        }
        if (userService.existsByDesignationId(id)) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getDesignationDependentCode(), statusCodeBundle.getDesignationDeleteDependentMessage()));
        }
        designationService.deleteDesignationById(id);
        return ResponseEntity.ok(new BaseResponse(RequestStatus.SUCCESS.getStatus(), statusCodeBundle.getCommonSuccessCode(), statusCodeBundle.getDesignationSuccessfullyDeletedMessage()));
    }

    @PutMapping(EndpointURI.DESIGNATION)
    public ResponseEntity<Object> updateDesignation(@RequestBody DesignationRequest designationRequest) {

        if (designationRequest.getId() == null || designationRequest.getName() == null)
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getDesignationNullValuesMessage()));
        if (!designationService.existById(designationRequest.getId()))
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getDesignationNotExistsCode(), statusCodeBundle.getDesignationNotExistsMessage()));
        if (designationService.existsByNameIdNot(designationRequest.getId(), designationRequest.getName()))
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getDesignationAlreadyExistsCode(), statusCodeBundle.getDesignationAlreadyExistsMessage()));
        if (!companyUserService.existsByCompanyUserId(designationRequest.getCompanyUserId())) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getCompanyUserNotExistsCode(), statusCodeBundle.getCompanyUserIdNotExistMessage()));
        }

        designationService.saveDesignation(designationRequest);
        return ResponseEntity.ok(new BaseResponse(RequestStatus.SUCCESS.getStatus(), statusCodeBundle.getCommonSuccessCode(), statusCodeBundle.getDesignationUpdateSuccessMessage()));
    }

    @GetMapping(value = EndpointURI.DESIGNATION_BY_ID)
    public ResponseEntity<Object> GetDesignationById(@PathVariable Long id) {
        if (!designationService.existById(id)) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getDesignationNotExistsCode(), statusCodeBundle.getDesignationNotExistsMessage()));
        }
        return ResponseEntity.ok(new ContentResponse<>(Constants.DESIGNATION, designationService.getDesignationById(id), RequestStatus.SUCCESS.getStatus(), statusCodeBundle.getCommonSuccessCode(), statusCodeBundle.getGetDesignationByIdSuccessMessage()));
    }

    @GetMapping(value = EndpointURI.DESIGNATIONS_BY_COMPANY_USER_ID)
    public ResponseEntity<Object> getAllDesignationByCompanyUserId(@PathVariable Long companyUserId) {
        if(!companyUserService.existsById(companyUserId)){
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(),statusCodeBundle.getFailureCode(),statusCodeBundle.getCompanyUserIdNotExistMessage()));
        }
        return ResponseEntity.ok(new ContentResponse<>(Constants.DESIGNATION,
                designationService.getAllDesignationByCompanyUserId(companyUserId),
                RequestStatus.SUCCESS.getStatus(),
                statusCodeBundle.getCommonSuccessCode(),
                statusCodeBundle.getGetDesignationSuccessMessage()));
    }

}
