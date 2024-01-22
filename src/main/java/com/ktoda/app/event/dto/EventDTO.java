package com.ktoda.app.event.dto;

public record EventDTO(
        long id,
        String title,
        String description,
        String color,
        EventPropertyDTO property,
        EventNotificationDTO notification
) {
}
