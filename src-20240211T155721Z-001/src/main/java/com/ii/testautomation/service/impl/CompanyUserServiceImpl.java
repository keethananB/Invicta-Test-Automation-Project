package com.ii.testautomation.service.impl;

import com.ii.testautomation.dto.request.CompanyUserRequest;
import com.ii.testautomation.dto.response.CompanyUserResponse;
import com.ii.testautomation.dto.search.CompanyUserSearch;
import com.ii.testautomation.entities.CompanyUser;
import com.ii.testautomation.entities.Designation;
import com.ii.testautomation.entities.Licenses;
import com.ii.testautomation.entities.QCompanyUser;
import com.ii.testautomation.entities.Users;
import com.ii.testautomation.enums.LoginStatus;
import com.ii.testautomation.repositories.CompanyUserRepository;
import com.ii.testautomation.repositories.DesignationRepository;
import com.ii.testautomation.repositories.LicensesRepository;
import com.ii.testautomation.repositories.UserRepository;
import com.ii.testautomation.response.common.PaginatedContentResponse;
import com.ii.testautomation.service.CompanyUserService;
import com.ii.testautomation.service.DesignationService;
import com.ii.testautomation.service.UserService;
import com.ii.testautomation.utils.Constants;
import com.ii.testautomation.utils.Utils;
import com.querydsl.core.BooleanBuilder;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class CompanyUserServiceImpl implements CompanyUserService {
    @Autowired
    private CompanyUserRepository companyUserRepository;
    @Autowired
    private LicensesRepository licensesRepository;
    @Autowired
    private DesignationRepository designationRepository;
    @Autowired
    private DesignationService designationService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean existsByCompanyUserId(Long id) {
        return companyUserRepository.existsById(id);
    }

    @Override
    public boolean isUpdateCompanyUserNameExists(String name, Long licensesId, Long id) {
        return companyUserRepository.existsByCompanyNameIgnoreCaseAndLicensesIdAndIdNot(name, licensesId, id);
    }

    @Override
    public boolean isUpdateEmailExists(String email, Long id) {
        return companyUserRepository.existsByEmailIgnoreCaseAndIdNot(email, id);
    }

    @Override
    public boolean isUpdateCompanyUserContactNumberExists(String contactNumber, Long id) {
        return companyUserRepository.existsByContactNumberIgnoreCaseAndIdNot(contactNumber, id);

    }

    @Override
    public List<CompanyUserResponse> getAllCompanyUserWithMultiSearch(Pageable pageable, PaginatedContentResponse.Pagination pagination, CompanyUserSearch companyUserSearch) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (Utils.isNotNullAndEmpty(companyUserSearch.getCompanyName())) {
            booleanBuilder.and(QCompanyUser.companyUser.companyName.containsIgnoreCase(companyUserSearch.getCompanyName()));
        }
        if (Utils.isNotNullAndEmpty(companyUserSearch.getContactNumber())) {
            booleanBuilder.and(QCompanyUser.companyUser.contactNumber.containsIgnoreCase(companyUserSearch.getContactNumber()));
        }
        if (Utils.isNotNullAndEmpty(companyUserSearch.getEmail())) {
            booleanBuilder.and(QCompanyUser.companyUser.email.containsIgnoreCase(companyUserSearch.getEmail()));
        }
        if (Utils.isNotNullAndEmpty(companyUserSearch.getLicenseName())) {
            booleanBuilder.and(QCompanyUser.companyUser.licenses.name.containsIgnoreCase(companyUserSearch.getLicenseName()));
        }
        if (companyUserSearch.getStartDate() != null) {
            if (Utils.isNotNullAndEmpty(companyUserSearch.getStartDate().toString())) {
                booleanBuilder.and(QCompanyUser.companyUser.startDate.eq(companyUserSearch.getStartDate()));
            }
        }
        if (companyUserSearch.getEndDate() != null) {
            if (Utils.isNotNullAndEmpty(companyUserSearch.getEndDate().toString())) {
                booleanBuilder.and(QCompanyUser.companyUser.endDate.eq(companyUserSearch.getEndDate()));
            }
        }
        if (companyUserSearch.getLicenseDuration() != null) {
            if (Utils.isNotNullAndEmpty(companyUserSearch.getLicenseDuration().toString())) {
                booleanBuilder.and(QCompanyUser.companyUser.licenses.duration.eq(companyUserSearch.getLicenseDuration()));
            }
        }
        if (companyUserSearch.getNoOfUsers() != null) {
            if (Utils.isNotNullAndEmpty(companyUserSearch.getNoOfUsers().toString())) {
                booleanBuilder.and(QCompanyUser.companyUser.licenses.noOfUsers.eq(companyUserSearch.getNoOfUsers()));
            }
        }
        if (companyUserSearch.getNoOfProjects() != null) {
            if (Utils.isNotNullAndEmpty(companyUserSearch.getNoOfProjects().toString())) {
                booleanBuilder.and(QCompanyUser.companyUser.licenses.noOfProjects.eq(companyUserSearch.getNoOfProjects()));
            }
        }
        if (companyUserSearch.getPrice() != null) {
            if ((Utils.isNotNullAndEmpty(companyUserSearch.getPrice().toString()))) {
                booleanBuilder.and(QCompanyUser.companyUser.licenses.price.eq(companyUserSearch.getPrice()));
            }
        }

        List<CompanyUserResponse> companyUserResponseList = new ArrayList<>();
        Page<CompanyUser> companyUserPage = companyUserRepository.findAll(booleanBuilder, pageable);
        List<CompanyUser> companyUserList = companyUserPage.getContent();
        pagination.setTotalPages(companyUserPage.getTotalPages());
        pagination.setTotalRecords(companyUserPage.getTotalElements());
        for (CompanyUser companyUser : companyUserList) {
            Users admin = userRepository.findFirstByCompanyUserIdAndDesignationName(companyUser.getId(), Constants.COMPANY_ADMIN);
            if (admin == null) continue;
            CompanyUserResponse companyUserResponse = new CompanyUserResponse();
            BeanUtils.copyProperties(companyUser, companyUserResponse);
            companyUserResponse.setFirstName(admin.getFirstName());
            companyUserResponse.setLastName(admin.getLastName());
            companyUserResponse.setNoOfUsers(companyUser.getLicenses().getNoOfUsers());
            companyUserResponse.setLicenseDuration(companyUser.getLicenses().getDuration());
            companyUserResponse.setLicenseName(companyUser.getLicenses().getName());
            companyUserResponse.setLicenseId(companyUser.getLicenses().getId());
            companyUserResponse.setNoOfProjects(companyUser.getLicenses().getNoOfProjects());
            companyUserResponse.setPrice(companyUser.getLicenses().getPrice());
            companyUserResponseList.add(companyUserResponse);
        }
        return companyUserResponseList;
    }

    @Override
    public CompanyUser findByCompanyUserId(Long companyUserId) {
        return companyUserRepository.findById(companyUserId).get();
    }

    @Override
    public boolean existsByStatusAndEmail(boolean b, String email) {
        return companyUserRepository.existsByStatusAndEmailIgnoreCase(b, email);
    }

    public boolean existsByLicenseId(Long id) {
        return companyUserRepository.existsByLicensesId(id);
    }

    @Override
    public boolean isExistCompanyUserName(String companyName) {
        return companyUserRepository.existsByCompanyNameIgnoreCase(companyName);
    }

    @Override
    public boolean isExistByCompanyUserEmail(String email) {
        return companyUserRepository.existsByEmailIgnoreCase(email);
    }

    @Override
    public boolean isExistByCompanyUserContactNumber(String contactNumber) {
        return companyUserRepository.existsByContactNumber(contactNumber);
    }

    @Override
    public void saveCompanyUser(CompanyUserRequest companyUserRequest) {
        CompanyUser companyUser = new CompanyUser();
        Licenses licenses = licensesRepository.findById(companyUserRequest.getLicenses_id()).orElse(null);
        licenses.setId(companyUserRequest.getLicenses_id());
        companyUser.setLicenses(licenses);
        BeanUtils.copyProperties(companyUserRequest, companyUser);
        LocalDate startDate = companyUser.getStartDate();
        int durationMonths = licenses.getDuration().intValue();
        LocalDate endDate = startDate.plusMonths(durationMonths);
        companyUser.setEndDate(endDate);
        companyUserRepository.save(companyUser);
        CompanyUser companyAdmin = companyUserRepository.findByEmail(companyUserRequest.getEmail());

        Designation designation = new Designation();
        designation.setName(Constants.COMPANY_ADMIN);
        designation.setCompanyUser(companyAdmin);
        designationRepository.save(designation);
        Designation adminDesignation = designationRepository.findFirstByNameAndCompanyUserId(Constants.COMPANY_ADMIN, companyAdmin.getId());

        Users user = new Users();
        user.setFirstName(companyUserRequest.getFirstName());
        user.setLastName(companyUserRequest.getLastName());
        user.setEmail(companyAdmin.getEmail());
        user.setContactNumber(companyAdmin.getContactNumber());
        user.setCompanyUser(companyAdmin);
        user.setDesignation(adminDesignation);
        user.setStatus(LoginStatus.NEW.getStatus());
        userRepository.save(user);
    }

    @Override
    public void updateCompanyUser(CompanyUserRequest companyUserRequest) {
        CompanyUser companyUser = companyUserRepository.findById(companyUserRequest.getId()).get();
        Users user = userRepository.findFirstByCompanyUserIdAndDesignationName(companyUserRequest.getId(), Constants.COMPANY_ADMIN);
        if (!(companyUserRequest.getLicenses_id() == null)) {
            Licenses license = licensesRepository.findById(companyUserRequest.getLicenses_id()).get();
            companyUser.setLicenses(license);
            LocalDate startDate = companyUser.getStartDate();
            int durationMonths = license.getDuration().intValue();
            LocalDate endDate = startDate.plusMonths(durationMonths);
            companyUser.setEndDate(endDate);
        }
        if (!(companyUserRequest.getContactNumber() == null))
            companyUser.setContactNumber(companyUserRequest.getContactNumber());
        if (!(companyUserRequest.getCompanyName() == null))
            companyUser.setCompanyName(companyUserRequest.getCompanyName());
        if (!(companyUserRequest.getStartDate() == null)) companyUser.setStartDate(companyUserRequest.getStartDate());
        if (!(companyUserRequest.getFirstName() == null)) user.setFirstName(companyUserRequest.getFirstName());
        if (!(companyUserRequest.getLastName() == null)) user.setLastName(companyUserRequest.getLastName());
        if (!(companyUserRequest.getStatus() == true)) {
            user.setStatus(LoginStatus.ACTIVE.getStatus());
            user.setWrongCount(5);
        }
        companyUser.setStatus(companyUserRequest.getStatus());
        companyUserRepository.save(companyUser);
        user.setEmail(companyUser.getEmail());
        userRepository.save(user);
    }

    @Override
    public boolean existsById(Long id) {
        return companyUserRepository.existsById(id);
    }

    @Override
    public void deleteById(Long id) {
        companyUserRepository.deleteById(id);
    }

    @Override
    public CompanyUserResponse getCompanyUserById(Long id) {
        CompanyUser companyUser = companyUserRepository.findById(id).get();
        CompanyUserResponse companyUserResponse = new CompanyUserResponse();
        companyUserResponse.setLicenseId(companyUser.getLicenses().getId());
        companyUserResponse.setLicenseName(companyUser.getLicenses().getName());
        companyUserResponse.setLicenseDuration(companyUser.getLicenses().getDuration());
        companyUserResponse.setPrice(companyUser.getLicenses().getPrice());
        companyUserResponse.setNoOfUsers(companyUser.getLicenses().getNoOfUsers());
        companyUserResponse.setNoOfProjects(companyUser.getLicenses().getNoOfProjects());
        BeanUtils.copyProperties(companyUser, companyUserResponse);
        return companyUserResponse;
    }

 @Scheduled(cron = "0/1 * * * * ?")
    public void deactivateExpiredCompanyUsers() {
        LocalDate currentDate = LocalDate.now();
        List<CompanyUser> expiredCompanyUsers = companyUserRepository.findByEndDateLessThanEqualAndStatusTrue(currentDate);
        for (CompanyUser companyUser : expiredCompanyUsers) {
            companyUser.setStatus(false);
        }
        companyUserRepository.saveAll(expiredCompanyUsers);
    }

}