package org.example.concertTicketing.domain.ticket.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.concertTicketing.domain.concert.entity.Concert;
import org.example.concertTicketing.domain.concert.repository.ConcertRepository;
import org.example.concertTicketing.domain.order.entity.Order;
import org.example.concertTicketing.domain.order.repository.OrderRepository;
import org.example.concertTicketing.domain.redis.service.RedisService;
import org.example.concertTicketing.domain.seat.dto.response.SeatStatusDto;
import org.example.concertTicketing.domain.seat.dto.response.SeatStatusProjection;
import org.example.concertTicketing.domain.seat.entity.Seat;
import org.example.concertTicketing.domain.seat.repository.SeatRepository;
import org.example.concertTicketing.domain.ticket.dto.request.TicketReserveRequestDto;
import org.example.concertTicketing.domain.ticket.dto.response.TicketResponseDto;
import org.example.concertTicketing.domain.ticket.entity.Ticket;
import org.example.concertTicketing.domain.ticket.repository.TicketRepository;
import org.example.concertTicketing.domain.user.entity.User;
import org.example.concertTicketing.domain.user.repository.UserRepository;
import org.redisson.api.RedissonClient;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

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

    // redis
    private final RedisService redisService;
    private final StringRedisTemplate redisTemplate;
    private final RedissonClient redissonClient;

    // 티켓 예매 서비스
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
    // 티켓 예매 서비스
    @Transactional
    public List<Ticket> reserveTicketsLettuce(Long userId, Long concertId, TicketReserveRequestDto dto) {
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

        // 결제 성공 시 -> Redis 락 해제
        for (Long seatId : dto.seatIds()) {
            String key = "seat:lock:" + seatId;
            redisService.unlock(key, String.valueOf(userId));
        }

        return tickets;
    }
    // 티켓 예매 서비스
    @Transactional
    public List<Ticket> reserveTicketsRedisson(Long userId, Long concertId, TicketReserveRequestDto dto) {
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

        // 결제 성공 시 -> Redis 락 해제
        for (Long seatId : dto.seatIds()) {
            String key = "seat:lock:" + seatId;
            redisService.unlock(key, String.valueOf(userId));
        }

        return tickets;
    }

    // 티켓 예매 조회 서비스
    @Transactional(readOnly = true)
    public Page<TicketResponseDto> getUserTickets(Long userId, Pageable pageable) {
        return ticketRepository.findByUserId(userId, pageable)
                .map(TicketResponseDto::from);
    }

    // 티켓 예매 취소 서비스
    @Transactional
    public List<Ticket> cancelTicket(Long orderId) {
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
    // 티켓 예매 취소 서비스
    @Transactional
    public List<Ticket> cancelTicketLettuce(Long orderId) {
        List<Ticket> tickets = ticketRepository.findAllByOrderId(orderId);

        if (tickets.isEmpty()) {
            throw new EntityNotFoundException("예매 내역이 없습니다.");
        };

        if (tickets.get(0).isCanceled()) {
            throw new IllegalStateException("이미 취소된 티켓입니다.");
        }

        for (Ticket ticket : tickets) {
            // 결제 실패 or 취소 시 -> redis 락 수동 해제
            Long seatId = ticket.getSeat().getId();
            String key = "seat:lock:" + seatId;
            redisService.unlock(key, String.valueOf(ticket.getOrder().getUser().getId()));
        }
        return tickets;
    }

    // 좌석 조회 시 -> redis 락 여부 확인 후 상태 반영
    // :: Redis에서 락 여부도 함께 확인해서 클라이언트에 전달
    // 좌석 상태 - Redis 락 여부 확인 서비스
    public List<SeatStatusDto> getSeatStatusesWithLock(Long concertId, String rowLabel) {
        List<SeatStatusProjection> seats = seatRepository.findSeatStatusesByConcertIdAndRowLabel(concertId, rowLabel);

        return seats.stream()
                .map(seat -> {
                    String key = "seat:lock:" + seat.getSeatId();
                    boolean isLocked = redisTemplate.hasKey(key); // Redis 락 여부 확인
                    boolean isReserved = seat.getIsReserved() || isLocked; // DB 예약 or Redis 락

                    return new SeatStatusDto(
                            seat.getSeatId(),
                            seat.getRowLabel(),
                            seat.getColumnNumber(),
                            seat.getSeatLabel(),
                            seat.getPrice(),
                            isReserved
                    );
                })
                .toList();
    }
}
