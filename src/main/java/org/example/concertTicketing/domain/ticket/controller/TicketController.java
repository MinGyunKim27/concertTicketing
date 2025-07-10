package org.example.concertTicketing.domain.ticket.controller;

import lombok.RequiredArgsConstructor;
import org.example.concertTicketing.domain.ticket.dto.request.TicketReserveRequestDto;
import org.example.concertTicketing.domain.ticket.dto.response.*;
import org.example.concertTicketing.domain.ticket.entity.Ticket;
import org.example.concertTicketing.domain.ticket.repository.TicketRepository;
import org.example.concertTicketing.domain.ticket.service.TicketService;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TicketController {

    private final TicketService ticketService;

    @PostMapping("/concerts/{concertId}/tickets")
    public ResponseEntity<ApiResponse<TicketReserveResponseDto>> reserveTickets(
            @PathVariable Long concertId,
            @RequestBody TicketReserveRequestDto request,
            @AuthenticationPrincipal Long userId
            ) {
        List<Ticket> tickets = ticketService.reserveTicketsService(userId, concertId, request);
        TicketReserveResponseDto response = TicketReserveResponseDto.of(tickets, concertId);
        return ResponseEntity.ok(ApiResponse.success("콘서트 예매에 성공했습니다.", response));
    }

    @GetMapping("/users/my/tickets")
    public ResponseEntity<ApiResponse<TicketListResponseDto>> getMyTickets(
            @AuthenticationPrincipal Long userId,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        Page<TicketResponseDto> page = ticketService.getUserTicketsService(userId, pageable);
        TicketListResponseDto data = TicketListResponseDto.of(page);

        return ResponseEntity.ok(ApiResponse.success("티켓 조회가 완료되었습니다.", data));
    }

    @DeleteMapping("/users/my/tickets/{orderId}")
    public ResponseEntity<ApiResponse<TicketCancelResponseDto>> cancelTicket(@PathVariable Long orderId) {
        Ticket ticket = ticketService.cancelTicketService(orderId);
        TicketCancelResponseDto response = TicketCancelResponseDto.of(ticket);
        return ResponseEntity.ok(ApiResponse.success("주문 취소에 성공했습니다.", response));
    }
}
