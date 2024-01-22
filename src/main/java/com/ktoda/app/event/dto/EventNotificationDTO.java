package com.ktoda.app.event.dto;

import com.ktoda.app.event.NotificationType;

public record EventNotificationDTO(long id,
                                   int interval,
                                   NotificationType type) {
}
