package com.raghav.eventticketplatform.Repo;

import com.raghav.eventticketplatform.Entity.EventStatus;
import com.raghav.eventticketplatform.Entity.Events;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepo extends JpaRepository<Events,Long> {
    List<Events> findByOrganizerUsername(String organizerUsername);

    List<Events> findByOrganizerUsernameAndStatus(String organizerUsername, EventStatus status);

    List<Events> findByStatus(EventStatus status);

    List<Events> findEventsByStatusAndEventId(EventStatus status, Long eventId);
}
