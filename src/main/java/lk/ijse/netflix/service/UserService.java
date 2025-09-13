package lk.ijse.netflix.service;

import com.clerk.backend_api.models.components.User;
import com.clerk.backend_api.models.errors.ClerkErrors;
import lk.ijse.netflix.dto.PagedUsersResponse;

public interface UserService {

    User updateUserRole(String role, String userId, String plan) throws ClerkErrors, Exception;

    User updateUserRole(String userId, String role) throws ClerkErrors, Exception;

    boolean deleteUser(String userId);

    PagedUsersResponse getUsersList(int limit, int offset) throws Exception;
}
