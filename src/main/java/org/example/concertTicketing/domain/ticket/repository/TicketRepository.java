package org.example.concertTicketing.domain.ticket.repository;

import org.example.concertTicketing.domain.ticket.entity.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Page<Ticket> findByUserId(Long userId, Pageable pageable);
    Optional<Ticket> findByOrderId(Long orderId);
    boolean existsBySeatIdAndConcertIdAndCanceledAtIsNull(Long seatId, Long concertId);
}
