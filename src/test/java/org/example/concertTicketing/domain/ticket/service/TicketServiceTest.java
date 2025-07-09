package org.example.concertTicketing.domain.ticket.service;

import org.example.concertTicketing.domain.concert.entity.Concert;
import org.example.concertTicketing.domain.concert.repository.ConcertRepository;
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

    @InjectMocks
    private TicketService ticketService;

    @Test
    @DisplayName("티켓 예매")
    void testGetTicket() {
        // Given
        Long userId = 1L;
        Long concertId = 1L;
        List<Long> seatIds = List.of(10L, 20L);
        TicketReserveRequestDto dto = new TicketReserveRequestDto(seatIds);

        User user = mock(User.class);
        Concert concert = mock(Concert.class);

        Seat seat1 = new Seat();
        Seat seat2 = new Seat();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(concertRepository.findById(concertId)).willReturn(Optional.of(concert));

        given(ticketRepository.existsBySeatIdAndConcertIdAndCanceledAtIsNull(10L, concertId)).willReturn(false);
        given(ticketRepository.existsBySeatIdAndConcertIdAndCanceledAtIsNull(20L, concertId)).willReturn(false);

        given(seatRepository.findById(10L)).willReturn(Optional.of(seat1));
        given(seatRepository.findById(20L)).willReturn(Optional.of(seat2));

        // When
       // TicketReserveResponseDto response = ticketService.reserveTicketsService(userId, concertId, dto);

        // Then
       // assertThat(response).isNotNull();
    }



}