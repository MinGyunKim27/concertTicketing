package org.example.concertTicketing.domain.ticket.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.concertTicketing.domain.ticket.entity.Ticket;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class TicketCancelResponseDto {
    private String message;
    private Long orderNo;
    private Long concertId;

    public static TicketCancelResponseDto of(Ticket ticket) {
        return new TicketCancelResponseDto(
                "Tickets canceled successfully",
                ticket.getOrderId(),
                ticket.getConcert().getId()
        );
    }
}
