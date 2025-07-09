package org.example.concertTicketing.domain.ticket.controller;

import lombok.RequiredArgsConstructor;
import org.example.concertTicketing.domain.ticket.dto.request.TicketReserveRequestDto;
import org.example.concertTicketing.domain.ticket.dto.response.*;
import org.example.concertTicketing.domain.ticket.entity.Ticket;
import org.example.concertTicketing.domain.ticket.repository.TicketRepository;
import org.example.concertTicketing.domain.ticket.service.TicketService;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TicketController {

    private final TicketService ticketService;

    @PostMapping("/concerts/{concertId}/tickets")
    public ResponseEntity<ApiResponse<TicketReserveResponseDto>> reserveTickets(
            @PathVariable Long concertId,
            @RequestBody TicketReserveRequestDto request,
            @RequestHeader("X-USER-ID") Long userId    // 임시 테스트용, jwt 도입 후 수정 예정
            ) {
        TicketReserveResponseDto response = ticketService.reserveTicketsService(userId, concertId, request);
        return ResponseEntity.ok(ApiResponse.success("콘서트 예매에 성공했습니다.", response));
    }

    @GetMapping("/users/my/tickets")
    public ResponseEntity<ApiResponse<TicketListResponseDto>> getMyTickets(
            @RequestHeader("X-USER-ID")  Long userId,
            @PageableDefault(page = 0, size = 10) Pageable pageable
    ) {
        TicketListResponseDto response = ticketService.getUserTicketsService(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success("티켓 조회가 완료되었습니다.", response));
    }

    @DeleteMapping("/users/my/tickets/{orderId}")
    public ResponseEntity<ApiResponse<TicketCancelResponseDto>> cancelTicket(@PathVariable Long orderId) {
        TicketCancelResponseDto response = ticketService.cancelTicketService(orderId);
        return ResponseEntity.ok(ApiResponse.success("주문 취소에 성공했습니다.", response));
    }
}
