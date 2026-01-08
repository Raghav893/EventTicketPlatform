package com.raghav.eventticketplatform.Service;

import com.raghav.eventticketplatform.Entity.TicketValidation;
import com.raghav.eventticketplatform.Repo.TicketValidationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TicketValidationService {
    private final TicketValidationRepository ticketValidationRepository;
    public TicketValidation createValidTicket(TicketValidation ticketValidation) {
        return ticketValidationRepository.save(ticketValidation);
    }
}
