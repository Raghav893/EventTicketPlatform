package com.raghav.eventticketplatform.Service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class RoleCheckService {
    public boolean OrganizerCheck(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isOrganizer = authentication.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ORGANIZER"));
        return isOrganizer;
    }
}
