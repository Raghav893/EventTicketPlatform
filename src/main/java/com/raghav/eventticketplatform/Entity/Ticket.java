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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ticketId;

    @Column(nullable = false, unique = true)
    private Long ticketCode;

    @Column(nullable = false)
    private Long ticketTypeId;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Events event;

    @Column(nullable = false)
    private String attendeeUsername;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketStatus ticketStatus;

    @Column(nullable = false)
    private LocalDateTime purchasedAt;

    @OneToOne(mappedBy = "ticket", cascade = CascadeType.ALL)
    private TicketValidation ticketValidation;
}
