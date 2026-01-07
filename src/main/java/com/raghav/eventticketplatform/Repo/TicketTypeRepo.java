package com.raghav.eventticketplatform.Repo;

import com.raghav.eventticketplatform.Entity.TicketType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketTypeRepo extends JpaRepository<TicketType,Long> {
    TicketType findTicketTypeByTicketTypeId(Long ticketTypeId);
}
