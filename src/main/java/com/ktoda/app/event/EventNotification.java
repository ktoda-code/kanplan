package com.ktoda.app.event;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "event_notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventNotification implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private int interval;
    @Enumerated(EnumType.STRING)
    private NotificationType type;

    public EventNotification(int interval, NotificationType type) {
        this.interval = interval;
        this.type = type;
    }
}
