package com.raghav.eventticketplatform.Repo;

import com.raghav.eventticketplatform.Entity.Events;
import com.raghav.eventticketplatform.Entity.TicketValidation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketValidationRepository extends JpaRepository<TicketValidation, Long> {
    @Query("""
        SELECT tv
        FROM TicketValidation tv
        WHERE tv.ticket.event = :event
    """)
    List<TicketValidation> findByEvent(@Param("event") Events event);
}
