package org.example.concertTicketing.domain.ticket.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.concertTicketing.domain.ticket.entity.Ticket;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
@NoArgsConstructor
public class TicketReserveResponseDto {
    private String message;
    private List<Long> ticketIds;
    private Long concertId;


    public static TicketReserveResponseDto of(List<Ticket> tickets, Long concertId) {
        List<Long> ids = tickets.stream()
                .map(Ticket::getId)
                .collect(Collectors.toList());

        return new TicketReserveResponseDto("Tickets reserved successfully", ids, concertId);
    }
}
