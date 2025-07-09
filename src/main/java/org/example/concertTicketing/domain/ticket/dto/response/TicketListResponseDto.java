package org.example.concertTicketing.domain.ticket.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.concertTicketing.domain.ticket.entity.Ticket;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class TicketListResponseDto {
    private List<TicketResponseDto> tickets;
    private long totalElements;
    private int totalPages;
    private int size;
    private int number;

    public static TicketListResponseDto of(Page<TicketResponseDto> ticketPage) {
        return new TicketListResponseDto(
                ticketPage.getContent(),
                ticketPage.getTotalElements(),
                ticketPage.getTotalPages(),
                ticketPage.getSize(),
                ticketPage.getNumber());
    }
}
