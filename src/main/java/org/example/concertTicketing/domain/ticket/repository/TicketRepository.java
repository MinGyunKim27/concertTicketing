package org.example.concertTicketing.domain.ticket.repository;

import org.example.concertTicketing.domain.ticket.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByUserId(Long userId);
    Optional<Ticket> findByOrderId(Long orderId);
    boolean existsBySeatIdAndConcertIdAndCanceledAtIsNull(Long seatId, Long concertId);
}
