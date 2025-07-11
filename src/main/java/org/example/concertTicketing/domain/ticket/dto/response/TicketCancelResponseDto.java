package org.example.concertTicketing.domain.ticket.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.concertTicketing.domain.ticket.entity.Ticket;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class TicketCancelResponseDto {
    private String message;
    private Long orderNo;
    private Long concertId;

    public static TicketCancelResponseDto of(List<Ticket> tickets) {
        if (tickets.isEmpty()) {
            throw new IllegalArgumentException("취소된 티켓이 없습니다.");
        }

        Ticket ticket = tickets.get(0);

        return new TicketCancelResponseDto(
                "Tickets canceled successfully",
                ticket.getOrder().getId(),
                ticket.getOrder().getConcert().getId()
        );
    }
}
