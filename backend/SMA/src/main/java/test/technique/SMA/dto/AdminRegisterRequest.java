package test.technique.SMA.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminRegisterRequest {
    private String username;
    private String password;
    private String confirmPassword;
}
