package com.ii.testautomation.service;

import com.ii.testautomation.dto.request.UserRequest;
import com.ii.testautomation.dto.response.UserResponse;
import com.ii.testautomation.dto.search.UserSearch;
import com.ii.testautomation.response.common.PaginatedContentResponse;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface UserService {
    void saveUser(UserRequest userRequest);

    boolean existsByEmail(String email);

    boolean existsByUsersId(Long usersId);

    boolean existsByContactNo(String contactNo);

    boolean existsByDesignationId(Long designationId);

    boolean existsByCompanyUserId(Long id);

    boolean existsByUserId(Long id);

    boolean existsByEmailAndIdNot(String email, Long id);

    boolean existsByContactNumberAndIdNot(String contactNumber, Long id);

    void updateUser(UserRequest userRequest);

    void deleteUserById(Long id);

    UserResponse getUserById(Long id);

    void invalidPassword(String email);

    boolean existsByEmailAndPassword(String email, String password);

    List<UserResponse> getAllUserByCompanyUserId(Pageable pageable, PaginatedContentResponse.Pagination pagination, Long userId, UserSearch userSearch);

    String generateNonExpiringToken(String email);

    void changePassword(String token, String email, String password);

    boolean existsByStatusAndEmail(String status, String email);

    Long getAllUserCountByCompanyUserId(Long companyUserId);

    List<UserResponse> getAllUsersByCompanyAdminAndDesignation(Long userId, Long designationId);

    Boolean totalCountUser(Long companyUserId);

    void sendMail(String email);
}
