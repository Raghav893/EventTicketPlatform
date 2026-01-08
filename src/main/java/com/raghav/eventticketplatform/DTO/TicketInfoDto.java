package com.raghav.eventticketplatform.DTO;

import com.raghav.eventticketplatform.Entity.TicketStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class TicketInfoDto {
    @Enumerated(EnumType.STRING)
    TicketStatus ticketStatus;

    String attendeeUsername;

    String event_name;
}
