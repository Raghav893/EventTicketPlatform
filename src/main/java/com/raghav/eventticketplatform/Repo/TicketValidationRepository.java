package com.raghav.eventticketplatform.Repo;

import com.raghav.eventticketplatform.Entity.TicketValidation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketValidationRepository extends JpaRepository<TicketValidation, Long> {
}
