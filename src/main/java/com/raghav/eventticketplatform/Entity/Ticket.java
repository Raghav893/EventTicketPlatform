package com.raghav.eventticketplatform.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private String ticketCode;


    @ManyToOne
    @JoinColumn(name = "ticket_type_id")
    private TicketType ticketType;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "event_id", nullable = false)
    private Events event;

    @Column(nullable = false)
    private String attendeeUsername;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketStatus ticketStatus;

    @Column(nullable = false)
    private LocalDateTime purchasedAt;

    @JsonIgnore
    @OneToOne(mappedBy = "ticket")
    private TicketValidation ticketValidation;
}
