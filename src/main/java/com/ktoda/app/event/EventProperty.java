package com.ktoda.app.event;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "event_properties")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventProperty implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean allDay;

    public EventProperty(LocalDate date, LocalTime startTime, LocalTime endTime, boolean allDay) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.allDay = allDay;
    }
}
