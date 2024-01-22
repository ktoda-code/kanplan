package com.ktoda.app.user.dto;

import com.ktoda.app.event.Event;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;

public record UserDTO(
        @NotNull
        @NotBlank
        Long id,
        @NotNull
        @NotBlank
        String email,
        @NotNull
        @NotBlank
        Instant createdOn,
        List<Event> events
) {
}
