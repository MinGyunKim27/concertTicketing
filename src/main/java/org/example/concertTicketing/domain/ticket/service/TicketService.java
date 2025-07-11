package org.example.concertTicketing.domain.ticket.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.concertTicketing.domain.concert.entity.Concert;
import org.example.concertTicketing.domain.concert.repository.ConcertRepository;
import org.example.concertTicketing.domain.order.entity.Order;
import org.example.concertTicketing.domain.order.repository.OrderRepository;
import org.example.concertTicketing.domain.seat.entity.Seat;
import org.example.concertTicketing.domain.seat.repository.SeatRepository;
import org.example.concertTicketing.domain.ticket.dto.request.TicketReserveRequestDto;
import org.example.concertTicketing.domain.ticket.dto.response.TicketCancelResponseDto;
import org.example.concertTicketing.domain.ticket.dto.response.TicketListResponseDto;
import org.example.concertTicketing.domain.ticket.dto.response.TicketReserveResponseDto;
import org.example.concertTicketing.domain.ticket.dto.response.TicketResponseDto;
import org.example.concertTicketing.domain.ticket.entity.Ticket;
import org.example.concertTicketing.domain.ticket.repository.TicketRepository;
import org.example.concertTicketing.domain.user.entity.User;
import org.example.concertTicketing.domain.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.time.LocalDateTime.now;

@Service
@RequiredArgsConstructor
public class TicketService {

    // 예매 시 필요한 레포지토리 주입
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final ConcertRepository concertRepository;
    private final SeatRepository seatRepository;
    private final OrderRepository orderRepository;

    // 티켓 예매 서비스
    @Transactional
    public List<Ticket> reserveTicketsService(Long userId, Long concertId, TicketReserveRequestDto dto) {
        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("해당 회원을 찾을 수 없습니다."));

        // 콘서트 조회
        Concert concert = concertRepository.findById(concertId)
                .orElseThrow(() -> new EntityNotFoundException("해당 콘서트를 찾을 수 없습니다."));

        // 중복된 ㅈㅘ석 예약 여부 확인
        List<Long> seatIds = dto.seatIds();
        List<Long> reservedSeatIds = ticketRepository.findReservedSeatIds(concertId, seatIds);
        if (!reservedSeatIds.isEmpty()) {
            throw new IllegalStateException("이미 선택된 좌석이 있습니다: "  + reservedSeatIds);
        }

        // 좌석정보 일괄 조회
        List<Seat> seats = seatRepository.findAllById(seatIds);
        if (seats.size() != seatIds.size()) {
            throw new EntityNotFoundException("좌석 일부가 존재하지 않습니다.");
        }

        // Order 생성 및 저장
        Order order = orderRepository.save(Order.create(user, concert, LocalDateTime.now()));

        // Ticket 엔티티 생성
        LocalDateTime now = LocalDateTime.now();
        List<Ticket> tickets = seats.stream()
                .map(seat -> Ticket.reserve(order, seat, now))
                .toList();

        // Ticket 리스트 저장
        ticketRepository.saveAll(tickets);

        return tickets;

    }

    // 티켓 예매 조회 서비스
    @Transactional(readOnly = true)
    public Page<TicketResponseDto> getUserTicketsService(Long userId, Pageable pageable) {
        return ticketRepository.findByUserId(userId, pageable)
                .map(TicketResponseDto::from);
    }

    // 티켓 예매 취소 서비스
    @Transactional
    public List<Ticket> cancelTicketService(Long orderId) {
        List<Ticket> tickets = ticketRepository.findAllByOrderId(orderId);

        if (tickets.isEmpty()) {
            throw new EntityNotFoundException("예매 내역이 없습니다.");
        };

        if (tickets.get(0).isCanceled()) {
            throw new IllegalStateException("이미 취소된 티켓입니다.");
        }

        for (Ticket ticket : tickets) {
            if (!ticket.isCanceled()) {
                ticket.cancel();
            }
        }
        return tickets;
    }


}
