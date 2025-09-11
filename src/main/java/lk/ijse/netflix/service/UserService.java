package lk.ijse.netflix.service;

import com.clerk.backend_api.Clerk;
import com.clerk.backend_api.models.components.TotalCount;
import com.clerk.backend_api.models.components.User;
import com.clerk.backend_api.models.errors.ClerkErrors;
import com.clerk.backend_api.models.operations.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lk.ijse.netflix.dto.PagedUsersResponse;
import lk.ijse.netflix.dto.UserData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.lang.Object;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final Clerk clerk;

    public User updateUserRole(String role, String userId, String plan) throws ClerkErrors, Exception {
        // Build request body with new role in public metadata
        System.out.println("Updating user " + userId + " with role: " + role + " and plan: " + plan);
        // create map
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("role", role);
        parameters.put("plan", plan);
        System.out.println(parameters);
        UpdateUserRequestBody body = UpdateUserRequestBody.builder()
                .publicMetadata(parameters)
                .build();
        System.out.println(body);
        // Call update and get the response
        UpdateUserResponse response = clerk.users().update(userId, body);
        System.out.println(response);
        // Extract the updated user from the response
        return response.user().get(); // This should now work correctly
    }
    public User updateUserRole(String userId, String role) throws ClerkErrors, Exception {
        // Build request body with new role in public metadata
        UpdateUserRequestBody body = UpdateUserRequestBody.builder()
                .publicMetadata(Map.of("role", role))
                .build();

        // Call update and get the response
        UpdateUserResponse response = clerk.users().update(userId, body);
        System.out.println(response);
        // Extract the updated user from the response
        return response.user().get(); // This should now work correctly
    }

    public boolean deleteUser(String userId){
        try {
            clerk.users().delete(userId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

//    public List<User> getUsersList(int limit, int offset) throws Exception {
//        try {
//            // Create request object
//            GetUserListRequest request = GetUserListRequest.builder()
//                    .limit((long) limit)
//                    .offset((long) offset)
//                    .build();
//
//            // Call the API
//            GetUserListResponse response = clerk.users().list(request);
//            //get all users count
//            GetUsersCountRequest req = GetUsersCountRequest.builder().build();
//            GetUsersCountResponse res = clerk.users().count(req);
//            System.out.println("Total users count: " + res.totalCount());
//            // Process the response
//            if (response.userList() != null && response.userList().get() != null) {
//                List<User> users = response.userList().get();
//                return users;
//            }
//            return List.of();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new RuntimeException("Failed to fetch users: " + e.getMessage());
//        }
//    }

    public PagedUsersResponse getUsersList(int limit, int offset) throws Exception {
        try {
            // Fetch paginated users
            GetUserListRequest request = GetUserListRequest.builder()
                    .limit((long) limit)
                    .offset((long) offset)
                    .build();
            GetUserListResponse response = clerk.users().list(request);

            List<User> users = List.of();
            if (response.userList() != null && response.userList().get() != null) {
                users = response.userList().get();
            }

            // Get total user count
            GetUsersCountRequest countRequest = GetUsersCountRequest.builder().build();
            GetUsersCountResponse countResponse = clerk.users().count(countRequest);
            Optional<TotalCount> totalCountOpt = countResponse.totalCount();

            // Calculate current page from offset and limit
            int currentPage = offset / limit;
            return new PagedUsersResponse(users, totalCountOpt, currentPage, limit);
            //return new PagedUsersResponse(users, totalCount, currentPage, limit);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to fetch users: " + e.getMessage());
        }
    }


}
