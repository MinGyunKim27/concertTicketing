package org.example.concertTicketing.domain.ticket.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.concertTicketing.domain.concert.entity.Concert;
import org.example.concertTicketing.domain.concert.repository.ConcertRepository;
import org.example.concertTicketing.domain.order.entity.Order;
import org.example.concertTicketing.domain.order.repository.OrderRepository;
import org.example.concertTicketing.domain.redis.service.RedisService;
import org.example.concertTicketing.domain.seat.entity.Seat;
import org.example.concertTicketing.domain.seat.repository.SeatRepository;
import org.example.concertTicketing.domain.ticket.dto.request.TicketReserveRequestDto;
import org.example.concertTicketing.domain.ticket.dto.response.TicketReserveResponseDto;
import org.example.concertTicketing.domain.ticket.entity.Ticket;
import org.example.concertTicketing.domain.ticket.repository.TicketRepository;
import org.example.concertTicketing.domain.user.entity.User;
import org.example.concertTicketing.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.example.concertTicketing.domain.ticket.entity.QTicket.ticket;
import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.in;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ConcertRepository concertRepository;
    @Mock
    private SeatRepository seatRepository;
    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private TicketService ticketService;

    @Test
    @DisplayName("티켓 예매 성공")
    void testReserveTickets_Success() {
        // Given
        Long userId = 1L;
        Long concertId = 100L;
        List<Long> seatIds = List.of(10L, 20L);
        TicketReserveRequestDto dto = new TicketReserveRequestDto(seatIds);

        User user = mock(User.class);
        Concert concert = mock(Concert.class);
        Seat seat1 = new Seat(); seat1.setId(10L);
        Seat seat2 = new Seat(); seat2.setId(20L);

        Order mockOrder = mock(Order.class);
        Ticket ticket1 = Ticket.reserve(mockOrder, seat1, LocalDateTime.now());
        Ticket ticket2 = Ticket.reserve(mockOrder, seat2, LocalDateTime.now());

        // Repository Mocking
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(concertRepository.findById(concertId)).willReturn(Optional.of(concert));
        given(ticketRepository.findReservedSeatIds(concertId, seatIds)).willReturn(List.of());
        given(seatRepository.findAllById(seatIds)).willReturn(List.of(seat1, seat2));
        given(orderRepository.save(org.mockito.ArgumentMatchers.any(Order.class))).willReturn(mockOrder);
        given(ticketRepository.saveAll(org.mockito.ArgumentMatchers.anyList()))
                .willReturn(List.of(ticket1, ticket2));

        // When
        List<Ticket> tickets = ticketService.reserveTickets(userId, concertId, dto);

        // Then
        assertThat(tickets).isNotNull();
        assertThat(tickets.size()).isEqualTo(2);
        verify(ticketRepository).saveAll(org.mockito.ArgumentMatchers.anyList());
    }

    @Test
    @DisplayName("좌석이 이미 예매된 경우 예외 발생")
    void testReserveTickets_SeatAlreadyReserved() {
        // Given
        Long userId = 1L;
        Long concertId = 100L;
        List<Long> seatIds = List.of(10L);
        TicketReserveRequestDto dto = new TicketReserveRequestDto(seatIds);

        given(userRepository.findById(userId)).willReturn(Optional.of(mock(User.class)));
        given(concertRepository.findById(concertId)).willReturn(Optional.of(mock(Concert.class)));
        given(ticketRepository.findReservedSeatIds(concertId, seatIds)).willReturn(List.of(10L));

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            ticketService.reserveTickets(userId, concertId, dto);
        });

        assertThat(exception.getMessage()).contains("이미 선택된 좌석이 있습니다");
    }

    @Test
    @DisplayName("사용자가 존재하지 않는 경우 예외 발생")
    void testReserveTickets_UserNotFound() {
        // Given
        Long userId = 1L;
        Long concertId = 100L;
        List<Long> seatIds = List.of(10L);
        TicketReserveRequestDto dto = new TicketReserveRequestDto(seatIds);

        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            ticketService.reserveTickets(userId, concertId, dto);
        });

        assertThat(exception.getMessage()).contains("해당 회원을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("일부 좌석이 존재하지 않는 경우 예외 발생")
    void testReserveTickets_SeatNotFound() {
        // Given
        Long userId = 1L;
        Long concertId = 100L;
        List<Long> seatIds = List.of(10L, 20L);
        TicketReserveRequestDto dto = new TicketReserveRequestDto(seatIds);

        given(userRepository.findById(userId)).willReturn(Optional.of(mock(User.class)));
        given(concertRepository.findById(concertId)).willReturn(Optional.of(mock(Concert.class)));
        given(ticketRepository.findReservedSeatIds(concertId, seatIds)).willReturn(List.of());
        given(seatRepository.findAllById(seatIds)).willReturn(List.of(new Seat())); // 하나만 조회됨

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            ticketService.reserveTickets(userId, concertId, dto);
        });

        assertThat(exception.getMessage()).contains("좌석 일부가 존재하지 않습니다.");
    }
}
