package lk.ijse.netflix.dto;

import lk.ijse.netflix.entity.Role;
import lombok.Data;

@Data
public class RegisterDTO {
    private String password;
    private String fullName;
    private String email;
    private Role role;
}

