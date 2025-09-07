package lk.ijse.netflix.service;

import lk.ijse.netflix.dto.AuthDTO;
import lk.ijse.netflix.dto.AuthResponseDTO;
import lk.ijse.netflix.dto.RegisterDTO;
import lk.ijse.netflix.entity.Role;
import lk.ijse.netflix.entity.User;
import lk.ijse.netflix.repo.UserRepository;
import lk.ijse.netflix.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthResponseDTO authenticate(AuthDTO authDTO) {
        User user=
                userRepository.findByEmail(authDTO.getEmail())
                        .orElseThrow(
                                ()->new UsernameNotFoundException
                                        ("Username not found"));
        if (!passwordEncoder.matches(
                authDTO.getPassword(),
                user.getPassword())) {
            throw new BadCredentialsException("Incorrect password");
        }
        String token=jwtUtil.generateToken(authDTO.getEmail());
        return  new AuthResponseDTO(token);
    }
    public String register(RegisterDTO registerDTO) {
        System.out.println("Registering user: " + registerDTO.getEmail());
        System.out.println("Role: " + registerDTO.getRole());
        System.out.println("Password: " + registerDTO.getPassword());
        if (registerDTO.getRole() == null){
            registerDTO.setRole(Role.NON_PAIDUSER);
        }
        if(userRepository.findByEmail(
                registerDTO.getEmail()).isPresent()){
            throw new RuntimeException("email already exists");
        }
        User user = User.builder()
                .fullName(registerDTO.getFullName())
                .password(passwordEncoder.encode(registerDTO.getPassword()))
                .email(registerDTO.getEmail())
                .role(registerDTO.getRole())
                .build();
        userRepository.save(user);
        return  "User Registration Success";
    }

    public Page<User> getUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findAll(pageable);
    }

    public String updateUserRole(UUID id, String role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        try {
            Role newRole = Role.valueOf(role);
            user.setRole(newRole);
            userRepository.save(user);
            return "User role updated successfully";
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid role: " + role);
        }
    }

    public String deleteByUserId(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
        return "User deleted successfully";
    }
}

