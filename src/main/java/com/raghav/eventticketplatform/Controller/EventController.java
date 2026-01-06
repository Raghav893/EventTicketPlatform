package com.raghav.eventticketplatform.Controller;

import com.raghav.eventticketplatform.Entity.EventStatus;
import com.raghav.eventticketplatform.Entity.Events;
import com.raghav.eventticketplatform.Service.EventServices;
import com.raghav.eventticketplatform.Service.RoleCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController

@RequestMapping("/events")
public class EventController {
    @Autowired
    private final EventServices eventServices;

    @Autowired
    private final RoleCheckService roleCheckService;

    public EventController(EventServices eventServices, RoleCheckService roleCheckService) {
        this.eventServices = eventServices;
        this.roleCheckService = roleCheckService;
    }

    @PostMapping("/create")
    public ResponseEntity<Events> CreateEvents(@RequestBody Events events) {
        boolean isOrganizer = roleCheckService.OrganizerCheck();
        if (!isOrganizer){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Events savedEvent = eventServices.createEvent(events,username);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedEvent);


    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {

        boolean isOrganizer = roleCheckService.OrganizerCheck();
        if (!isOrganizer) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Optional<Events> eventOpt = eventServices.getEventById(id);
        if (eventOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Events event = eventOpt.get();

        if (!event.getOrganizerUsername().equals(username)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        eventServices.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<Events>> getEventbyid(@PathVariable Long Id) {
        return new ResponseEntity<>(eventServices.getPublishedEventsByid(Id), HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<List<Events>> getAllEvents() {
        return new ResponseEntity<>(eventServices.getPublishedEvents(), HttpStatus.OK);
    }
    @GetMapping("/my-events")
    public ResponseEntity<List<Events>> getOrganizerEvents(){
        boolean isOrganizer = roleCheckService.OrganizerCheck();
        if (!isOrganizer){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Authentication authentication  = SecurityContextHolder.getContext().getAuthentication();
        String Username = authentication.getName();
        return new ResponseEntity<>(eventServices.getEventByUsername(Username),HttpStatus.OK);
    }
    @PutMapping("/{id}")
    public ResponseEntity<Events> updateEvent(@PathVariable Long id, @RequestBody Events updatedEvent) {

        boolean isOrganizer = roleCheckService.OrganizerCheck();
        if (!isOrganizer) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Optional<Events> eventOpt = eventServices.getEventById(id);
        if (eventOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Events existingEvent = eventOpt.get();

        if (!existingEvent.getOrganizerUsername().equals(username)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Events savedEvent = eventServices.updateEvent(existingEvent, updatedEvent);
        return ResponseEntity.ok(savedEvent);
    }

    @GetMapping("/my-events/{status}")
    public ResponseEntity<List<Events>> getOrganizerEventsByStatus(@PathVariable EventStatus status){
        boolean isOrganizer = roleCheckService.OrganizerCheck();
        if (!isOrganizer){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Authentication authentication  = SecurityContextHolder.getContext().getAuthentication();
        String Username = authentication.getName();
        return  new ResponseEntity<>(eventServices.getEventByUsernameAndStatus(Username,status),HttpStatus.OK);
    }

}
