package com.raghav.eventticketplatform.Controller;

import com.raghav.eventticketplatform.Entity.Events;
import com.raghav.eventticketplatform.Entity.TicketType;
import com.raghav.eventticketplatform.Service.EventServices;
import com.raghav.eventticketplatform.Service.RoleCheckService;
import com.raghav.eventticketplatform.Service.TicketTypeService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Data
public class TicketTypeController {
    private final TicketTypeService ticketTypeService;
    private final RoleCheckService roleCheckService;
    private final EventServices eventServices;


    @PostMapping("/events/{eventId}/ticket-types")
    public ResponseEntity<TicketType> createTicketTypes(@PathVariable Long eventId, @RequestBody TicketType ticketType) {

        boolean isOrganizer = roleCheckService.OrganizerCheck();
        if (!isOrganizer) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
         String username = authentication.getName();

        Optional<Events> eventOpt = eventServices.getEventById(eventId);
        if (eventOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }


        Events event = eventOpt.get();

        if (!event.getOrganizerUsername().equals(username)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        ticketType.setEvent(event);
        TicketType saved = ticketTypeService.create(ticketType);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @DeleteMapping("/events/{eventId}/ticket-types/{ticketTypeId}")
    public ResponseEntity<Void> deleteTicketType(@PathVariable Long ticketTypeId,@PathVariable Long eventId) {
        boolean isOrganizer = roleCheckService.OrganizerCheck();
        if (!isOrganizer) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Optional<Events> eventsOpt = eventServices.getEventById(eventId);
        if (eventsOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
         String username = authentication.getName();
        Events event = eventsOpt.get();

        if (!event.getOrganizerUsername().equals(username)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        boolean deleted = ticketTypeService.deleteById(event, ticketTypeId);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/events/{eventId}/ticket-types/{ticketTypeId}")
    public ResponseEntity<TicketType> updateTicketType(
            @PathVariable Long eventId,
            @PathVariable Long ticketTypeId,
            @RequestBody TicketType updatedTicketType) {

        boolean isOrganizer = roleCheckService.OrganizerCheck();
        if (!isOrganizer) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Optional<Events> eventOpt = eventServices.getEventById(eventId);
        if (eventOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Events event = eventOpt.get();

        if (!event.getOrganizerUsername().equals(username)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Optional<TicketType> updated =
                ticketTypeService.update(event, ticketTypeId, updatedTicketType);
        if (updated.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated.get());
    }


}
