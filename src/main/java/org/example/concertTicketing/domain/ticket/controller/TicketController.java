package org.example.concertTicketing.domain.ticket.controller;

import lombok.RequiredArgsConstructor;
import org.example.concertTicketing.domain.ticket.dto.request.TicketReserveRequestDto;
import org.example.concertTicketing.domain.ticket.dto.response.ApiResponse;
import org.example.concertTicketing.domain.ticket.dto.response.TicketResponseDto;
import org.example.concertTicketing.domain.ticket.entity.Ticket;
import org.example.concertTicketing.domain.ticket.repository.TicketRepository;
import org.example.concertTicketing.domain.ticket.service.TicketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TicketController {

    private final TicketService ticketService;

    @PostMapping("/concerts/{concertId}/tickets")
    public ResponseEntity<ApiResponse<List<TicketResponseDto>>> reserveTickets(
            @PathVariable Long concertId,
            @RequestBody TicketReserveRequestDto request,
            @RequestHeader("X-USER-ID") Long userId     // 임시 테스트용, jwt 도입 후 수정 예정
            ) {
        List<TicketResponseDto> reserved = ticketService.reserveTicketsService(userId, concertId, request);
        return ResponseEntity.ok(ApiResponse.success("콘서트 예매에 성공했습니다.", reserved));
    }
}
