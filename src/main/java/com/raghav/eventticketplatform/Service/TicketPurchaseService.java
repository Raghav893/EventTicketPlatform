package com.raghav.eventticketplatform.Service;

import com.raghav.eventticketplatform.Entity.Ticket;
import com.raghav.eventticketplatform.Entity.TicketStatus;
import com.raghav.eventticketplatform.Entity.TicketValidation;
import com.raghav.eventticketplatform.Repo.TicketRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketPurchaseService {
    private final TicketRepo ticketRepo;

    public Ticket createTiceket(Ticket ticket) {
        return ticketRepo.save(ticket);
    }
    public List<Ticket> getTicketByAttendeeUsername(String username){
        return ticketRepo.findByAttendeeUsername(username);
    }
    public Ticket getTicketByTicketCode(String TicketCode){

        return ticketRepo.findTicketByTicketCode(TicketCode);
    }

    public List<TicketValidation> getvalidTickets() {
        return ticketRepo.getTicketByTicketStatus(TicketStatus.VALIDATED);
    }
}
