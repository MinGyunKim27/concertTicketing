package org.example.concertTicketing.domain.ticket.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.concertTicketing.domain.concert.entity.Concert;
import org.example.concertTicketing.domain.concert.repository.ConcertRepository;
import org.example.concertTicketing.domain.order.entity.Order;
import org.example.concertTicketing.domain.order.repository.OrderRepository;
import org.example.concertTicketing.domain.redis.service.RedisService;
import org.example.concertTicketing.domain.seat.entity.Seat;
import org.example.concertTicketing.domain.seat.repository.SeatRepository;
import org.example.concertTicketing.domain.ticket.dto.request.TicketReserveRequestDto;
import org.example.concertTicketing.domain.ticket.entity.Ticket;
import org.example.concertTicketing.domain.ticket.repository.TicketRepository;
import org.example.concertTicketing.domain.user.entity.User;
import org.example.concertTicketing.domain.user.repository.UserRepository;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketCommandService {

    // 예매 시 필요한 레포지토리 주입
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final ConcertRepository concertRepository;
    private final SeatRepository seatRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public List<Ticket> reserveTickets(Long userId, Long concertId, TicketReserveRequestDto dto) {
        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("해당 회원을 찾을 수 없습니다."));

        // 콘서트 조회
        Concert concert = concertRepository.findById(concertId)
                .orElseThrow(() -> new EntityNotFoundException("해당 콘서트를 찾을 수 없습니다."));

        // 중복된 좌석 예약 여부 확인
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
}
