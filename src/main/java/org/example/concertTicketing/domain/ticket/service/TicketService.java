package org.example.concertTicketing.domain.ticket.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.concertTicketing.domain.concert.entity.Concert;
import org.example.concertTicketing.domain.concert.repository.ConcertRepository;
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
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketService {

    // 예매 시 필요한 레포지토리 주입
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final ConcertRepository concertRepository;
    private final SeatRepository seatRepository;

    @Transactional
    public TicketReserveResponseDto reserveTicketsService(Long userId, Long concertId, TicketReserveRequestDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("해당 회원을 찾을 수 없습니다."));

        Concert concert = concertRepository.findById(concertId)
                .orElseThrow(() -> new EntityNotFoundException("해당 콘서트를 찾을 수 없습니다."));

        List<Ticket> reserved = new ArrayList<>();

        for (Long seatId : dto.seatIds()) {
            if (ticketRepository.existsBySeatIdAndConcertIdAndCanceledAtIsNull(seatId, concertId)) {
                throw new IllegalStateException("이미 선택된 좌석입니다." + seatId);
            }

            Seat seat = seatRepository.findById(seatId)
                    .orElseThrow(() -> new EntityNotFoundException("해당 좌석을 찾을 수 없습니다."));

            Ticket ticket = Ticket.reserve(user, concert, seat);
            reserved.add(ticketRepository.save(ticket));
        }
        return TicketReserveResponseDto.of(reserved, concertId);
    }

    @Transactional(readOnly = true)
    public TicketListResponseDto getUserTicketsService(Long userId, Pageable pageable) {
        Page<Ticket> ticketPage = ticketRepository.findByUserId(userId, pageable);
        return TicketListResponseDto.from(ticketPage);
    }

    @Transactional
    public TicketCancelResponseDto cancelTicketService(Long orderId) {
        Ticket ticket = ticketRepository.findByOrderId(orderId)
                .orElseThrow(() -> new EntityNotFoundException("예매 내역이 없습니다."));

        if (ticket.isCanceled()) {
            throw new IllegalStateException("이미 취소된 티켓입니다.");
        }
        ticket.cancel();
        return TicketCancelResponseDto.of(ticket);
    }


}
