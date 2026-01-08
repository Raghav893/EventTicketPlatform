package com.raghav.eventticketplatform.Service;

import com.raghav.eventticketplatform.Entity.Events;
import com.raghav.eventticketplatform.Entity.TicketValidation;
import com.raghav.eventticketplatform.Repo.TicketValidationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketValidationService {
    private final TicketValidationRepository ticketValidationRepository;
    public TicketValidation createValidTicket(TicketValidation ticketValidation) {
        return ticketValidationRepository.save(ticketValidation);
    }


    public List<TicketValidation> getValidatedTicketsByEvent(Events event) {
        return ticketValidationRepository.findByEvent(event);
    }
}
