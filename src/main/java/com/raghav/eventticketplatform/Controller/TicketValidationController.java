package com.raghav.eventticketplatform.Controller;


import com.raghav.eventticketplatform.DTO.TicketInfoDto;
import com.raghav.eventticketplatform.Entity.*;
import com.raghav.eventticketplatform.Repo.TicketRepo;
import com.raghav.eventticketplatform.Service.EventServices;
import com.raghav.eventticketplatform.Service.RoleCheckService;
import com.raghav.eventticketplatform.Service.TicketPurchaseService;
import com.raghav.eventticketplatform.Service.TicketValidationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class TicketValidationController {

    private final TicketValidationService ticketValidationService;
    private final TicketPurchaseService ticketPurchaseService;
    private final RoleCheckService roleCheckService;
    private final TicketRepo ticketRepo;
    private final EventServices eventServices;

    @Transactional
    @PostMapping("/tickets/{ticketCode}/validate")
    public ResponseEntity<TicketValidation> validateTicket(@PathVariable String ticketCode) {

        boolean isStaff = roleCheckService.StaffCheck();
        if (!isStaff) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String staffUsername = authentication.getName();

        Ticket ticket = ticketPurchaseService.getTicketByTicketCode(ticketCode);
        if (ticket == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        if (ticket.getTicketStatus() != TicketStatus.UNUSED) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        ticket.setTicketStatus(TicketStatus.VALIDATED);
        ticketRepo.save(ticket);

        TicketValidation ticketValidation = new TicketValidation();
        ticketValidation.setTicket(ticket);
        ticketValidation.setStaffUsername(staffUsername);
        ticketValidation.setEntryMethod(Method.QR_SCAN);
        ticketValidation.setValidatedAt(LocalDateTime.now());
        ticketValidation.setValidationId(UUID.randomUUID().toString());

        TicketValidation saved =
                ticketValidationService.createValidTicket(ticketValidation);

        return ResponseEntity.ok(saved);
    }

    @GetMapping("/ticket/{ticketCode}")
    public ResponseEntity<TicketInfoDto> getTicketInfoDto(@PathVariable String ticketCode) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Ticket ticket = ticketPurchaseService.getTicketByTicketCode(ticketCode);
        if (ticket == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Events event = ticket.getEvent();
        boolean isStaff = roleCheckService.StaffCheck();
        boolean isOrganizer = roleCheckService.OrganizerCheck();
        boolean isAttendee = roleCheckService.AttendeeCheck();
        boolean organizerOwnsEvent = isOrganizer && event.getOrganizerUsername().equals(username);

        boolean attendeeOwnsTicket = isAttendee && ticket.getAttendeeUsername().equals(username);

        if (!(isStaff || organizerOwnsEvent || attendeeOwnsTicket)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        TicketInfoDto ticketInfoDto = new TicketInfoDto();
        ticketInfoDto.setEvent_name(event.getTitle());
        ticketInfoDto.setTicketStatus(ticket.getTicketStatus());
        ticketInfoDto.setAttendeeUsername(ticket.getAttendeeUsername());

        return ResponseEntity.ok(ticketInfoDto);
    }

    @GetMapping("/events/{eventId}/validated-tickets")
    public ResponseEntity<List<TicketValidation>> getValidatedTickets(@PathVariable Long eventId) {

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

        List<TicketValidation> validatedTickets =
                ticketValidationService.getValidatedTicketsByEvent(event);

        return ResponseEntity.ok(validatedTickets);
    }

}
