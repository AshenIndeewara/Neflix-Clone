package lk.ijse.netflix.contoller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v1/auth")
@RequiredArgsConstructor
@CrossOrigin
public class AuthController {
//    private final AuthService authService;

//    @PostMapping("/register")
//    public ResponseEntity<ApiResponse> registerUser(
//            @RequestBody RegisterDTO registerDTO) {
//        System.out.println("Registering user (Controller): " + registerDTO.getEmail());
//        return ResponseEntity.ok(new ApiResponse(
//                200,
//                "OK",
//                authService.register(registerDTO)));
//    }
//    @PostMapping("/login")
//    public ResponseEntity<ApiResponse> login(
//            @RequestBody AuthDTO authDTO) {
//        return ResponseEntity.ok(new ApiResponse(
//                200,
//                "OK",
//                authService.authenticate(authDTO)));
//    }
}
