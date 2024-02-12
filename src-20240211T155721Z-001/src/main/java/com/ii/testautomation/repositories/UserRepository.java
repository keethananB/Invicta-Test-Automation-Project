package com.ii.testautomation.repositories;

import com.ii.testautomation.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface UserRepository extends JpaRepository<Users, Long>, QuerydslPredicateExecutor<Users> {

    boolean existsByEmailIgnoreCase(String email);

    List<Users> findByCompanyUserId(Long companyId);

    boolean existsByDesignationId(Long designationId);

    boolean existsByCompanyUserId(Long id);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    boolean existsByContactNumberIgnoreCaseAndIdNot(String contactNumber, Long id);

    boolean existsByContactNumberIgnoreCase(String contactNo);

    boolean existsByStatusAndEmailIgnoreCase(String status, String email);

    Users findByEmailIgnoreCase(String email);

    boolean existsByStatus(String status);

    List<Users> findAllByCompanyUser_IdAndDesignation_Id(Long companyUserId, Long designationId);

    Users findFirstByCompanyUserIdAndDesignationName(Long id, String companyAdmin);

    List<Users> findByCompanyUserIdAndStatus(Long companyUserId, String status);
}