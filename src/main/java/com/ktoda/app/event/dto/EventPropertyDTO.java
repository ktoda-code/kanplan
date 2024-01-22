package com.ktoda.app.event.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record EventPropertyDTO(
        long id,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        boolean allDay
) {
}
