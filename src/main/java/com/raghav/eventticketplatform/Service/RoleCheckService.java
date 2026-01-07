package com.raghav.eventticketplatform.Service;


import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class RoleCheckService {
    public boolean OrganizerCheck(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        authentication.getAuthorities()
                .forEach(a -> System.out.println("AUTHORITY = " + a.getAuthority()));


        boolean isOrganizer = authentication.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ORGANIZER"));
        return isOrganizer;
    }
    public boolean AttendeeCheck(){
        Authentication authentication =SecurityContextHolder.getContext().getAuthentication();
        boolean isAttendee = authentication.getAuthorities()
                .stream()
                .anyMatch(a->a.getAuthority().equals("ROLE_ATTENDEE"));
        return isAttendee;
    }
}
