package com.raghav.eventticketplatform.Repo;

import com.raghav.eventticketplatform.Entity.Ticket;
import com.raghav.eventticketplatform.Entity.TicketStatus;
import com.raghav.eventticketplatform.Entity.TicketValidation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepo extends JpaRepository<com.raghav.eventticketplatform.Entity.Ticket, Long> {
    List<Ticket> findByAttendeeUsername(String attendeeUsername);

    Ticket findTicketByTicketCode(String ticketCode);

    List<TicketValidation> getTicketByTicketStatus(TicketStatus ticketStatus);
}
