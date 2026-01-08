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
public class TicketValidation {

    @Id
    private String validationId;

    @OneToOne
    @JoinColumn(name = "ticket_id", nullable = false, unique = true)
    private Ticket ticket;

    @Column(nullable = false)
    private String staffUsername;

    @Column(nullable = false)
    private LocalDateTime ValidatedAt;



    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Method entryMethod;
}
