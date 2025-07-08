package org.example.concertTicketing.domain.ticket.dto.response;

import java.time.LocalDateTime;

public record TicketResponseDto(
        Long ticketId,
        Long concertId,
        Long seatId,
        Long orderId,
        LocalDateTime reservedAt
) {

}
