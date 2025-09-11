package lk.ijse.netflix.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserData {
    private String userId;
    private String fullName;
    private String email;
    private String userImage;

    public UserData(String id, String s, Optional<String> s1, Optional<String> s2, String role) {
    }
}
