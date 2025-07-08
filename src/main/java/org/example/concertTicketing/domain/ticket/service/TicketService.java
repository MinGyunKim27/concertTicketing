package org.example.concertTicketing.domain.ticket.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.concertTicketing.domain.concert.entity.Concert;
import org.example.concertTicketing.domain.concert.repository.ConcertRepository;
import org.example.concertTicketing.domain.seat.entity.Seat;
import org.example.concertTicketing.domain.seat.repository.SeatRepository;
import org.example.concertTicketing.domain.ticket.dto.request.TicketReserveRequestDto;
import org.example.concertTicketing.domain.ticket.dto.response.TicketResponseDto;
import org.example.concertTicketing.domain.ticket.entity.Ticket;
import org.example.concertTicketing.domain.ticket.repository.TicketRepository;
import org.example.concertTicketing.domain.user.entity.User;
import org.example.concertTicketing.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final ConcertRepository concertRepository;
    private final SeatRepository seatRepository;

    @Transactional
    public List<TicketResponseDto> reserveTicketsService(Long userId, Long concertId, TicketReserveRequestDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("해당 회원을 찾을 수 없습니다."));

        Concert concert = concertRepository.findById(concertId)
                .orElseThrow(() -> new EntityNotFoundException("해당 콘서트를 찾을 수 없습니다."));

        List<TicketResponseDto> reserved = new ArrayList<>();

        for (Long seatId : dto.seatIds()) {
            if (ticketRepository.existsBySeatIdAndConcertIdAndCanceledAtIsNull(seatId, concertId)) {
                throw new IllegalStateException("이미 선택된 좌석입니다." + seatId);
            }

            Seat seat = seatRepository.findById(seatId)
                    .orElseThrow(() -> new EntityNotFoundException("해당 좌석을 찾을 수 없습니다."));

            Ticket ticket = Ticket.reserve(user, concert, seat, dto.orderNo());
            reserved.add(TicketResponseDto.from(ticketRepository.save(ticket)));
        }
        return reserved;
    }


}
