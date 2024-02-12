package com.ii.testautomation.service.impl;

import com.ii.testautomation.dto.request.LicenseRequest;
import com.ii.testautomation.dto.response.LicenseResponse;
import com.ii.testautomation.dto.search.LicensesSearch;
import com.ii.testautomation.entities.Licenses;
import com.ii.testautomation.entities.QLicenses;
import com.ii.testautomation.repositories.LicensesRepository;
import com.ii.testautomation.response.common.PaginatedContentResponse;
import com.ii.testautomation.service.LicenseService;
import com.ii.testautomation.utils.Utils;
import com.querydsl.core.BooleanBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class LicenseServiceImpl implements LicenseService {
    @Autowired
    private LicensesRepository licenseRepository;

    @Override
    public List<LicenseResponse> multiSearchLicensesWithPagination(Pageable pageable, PaginatedContentResponse.Pagination pagination, LicensesSearch licensesSearch) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (Utils.isNotNullAndEmpty(licensesSearch.getName())) {
            booleanBuilder.and(QLicenses.licenses.name.containsIgnoreCase(licensesSearch.getName()));
        }
        List<LicenseResponse> licenseResponseList = new ArrayList<>();
        Page<Licenses> licensesPage = licenseRepository.findAll(booleanBuilder, pageable);
        pagination.setPageSize(licensesPage.getTotalPages());
        pagination.setTotalRecords(licensesPage.getTotalElements());

        for (Licenses licenses : licensesPage) {
            if (licenses.getId()==1L) continue;
            LicenseResponse licenseResponse = new LicenseResponse();
            BeanUtils.copyProperties(licenses, licenseResponse);
            licenseResponseList.add(licenseResponse);
        }
        return licenseResponseList;
    }

    @Override
    public void createLicense(LicenseRequest licenseRequest) {
        Licenses licenses = new Licenses();
        BeanUtils.copyProperties(licenseRequest, licenses);
        licenseRepository.save(licenses);
    }

    @Override
    public boolean existsByName(String name) {
        return licenseRepository.existsByNameIgnoreCase(name);
    }

    @Override
    public boolean existsByDurationAndNoOfProjectsAndNoOfUsers(Long duration, Long no_of_projects, Long no_of_users) {
        return licenseRepository.existsByDurationAndNoOfProjectsAndNoOfUsers(duration,no_of_projects,no_of_users);
    }

    @Override
    public boolean isUpdateByDurationAndNoOfProjectsAndNoOfUsers(Long duration, Long no_of_projects, Long no_of_users, Long id) {
        return licenseRepository.existsByDurationAndNoOfProjectsAndNoOfUsersAndIdNot(duration,no_of_projects,no_of_users,id);
    }

    @Override
    public boolean isUpdateNameExists(String name, Long id) {
        return licenseRepository.existsByNameIgnoreCaseAndIdNot(name, id);
    }

    @Override
    public void deleteLicenseById(Long id) {
        licenseRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return licenseRepository.existsById(id);
    }

    @Override
    public LicenseResponse getLicenseById(Long licenseId) {
        Licenses licenses=licenseRepository.findById(licenseId).get();
        LicenseResponse licenseResponse=new LicenseResponse();
        BeanUtils.copyProperties(licenses,licenseResponse);
        return licenseResponse;
    }

    @Override
    public boolean checkLicenseReduce(Long oldLicenseId, Long newLicenseId) {
        Licenses oldLicense = licenseRepository.findById(oldLicenseId).get();
        Long oldNoOfUsers = oldLicense.getNoOfUsers();
        Long oldNoOfProjects = oldLicense.getNoOfProjects();
        Long oldDuration = oldLicense.getDuration();

        Licenses newLicense = licenseRepository.findById(newLicenseId).get();
        Long newNoOfUsers = newLicense.getNoOfUsers();
        Long newNoOfProjects = newLicense.getNoOfProjects();
        Long newDuration = newLicense.getDuration();

        if (newNoOfUsers < oldNoOfUsers) return false;
        else if (newNoOfProjects < oldNoOfProjects) return false;
        else if (newDuration < oldDuration) return false;
        else return true;

    }

    @Override
    public Licenses findById(Long id) {
        return licenseRepository.findById(id).get();
    }
}
