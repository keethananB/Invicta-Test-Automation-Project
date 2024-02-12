package com.ii.testautomation.repositories;

import com.ii.testautomation.entities.Designation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DesignationRepository extends JpaRepository<Designation, Long> {

  List<Designation> findAllDesignationByCompanyUserId(Long id);

  boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

  Designation findFirstByNameAndCompanyUserId(String companyAdmin, Long id);

  boolean existsByNameIgnoreCaseAndCompanyUserId(String designationName, Long companyUserId);

    List<Designation> findAllDesignationByCompanyUserIdOrderByUpdatedAtDesc(Long companyUserId);

    boolean existsByCompanyUserId(Long id);
}