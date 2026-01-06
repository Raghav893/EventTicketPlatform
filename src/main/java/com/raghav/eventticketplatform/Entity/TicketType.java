package com.raghav.eventticketplatform.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Entity
@RequiredArgsConstructor
@Data
public class TicketType {
    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long TicketTypeId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private  int ticketQuantity;

    @Column(nullable = false)
    private int remaining ;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "events_event_id")
    private Events event;

    @Column(nullable = false)
    private LocalDateTime CreatedAt;



}
