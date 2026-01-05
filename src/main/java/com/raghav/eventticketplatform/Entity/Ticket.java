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
public class Ticket {
    @Column(nullable = false,unique = true)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long ticket_id;
    @Column(nullable = false)
    Long ticket_code;
    @Column(nullable = false)
    Long ticketTypeId;
    @Column(nullable = false)
    Long event_id;
    @Column(nullable = false)
    String attendeeUsername;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    TicketStatus ticketStatus;
    @Column(nullable = false)
    LocalDateTime purchasedAt;
}
