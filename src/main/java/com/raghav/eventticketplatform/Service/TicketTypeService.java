package com.raghav.eventticketplatform.Service;

import com.raghav.eventticketplatform.Entity.Events;
import com.raghav.eventticketplatform.Entity.TicketType;
import com.raghav.eventticketplatform.Repo.TicketTypeRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TicketTypeService {

    private final TicketTypeRepo ticketTypeRepo;


    public TicketType create(TicketType ticketType) {
        ticketType.setCreatedAt(LocalDateTime.now());
        return ticketTypeRepo.save(ticketType);
    }

    public boolean deleteById(Events event, Long ticketTypeId) {

        Optional<TicketType> ticketTypeOpt = ticketTypeRepo.findById(ticketTypeId);
        if (ticketTypeOpt.isEmpty()) {
            return false;
        }
        TicketType ticketType = ticketTypeOpt.get();
        if (!ticketType.getEvent().getEventId().equals(event.getEventId())) {
            return false;
        }
        if (ticketType.getRemaining() < ticketType.getTicketQuantity()) {
            throw new IllegalStateException("Tickets already sold");
        }
        ticketTypeRepo.delete(ticketType);
        return true;
    }
    public Optional<TicketType> update(Events event, Long ticketTypeId, TicketType updatedTicketType) {

        Optional<TicketType> ticketTypeOpt = ticketTypeRepo.findById(ticketTypeId);
        if (ticketTypeOpt.isEmpty()) {
            return Optional.empty();
        }

        TicketType ticketType = ticketTypeOpt.get();

        if (!ticketType.getEvent().getEventId().equals(event.getEventId())) {
            return Optional.empty();
        }

        if (ticketType.getRemaining() < ticketType.getTicketQuantity()) {
            return Optional.empty();
        }

        ticketType.setName(updatedTicketType.getName());
        ticketType.setTicketQuantity(updatedTicketType.getTicketQuantity());
        ticketType.setRemaining(updatedTicketType.getTicketQuantity());

        return Optional.of(ticketTypeRepo.save(ticketType));
    }


    public TicketType getTicketTypeById(Long ticketTypeId) {
        return ticketTypeRepo.findTicketTypeByTicketTypeId(ticketTypeId);
    }
}
