package com.ii.testautomation.controllers;

import com.ii.testautomation.dto.request.LicenseRequest;
import com.ii.testautomation.dto.search.LicensesSearch;
import com.ii.testautomation.entities.Licenses;
import com.ii.testautomation.enums.RequestStatus;
import com.ii.testautomation.response.common.BaseResponse;
import com.ii.testautomation.response.common.ContentResponse;
import com.ii.testautomation.response.common.PaginatedContentResponse;
import com.ii.testautomation.service.CompanyUserService;
import com.ii.testautomation.service.LicenseService;
import com.ii.testautomation.utils.Constants;
import com.ii.testautomation.utils.EndpointURI;
import com.ii.testautomation.utils.StatusCodeBundle;
import com.ii.testautomation.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class LicenseController {
    @Autowired
    private LicenseService licenseService;
    @Autowired
    private StatusCodeBundle statusCodeBundle;
    @Autowired
    private CompanyUserService companyUserService;

    @GetMapping(value = EndpointURI.LICENSES)
    public ResponseEntity<Object> getAllLicense(@RequestParam(name = "page") int page, @RequestParam(name = "size") int size, @RequestParam(name = "direction") String direction, @RequestParam(name = "sortField") String sortField, LicensesSearch licensesSearch) {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.valueOf(direction), sortField);
        PaginatedContentResponse.Pagination pagination = new PaginatedContentResponse.Pagination(page, size, 0, 0L);
        return ResponseEntity.ok(new ContentResponse<>(Constants.LICENSE, licenseService.multiSearchLicensesWithPagination(pageable, pagination, licensesSearch), RequestStatus.SUCCESS.getStatus(), statusCodeBundle.getCommonSuccessCode(), statusCodeBundle.getGetLicenseSuccessMessage()));
    }

    @PostMapping(EndpointURI.LICENSE)
    public ResponseEntity<Object> createLicense(@RequestBody LicenseRequest licenseRequest) {
        if (licenseRequest.getName() == null || licenseRequest.getName().isEmpty())
        return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getLicenseNameNullOrEmptyMessage()));
        if (!Utils.checkRegexBeforeAfterWords(licenseRequest.getName()))
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getFailureCode(), statusCodeBundle.getSpacesNotAllowedMessage()));
        if (licenseService.existsByName(licenseRequest.getName()))
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getLicenseAlreadyExistCode(), statusCodeBundle.getLicenseNameAlreadyExistMessage()));
        if (licenseService.existsByDurationAndNoOfProjectsAndNoOfUsers(licenseRequest.getDuration(), licenseRequest.getNoOfProjects(), licenseRequest.getNoOfUsers()))
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getLicenseAlreadyExistCode(), statusCodeBundle.getLicensePackageAlreadyExistMessage()));
        licenseService.createLicense(licenseRequest);
        return ResponseEntity.ok(new BaseResponse(RequestStatus.SUCCESS.getStatus(), statusCodeBundle.getCommonSuccessCode(), statusCodeBundle.getLicenseInsertSuccessMessage()));
    }

    @PutMapping(EndpointURI.LICENSE)
    public ResponseEntity<Object> UpdateLicense(@RequestBody LicenseRequest licenseRequest) {
        Licenses existingLicense = licenseService.findById(licenseRequest.getId());

        if (licenseRequest.getName() == null) {
            licenseRequest.setName(existingLicense.getName());
        }

        if (licenseRequest.getDuration() == null) {
            licenseRequest.setDuration(existingLicense.getDuration());
        }

        if (licenseRequest.getNoOfProjects() == null) {
            licenseRequest.setNoOfProjects(existingLicense.getNoOfProjects());
        }

        if (licenseRequest.getNoOfUsers() == null) {
            licenseRequest.setNoOfUsers(existingLicense.getNoOfUsers());
        }
        if (licenseRequest.getPrice() == null) {
            licenseRequest.setPrice(existingLicense.getPrice());
        }
        if (!licenseService.existsById(licenseRequest.getId()))
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getLicenseNotExistCode(), statusCodeBundle.getLicensePackageNotExistMessage()));

        if (licenseService.isUpdateNameExists(licenseRequest.getName(), licenseRequest.getId()))
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getLicenseAlreadyExistCode(), statusCodeBundle.getLicenseNameAlreadyExistMessage()));

        if (licenseService.isUpdateByDurationAndNoOfProjectsAndNoOfUsers(licenseRequest.getDuration(), licenseRequest.getNoOfProjects(), licenseRequest.getNoOfUsers(), licenseRequest.getId()))
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getLicenseAlreadyExistCode(), statusCodeBundle.getLicensePackageAlreadyExistMessage()));

        licenseService.createLicense(licenseRequest);
        return ResponseEntity.ok(new BaseResponse(RequestStatus.SUCCESS.getStatus(), statusCodeBundle.getCommonSuccessCode(), statusCodeBundle.getLicenseSuccessfullyUpdatedMessage()));
    }

    @GetMapping(value = EndpointURI.LICENSE_BY_ID)
    public ResponseEntity<Object> getLicenseById(@PathVariable Long id){
        if(!licenseService.existsById(id)){
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(),statusCodeBundle.getLicenseNotExistCode(),
                    statusCodeBundle.getLicensePackageNotExistMessage()));
        }
        return ResponseEntity.ok(new ContentResponse<>(Constants.LICENSE, licenseService.getLicenseById(id), RequestStatus.SUCCESS.getStatus(), statusCodeBundle.getCommonSuccessCode(), statusCodeBundle.getLicenseGetByIdSuccessMessage()));
    }

    @DeleteMapping(EndpointURI.LICENSE_BY_ID)
    public ResponseEntity<Object> DeleteLicense(@PathVariable Long id) {
        if (!licenseService.existsById(id)) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getLicenseNotExistCode(), statusCodeBundle.getLicensePackageNotExistMessage()));
        }
        if (companyUserService.existsByLicenseId(id)) {
            return ResponseEntity.ok(new BaseResponse(RequestStatus.FAILURE.getStatus(), statusCodeBundle.getLicenseDeleteDependentCode(), statusCodeBundle.getLicenseDeleteDependentMessage()));
        }
        licenseService.deleteLicenseById(id);
        return ResponseEntity.ok(new BaseResponse(RequestStatus.SUCCESS.getStatus(), statusCodeBundle.getCommonSuccessCode(), statusCodeBundle.getLicenseSuccessfullyDeletedMessage()));
    }
}
