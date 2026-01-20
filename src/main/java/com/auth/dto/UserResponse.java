package com.auth.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String gender;
    private String email;
    private String countryCode;
    private String phone;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
