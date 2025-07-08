package org.example.concertTicketing.domain.ticket.dto.request;

import java.util.List;

public record TicketReserveRequestDto(
        Long orderNo,
        List<Long> seatIds
) {}
