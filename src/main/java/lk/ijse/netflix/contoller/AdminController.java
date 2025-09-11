package lk.ijse.netflix.contoller;

import lk.ijse.netflix.dto.ApiResponse;
import lk.ijse.netflix.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("v1/admin")
@RequiredArgsConstructor
@CrossOrigin
public class AdminController {
//    private final AuthService authService;

//    @PreAuthorize("hasRole('ADMIN')")
//    @GetMapping("/users")
//    public ResponseEntity<ApiResponse> getUsers(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size
//    ) {
//        return ResponseEntity.ok(new ApiResponse(200, "OK", authService.getUsers(page, size)));
//    }

//    @PreAuthorize("hasRole('ADMIN')")
//    @PutMapping("/users/{id}/role")
//    public ResponseEntity<ApiResponse> updateUserRole(
//            @PathVariable UUID id,
//            @RequestParam String role
//    ) {
//        return ResponseEntity.ok(new ApiResponse(200, "OK", authService.updateUserRole(id, role)));
//    }
//
//    @PreAuthorize("hasRole('ADMIN')")
//    @DeleteMapping("/users/{id}")
//    public ResponseEntity<ApiResponse> deleteUser(@PathVariable UUID id) {
//        return ResponseEntity.ok(new ApiResponse(200, "OK", authService.deleteByUserId(id)));
//    }


    private final UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<ApiResponse> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            return ResponseEntity.ok(new ApiResponse(200, "OK", userService.getUsersList(size, page * size)));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new ApiResponse(500, "Internal Server Error", null));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/users/{id}/role")
    public ResponseEntity<ApiResponse> updateUserRole(
            @PathVariable String id,
            @RequestParam String role
    ) {
        System.out.println(id + " : " + role);
        try {
            return ResponseEntity.ok(new ApiResponse(200, "OK", userService.updateUserRole(id, role)));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new ApiResponse(500, "Internal Server Error", null));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable String id) {
        try {
            return ResponseEntity.ok(new ApiResponse(200, "OK", userService.deleteUser(id)));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new ApiResponse(500, "Internal Server Error", null));
        }
    }
}
