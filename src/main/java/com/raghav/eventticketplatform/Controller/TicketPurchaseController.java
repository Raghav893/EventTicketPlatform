package com.raghav.eventticketplatform.Controller;

import com.raghav.eventticketplatform.Entity.Events;
import com.raghav.eventticketplatform.Entity.Ticket;
import com.raghav.eventticketplatform.Entity.TicketStatus;
import com.raghav.eventticketplatform.Entity.TicketType;
import com.raghav.eventticketplatform.Service.EventServices;
import com.raghav.eventticketplatform.Service.RoleCheckService;
import com.raghav.eventticketplatform.Service.TicketPurchaseService;
import com.raghav.eventticketplatform.Service.TicketTypeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class TicketPurchaseController {

    private final EventServices eventServices;
    private final TicketTypeService ticketTypeService;
    private final TicketPurchaseService ticketPurchaseService;
    private final RoleCheckService roleCheckService;

    @Transactional
    @PostMapping("/events/{eventId}/ticket-types/{ticketTypeId}/purchase")
    public ResponseEntity<Ticket> purchaseTicket(
            @PathVariable Long eventId,
            @PathVariable Long ticketTypeId) {

        boolean isAttendee = roleCheckService.AttendeeCheck();
        if (!isAttendee) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Optional<Events> eventOpt = eventServices.getEventById(eventId);
        if (eventOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Events event = eventOpt.get();

        Optional<TicketType> ticketTypeOpt = Optional.ofNullable(ticketTypeService.getTicketTypeById(ticketTypeId));
        if (ticketTypeOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        TicketType ticketType = ticketTypeOpt.get();

        if (!ticketType.getEvent().getEventId().equals(event.getEventId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (ticketType.getRemaining() <= 0) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        ticketType.setRemaining(ticketType.getRemaining() - 1);
        ticketTypeService.update(event,ticketTypeId,ticketType);

        Ticket ticket = new Ticket();
        ticket.setEvent(event);
        ticket.setTicketType(ticketType);
        ticket.setAttendeeUsername(username);
        ticket.setTicketStatus(TicketStatus.UNUSED);
        ticket.setPurchasedAt(LocalDateTime.now());
        ticket.setTicketCode(System.currentTimeMillis());

        Ticket saved = ticketPurchaseService.createTiceket(ticket);

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}
