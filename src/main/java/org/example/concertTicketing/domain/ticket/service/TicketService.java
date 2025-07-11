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
import org.redisson.RedissonMultiLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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

    @Transactional
    public List<Ticket> reserveTicketsLettuce(Long userId, Long concertId, TicketReserveRequestDto dto) {
        List<Long> seatIds = dto.seatIds();
        String lockOwner = UUID.randomUUID().toString(); // 락 주인 식별용

        try {
            // 1. 분산 락 획득
            for (Long seatId : seatIds) {
                String key = "lock:concert:" + concertId + ":seat:" + seatId;
                boolean locked = redisService.lock(key, lockOwner, 10_000);
                if (!locked) {
                    throw new IllegalStateException("이미 예약 중인 좌석입니다: seatId=" + seatId);
                }
            }

            // 2. 사용자, 콘서트, 좌석 정보 조회
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("해당 회원을 찾을 수 없습니다."));

            Concert concert = concertRepository.findById(concertId)
                    .orElseThrow(() -> new EntityNotFoundException("해당 콘서트를 찾을 수 없습니다."));

            List<Long> reservedSeatIds = ticketRepository.findReservedSeatIds(concertId, seatIds);
            if (!reservedSeatIds.isEmpty()) {
                throw new IllegalStateException("이미 예약된 좌석이 있습니다: " + reservedSeatIds);
            }

            List<Seat> seats = seatRepository.findAllById(seatIds);
            if (seats.size() != seatIds.size()) {
                throw new EntityNotFoundException("좌석 일부가 존재하지 않습니다.");
            }

            // 3. 주문 및 티켓 저장
            Order order = orderRepository.save(Order.create(user, concert, LocalDateTime.now()));
            LocalDateTime now = LocalDateTime.now();

            List<Ticket> tickets = seats.stream()
                    .map(seat -> Ticket.reserve(order, seat, now))
                    .toList();

            return ticketRepository.saveAll(tickets);

        } finally {
            // 4. 락 해제
            for (Long seatId : seatIds) {
                String key = "lock:concert:" + concertId + ":seat:" + seatId;
                redisService.unlock(key, lockOwner);
            }
        }
    }

    // 티켓 예매 서비스
    @Transactional
    public List<Ticket> reserveTicketsRedisson(Long userId, Long concertId, TicketReserveRequestDto dto) throws InterruptedException {
        List<Long> seatIds = dto.seatIds();

        // 1. MultiLock 생성
        List<RLock> lockList = seatIds.stream()
                .map(seatId -> redissonClient.getLock("lock:concert:" + concertId + ":seat:" + seatId))
                .toList();
        RLock multiLock = new RedissonMultiLock(lockList.toArray(new RLock[0]));

        boolean locked = false;
        try {
            // 2. 모든 좌석 락 획득 시도
            locked = multiLock.tryLock(0, 10, TimeUnit.SECONDS); // 대기 0초, 점유 10초
            if (!locked) {
                throw new IllegalStateException("일부 좌석이 현재 예약 중입니다.");
            }

            // 3. 사용자 조회
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("해당 회원을 찾을 수 없습니다."));

            // 4. 콘서트 조회
            Concert concert = concertRepository.findById(concertId)
                    .orElseThrow(() -> new EntityNotFoundException("해당 콘서트를 찾을 수 없습니다."));

            // 5. 중복된 좌석 예약 여부 확인
            List<Long> reservedSeatIds = ticketRepository.findReservedSeatIds(concertId, seatIds);
            if (!reservedSeatIds.isEmpty()) {
                throw new IllegalStateException("이미 예약된 좌석이 있습니다: " + reservedSeatIds);
            }

            // 6. 좌석 정보 조회
            List<Seat> seats = seatRepository.findAllById(seatIds);
            if (seats.size() != seatIds.size()) {
                throw new EntityNotFoundException("좌석 일부가 존재하지 않습니다.");
            }

            // 7. 주문 및 티켓 저장
            Order order = orderRepository.save(Order.create(user, concert, LocalDateTime.now()));
            LocalDateTime now = LocalDateTime.now();

            List<Ticket> tickets = seats.stream()
                    .map(seat -> Ticket.reserve(order, seat, now))
                    .toList();

            return ticketRepository.saveAll(tickets);

        } finally {
            // 8. 락 해제
            if (locked) {
                multiLock.unlock();
            }
        }
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
}
