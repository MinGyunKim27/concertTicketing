package org.example.concertTicketing.domain.concert.dto.response;

import java.time.LocalDateTime;

public interface ConcertWithRemainingTicketsProjection {
    Long getId();
    String getConcertName();
    LocalDateTime getDate();
    String getVenue();
    Long getRemainingTickets();
}

