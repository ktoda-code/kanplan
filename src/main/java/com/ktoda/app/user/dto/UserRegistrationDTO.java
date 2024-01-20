package com.ktoda.app.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserRegistrationDTO(
        @NotNull
        @NotBlank(message = "Email is mandatory")
        @Email(message = "Invalid email address")
        String email,
        @NotNull
        @NotBlank(message = "Password is mandatory")
        String password
) {
}
