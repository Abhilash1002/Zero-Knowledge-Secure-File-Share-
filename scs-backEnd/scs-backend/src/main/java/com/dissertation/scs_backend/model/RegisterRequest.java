package com.dissertation.scs_backend.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    @NotBlank
    private String userName;
    @NotBlank(message = "E-mail address must not be empty")
    @Email(message = "User must have valid email address")
    private String email;
    private String password;
}
