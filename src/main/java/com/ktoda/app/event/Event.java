package com.ktoda.app.event;

import com.ktoda.app.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.awt.*;
import java.io.Serializable;


@Entity
@Table(name = "events")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Event implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private String title;
    private String description;
    private String color; // Hex code
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "event_property_id")
    private EventProperty property;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "event_notification_id")
    private EventNotification notification;

    public Event(String title,
                 String description,
                 String color,
                 EventProperty property,
                 EventNotification notification) {
        this.title = title;
        this.description = description;
        this.color = color;
        this.property = property;
        this.notification = notification;
    }
}
