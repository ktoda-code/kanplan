package com.ktoda.app.event.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EventCreateDTO(
        @NotNull
        @NotBlank
        String title,
        String description,
        String color,
        EventPropertyDTO property,
        EventNotificationDTO notification
) {
}
