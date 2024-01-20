package com.ktoda.app.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserUpdateDTO(
        @NotNull
        long id,
        @NotNull
        @NotBlank
        @Email
        String email,
        @NotNull
        @NotBlank
        String password
) {
}
