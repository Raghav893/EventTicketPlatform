package com.raghav.eventticketplatform.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Events {
    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long event_id;
    @Column(nullable = false)
    String title;
    @Column(nullable = false)
    String description;
    @Column(nullable = false)
    String location;
    @Column(nullable = false)
    LocalDateTime dateTime;
    @Column(nullable = false)
    String OrganizerUsername;
    @Column(nullable = false)
    LocalDateTime CreatedAt;
    @Column(nullable = false)
    LocalDateTime UpdatedAt;
    @Enumerated(EnumType.STRING)
    EventStatus status;

}
