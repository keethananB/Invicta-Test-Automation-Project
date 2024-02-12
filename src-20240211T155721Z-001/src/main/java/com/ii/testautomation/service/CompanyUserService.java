package com.ii.testautomation.service;

import com.ii.testautomation.dto.request.CompanyUserRequest;
import com.ii.testautomation.dto.response.CompanyUserResponse;
import com.ii.testautomation.dto.search.CompanyUserSearch;
import com.ii.testautomation.entities.CompanyUser;
import com.ii.testautomation.response.common.PaginatedContentResponse;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface CompanyUserService {

    boolean existsByCompanyUserId(Long id);

    boolean isUpdateCompanyUserNameExists(String name, Long licenseId,Long id);

    boolean isUpdateEmailExists(String email,Long id);

    boolean isUpdateCompanyUserContactNumberExists(String contactNumber, Long id);

    List<CompanyUserResponse> getAllCompanyUserWithMultiSearch(Pageable pageable, PaginatedContentResponse.Pagination pagination, CompanyUserSearch companyUserSearch);

    boolean existsByLicenseId(Long id);

    boolean isExistCompanyUserName(String companyName);

    boolean isExistByCompanyUserEmail(String email);

    boolean isExistByCompanyUserContactNumber(String contactNumber);

    void saveCompanyUser(CompanyUserRequest companyUserRequest);

    boolean existsById(Long id);

    void deleteById(Long id);

    CompanyUserResponse getCompanyUserById(Long id);

    void updateCompanyUser(CompanyUserRequest companyUserRequest);

    CompanyUser findByCompanyUserId(Long companyUserId);

    boolean existsByStatusAndEmail(boolean b, String email);
}

