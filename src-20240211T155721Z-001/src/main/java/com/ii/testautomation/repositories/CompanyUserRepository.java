package com.ii.testautomation.repositories;

import com.ii.testautomation.entities.CompanyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.time.LocalDate;
import java.util.List;

public interface CompanyUserRepository extends JpaRepository<CompanyUser, Long>, QuerydslPredicateExecutor<CompanyUser> {

    boolean existsByCompanyNameIgnoreCase(String name);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByContactNumber(String contactNumber);

    boolean existsByCompanyNameIgnoreCaseAndLicensesIdAndIdNot(String companyName, Long licensesId, Long id);

    boolean existsByEmailIgnoreCaseAndLicensesIdAndIdNot(String email, Long licensesId, Long id);

    boolean existsByContactNumberIgnoreCaseAndIdNot(String contactNumber, Long id);

    boolean existsByLicensesId(Long id);

    CompanyUser findByEmail(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    List<CompanyUser> findByEndDateLessThanEqualAndStatusTrue(LocalDate currentDate);

  boolean existsByStatusAndEmailIgnoreCase(boolean b, String email);
}
