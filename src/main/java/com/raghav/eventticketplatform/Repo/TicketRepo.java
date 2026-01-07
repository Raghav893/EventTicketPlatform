package com.raghav.eventticketplatform.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepo extends JpaRepository<com.raghav.eventticketplatform.Entity.Ticket, Long> {
}
