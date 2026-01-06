package com.raghav.eventticketplatform.Service;

import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.raghav.eventticketplatform.Entity.EventStatus;
import com.raghav.eventticketplatform.Entity.Events;
import com.raghav.eventticketplatform.Repo.EventRepo;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EventServices {
    @Autowired
    EventRepo eventRepo;

    public List<Events> getAllEvents(){
        return eventRepo.findAll();
    }
    public Events createEvent(Events event,String username) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();



        event.setOrganizerUsername(username);
        event.setCreatedAt(LocalDateTime.now());
        event.setUpdatedAt(LocalDateTime.now());
        event.setStatus(EventStatus.DRAFT);

        return eventRepo.save(event);
    }


    public void deleteEvent(Long id) {
        eventRepo.deleteById(id);
    }
    public Optional<Events> getEventById(Long EventId){
        return eventRepo.findById(EventId);
    }
    public List<Events> getEventByUsername(String username){
        return eventRepo.findByOrganizerUsername(username);
    }
    public List<Events> getEventByUsernameAndStatus(String username,EventStatus status){
        return eventRepo.findByOrganizerUsernameAndStatus(username,status);
    }
    public List<Events> getPublishedEvents(){
        return eventRepo.findByStatus(EventStatus.PUBLISHED);
    }
    public List<Events> getPublishedEventsByid(Long Id){
        return eventRepo.findEventsByStatusAndEventId(EventStatus.PUBLISHED,Id);
    }
    public Events updateEvent(Events existingEvent, Events updatedEvent) {

        existingEvent.setTitle(updatedEvent.getTitle());
        existingEvent.setDescription(updatedEvent.getDescription());
        existingEvent.setLocation(updatedEvent.getLocation());
        existingEvent.setDateTime(updatedEvent.getDateTime());
        existingEvent.setStatus(updatedEvent.getStatus());
        existingEvent.setUpdatedAt(LocalDateTime.now());

        return eventRepo.save(existingEvent);
    }


}
