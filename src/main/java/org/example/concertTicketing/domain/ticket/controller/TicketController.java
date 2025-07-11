package org.example.concertTicketing.domain.ticket.controller;

import lombok.RequiredArgsConstructor;
import org.example.concertTicketing.domain.redis.service.RedisService;
import org.example.concertTicketing.domain.seat.dto.response.SeatLockRequestDto;
import org.example.concertTicketing.domain.seat.dto.response.SeatStatusDto;
import org.example.concertTicketing.domain.ticket.dto.request.TicketReserveRequestDto;
import org.example.concertTicketing.domain.ticket.dto.response.*;
import org.example.concertTicketing.domain.ticket.entity.Ticket;
import org.example.concertTicketing.domain.ticket.service.TicketService;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;

import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TicketController {

    private final TicketService ticketService;
    private final RedisService redisService;

    // 티켓 예매
    @PostMapping("/concerts/{concertId}/tickets")
    public ResponseEntity<ApiResponse<TicketReserveResponseDto>> reserveTickets(
            @PathVariable Long concertId,
            @RequestBody TicketReserveRequestDto request,
            @AuthenticationPrincipal Long userId
            ) {
        List<Ticket> tickets = ticketService.reserveTickets(userId, concertId, request);
        TicketReserveResponseDto response = TicketReserveResponseDto.of(tickets, concertId);
        return ResponseEntity.ok(ApiResponse.success("콘서트 예매에 성공했습니다.", response));
    }

    // 티켓 예매 조회
    @GetMapping("/users/my/tickets")
    public ResponseEntity<ApiResponse<TicketListResponseDto>> getMyTickets(
            @AuthenticationPrincipal Long userId,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        Page<TicketResponseDto> page = ticketService.getUserTickets(userId, pageable);
        TicketListResponseDto data = TicketListResponseDto.of(page);

        return ResponseEntity.ok(ApiResponse.success("티켓 조회가 완료되었습니다.", data));
    }

    // 티켓 예매 취소
    @DeleteMapping("/users/my/tickets/{orderId}")
    public ResponseEntity<ApiResponse<TicketCancelResponseDto>> cancelTicket(@PathVariable Long orderId) {
        List<Ticket> tickets = ticketService.cancelTicket(orderId);
        TicketCancelResponseDto response = TicketCancelResponseDto.of(tickets);
        return ResponseEntity.ok(ApiResponse.success("주문 취소에 성공했습니다.", response));
    }

    // 좌석 선택 시 -> Redis에 Lock 저장
    @PostMapping("/concerts/{concertId}/seats/lock")
    public ResponseEntity<ApiResponse<String>> lockSeats(
            @PathVariable Long concertId,
            @RequestBody SeatLockRequestDto requestDto,
            @AuthenticationPrincipal Long userId
    ) {
       redisService.lockSeats(userId, concertId, requestDto.seatIds());

       return ResponseEntity.ok(ApiResponse.success("좌석이 임시로 예약되었습니다."));
    }

    // 클라이언트에서 조회할 수 있도록
    @GetMapping("/concerts/{concertId}/seats")
    public ResponseEntity<ApiResponse<List<SeatStatusDto>>> getSeatStatuses(
            @PathVariable Long concertId,
            @RequestParam String rowLabel
    ) {
        List<SeatStatusDto> seatStatuses = ticketService.getSeatStatusesWithLock(concertId, rowLabel);
        return ResponseEntity.ok(ApiResponse.success("좌석 상태 조회 성공", seatStatuses));
    }
    // => 테스트 예시
    //누군가 seatId = 10에 대해 Redis 락을 잡으면
    // /concerts/{concertId}/seats?rowLabel=A 조회 시 해당 좌석은 isReserved: true로 표시됨
    //Redis TTL 5분 지나면 자동으로 락 풀림 → 다시 선택 가능
}
