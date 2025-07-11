package org.example.concertTicketing.domain.ticket.dto.response;

import org.example.concertTicketing.domain.ticket.entity.Ticket;

import java.time.LocalDateTime;

public record TicketResponseDto(
        Long ticketId,
        Long concertId,
        Long seatId,
        Long orderId,
        LocalDateTime reservedAt
) {
    public static TicketResponseDto from(Ticket ticket) {
        return new TicketResponseDto(
                ticket.getId(),
                ticket.getOrder().getConcert().getId(),
                ticket.getSeat().getId(),
                ticket.getOrder().getId(),
                ticket.getReservedAt()
        );
    }
}
