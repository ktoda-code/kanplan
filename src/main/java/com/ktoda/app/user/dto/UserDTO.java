package com.ktoda.app.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record UserDTO(
        @NotNull
        @NotBlank
        Long id,
        @NotNull
        @NotBlank
        String email,
        @NotNull
        @NotBlank
        Instant createdOn
) {
}
