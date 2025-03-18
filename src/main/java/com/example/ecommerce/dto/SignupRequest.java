package com.example.ecommerce.dto;

import java.util.Set;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class SignupRequest {

    @NotBlank
    @Size(min = 3, max = 7)
    private String username;

    @NotBlank
    @Size(max = 30)
    @Email
    private String email;

    private Set<String> roles;

    @NotBlank
    @Size(min = 8, max = 40)
    private String password;

}
