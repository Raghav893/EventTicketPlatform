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
    @Column(nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long ValidationId;
    @Column(nullable = false)
    Long ticket_id;
    @Column(nullable = false)
    String staffUsername;
    @Column(nullable = false)
    LocalDateTime CreatedAt;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    Method entryMethod;

}
