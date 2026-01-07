package com.raghav.eventticketplatform.Service;

import com.raghav.eventticketplatform.Entity.Ticket;
import com.raghav.eventticketplatform.Repo.TicketRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TicketPurchaseService {
    private final TicketRepo ticketRepo;

    public Ticket createTiceket(Ticket ticket) {
        return ticketRepo.save(ticket);
    }
}
